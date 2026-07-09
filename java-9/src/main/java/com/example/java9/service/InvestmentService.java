package com.example.java9.service;  // 声明服务层包路径

import com.example.java9.model.FinancialProduct;  // 导入理财产品实体
import com.example.java9.model.InvestmentOrder;  // 导入投资订单实体
import com.example.java9.model.InvestmentStatus;  // 导入投资状态枚举（CONFIRMED/REDEEMED）
import com.example.java9.model.ProductStatus;  // 导入产品状态枚举
import com.example.java9.model.TransactionType;  // 导入交易类型枚举
import com.example.java9.model.User;  // 导入用户实体
import com.example.java9.repository.InvestmentOrderRepository;  // 导入投资订单 Repository
import org.springframework.stereotype.Service;  // 导入 Spring Service 注解
import org.springframework.transaction.annotation.Transactional;  // 导入事务注解

import java.math.BigDecimal;  // 导入高精度十进制类，金额与收益率计算必须用 BigDecimal 避免 double 精度丢失
import java.math.RoundingMode;  // 导入舍入模式枚举
import java.time.LocalDateTime;  // 导入时间类
import java.time.format.DateTimeFormatter;  // 导入时间格式化器
import java.util.List;  // 导入集合 List
import java.util.concurrent.ThreadLocalRandom;  // 导入线程本地随机数生成器

/**
 * 理财投资服务
 * <p>
 * 理财投资核心业务，负责用户申购、赎回理财产品及收益计算。
 * 申购时校验产品状态、起投金额、用户余额与实名状态，扣款后创建订单并直接确认计息。
 * 赎回时返还本金与预期收益。所有资金变动通过 {@link TransactionService} 记录流水。
 * </p>
 */
@Service  // 标记为 Spring Service Bean
public class InvestmentService {

    /** 一年的天数基数，用于预期收益按天折算 */
    // 使用 BigDecimal 而非 int：收益计算涉及 BigDecimal.divide，除数必须是 BigDecimal 类型
    private static final BigDecimal YEAR_DAYS = new BigDecimal(365);

    /** 预期收益小数保留位数 */
    private static final int RETURN_SCALE = 2;

    private final InvestmentOrderRepository investmentOrderRepository;  // 投资订单 Repository，final 保证不可变
    private final ProductService productService;  // 产品服务，final 保证不可变
    private final UserService userService;  // 用户服务，final 保证不可变
    private final TransactionService transactionService;  // 交易流水服务，final 保证不可变
    private final RiskService riskService;  // 风控服务，final 保证不可变

    // 构造器注入：保证依赖不可变、显式暴露依赖、便于单元测试，Spring 启动时即可发现循环依赖
    public InvestmentService(InvestmentOrderRepository investmentOrderRepository,
                             ProductService productService,
                             UserService userService,
                             TransactionService transactionService,
                             RiskService riskService) {
        this.investmentOrderRepository = investmentOrderRepository;  // 注入投资订单 Repository
        this.productService = productService;  // 注入产品服务
        this.userService = userService;  // 注入用户服务
        this.transactionService = transactionService;  // 注入交易流水服务
        this.riskService = riskService;  // 注入风控服务
    }

    /**
     * 申购理财产品
     * <p>
     * 校验用户与产品合法性后扣减余额、累加产品已投金额、创建投资订单（直接确认计息），
     * 记录投资流水并触发风控检查。
     * </p>
     *
     * @param userId    用户 ID
     * @param productId 产品 ID
     * @param amount    投资金额
     * @return 已确认的投资订单
     * @throws IllegalArgumentException 用户/产品不存在、低于起投金额或余额不足
     * @throws IllegalStateException    产品不在售或用户未实名
     */
    @Transactional  // 声明事务边界：余额扣减、产品已投金额累加、订单创建、流水记录、风控检查在同一事务，任一失败整体回滚，保证资金与订单一致
    public InvestmentOrder invest(Long userId, Long productId, BigDecimal amount) {
        // 1. 查找用户和产品，不存在则拒绝
        User user = userService.findById(userId);  // 查询用户，不存在则抛异常
        FinancialProduct product = productService.findById(productId);  // 查询产品，不存在则抛异常

        // 2. 校验产品状态为在售
        if (product.getStatus() != ProductStatus.ON_SALE) {  // 非在售状态（下架/售罄）拒绝申购
            throw new IllegalStateException("产品不在售");
        }

        // 3. 校验投资金额不低于起投金额
        // 使用 compareTo 比较金额，避免 BigDecimal equals 因 scale 不同导致判断错误
        if (amount.compareTo(product.getMinAmount()) < 0) {
            throw new IllegalArgumentException("低于起投金额");  // 低于起投门槛快速失败
        }

        // 4. 校验用户余额充足
        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("余额不足");  // 余额不足快速失败
        }

        // 5. 校验用户已完成实名认证
        // 使用 Boolean.TRUE.equals 避免 verified 为 null 时 NPE
        if (!Boolean.TRUE.equals(user.getVerified())) {
            throw new IllegalStateException("请先完成实名认证");  // 未实名拒绝投资，符合合规要求
        }

        // 6. 计算预期收益：amount * annualRate * duration / 365（保留2位小数，HALF_UP）
        // 使用 BigDecimal 链式计算，multiply 不改变 scale，divide 显式指定 scale 与舍入模式 HALF_UP（四舍五入）
        BigDecimal expectedReturn = amount
                .multiply(product.getAnnualRate())  // 金额 × 年化收益率
                .multiply(new BigDecimal(product.getDuration()))  // × 投资期限天数
                .divide(YEAR_DAYS, RETURN_SCALE, RoundingMode.HALF_UP);  // ÷ 365 天，保留 2 位小数四舍五入

        // 7. 扣减用户余额（实体为受管态，事务内 dirty checking 自动持久化）
        user.setBalance(user.getBalance().subtract(amount));  // 余额扣减，使用 BigDecimal.subtract 避免浮点精度丢失

        // 8. 增加产品已投金额（内部已处理满额售罄逻辑）
        productService.addInvestedAmount(productId, amount);  // 累加产品已投金额，满额自动售罄

        // 9. 创建投资订单，状态直接置为 CONFIRMED 开始计息
        String orderNo = generateOrderNo();  // 生成唯一订单号 INV + 时间戳 + 随机数
        InvestmentOrder order = new InvestmentOrder(orderNo, userId, productId, amount, expectedReturn);  // 构造投资订单
        order.setStatus(InvestmentStatus.CONFIRMED);  // 直接确认，开始计息
        InvestmentOrder saved = investmentOrderRepository.save(order);  // 持久化订单

        // 10. 记录投资流水（出账取负，备注携带产品名便于用户识别）
        transactionService.recordTransaction(userId, "USER", TransactionType.INVEST,
                amount.negate(), user.getBalance(), orderNo, "理财投资：" + product.getName());  // negate 将正数转为负数表示出账

        // 11. 风控检查（大额或未实名可能生成风控记录）
        riskService.checkTransactionRisk(userId, amount, orderNo);  // 触发风控规则引擎

        // 12. 返回订单
        return saved;  // 返回已确认的投资订单
    }

    /**
     * 赎回理财产品
     * <p>
     * 校验订单状态为 CONFIRMED 后，返还本金与预期收益至用户余额，
     * 订单状态置为 REDEEMED 并记录赎回流水。
     * </p>
     *
     * @param orderId 订单 ID
     * @return 已赎回的投资订单
     * @throws IllegalArgumentException 订单不存在
     * @throws IllegalStateException    订单状态不允许赎回
     */
    @Transactional  // 声明事务边界：订单状态更新、用户余额返还、流水记录在同一事务，保证资金与订单一致
    public InvestmentOrder redeem(Long orderId) {
        // 1. 查找订单
        InvestmentOrder order = investmentOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("投资订单不存在"));  // 查询订单，不存在则抛异常

        // 2. 校验订单状态为已确认（计息中）
        if (order.getStatus() != InvestmentStatus.CONFIRMED) {  // 非计息中状态（已赎回）拒绝重复赎回
            throw new IllegalStateException("订单状态不允许赎回");
        }

        // 3. 计算返还金额 = 本金 + 预期收益
        // 使用 BigDecimal.add 累加，避免 double 精度丢失导致收益计算错误
        BigDecimal returnAmount = order.getAmount().add(order.getExpectedReturn());  // 本金 + 预期收益

        // 4. 用户余额增加（实体为受管态，事务内 dirty checking 自动持久化）
        User user = userService.findById(order.getUserId());  // 查询用户
        user.setBalance(user.getBalance().add(returnAmount));  // 余额增加，使用 BigDecimal.add 避免浮点精度丢失

        // 5. 订单状态改为已赎回
        order.setStatus(InvestmentStatus.REDEEMED);  // 标记为已赎回，防止重复赎回
        InvestmentOrder saved = investmentOrderRepository.save(order);  // 持久化订单状态

        // 6. 记录赎回流水（入账取正）
        transactionService.recordTransaction(order.getUserId(), "USER", TransactionType.REDEEM,
                returnAmount, user.getBalance(), order.getOrderNo(), "理财赎回");  // 返还金额为正表示入账

        // 7. 返回订单
        return saved;  // 返回已赎回的订单
    }

    /**
     * 查询用户投资订单
     *
     * @param userId 用户 ID
     * @return 该用户的投资订单列表
     */
    public List<InvestmentOrder> findByUserId(Long userId) {
        return investmentOrderRepository.findByUserId(userId);  // 按用户 ID 查询投资订单
    }

    /**
     * 查询所有投资订单（管理后台用）
     *
     * @return 全部投资订单列表
     */
    public List<InvestmentOrder> findAll() {
        return investmentOrderRepository.findAll();  // 查询全部投资订单
    }

    /**
     * 根据订单号查询
     *
     * @param orderNo 订单号
     * @return 投资订单
     * @throws IllegalArgumentException 订单不存在
     */
    public InvestmentOrder findByOrderNo(String orderNo) {
        return investmentOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("投资订单不存在"));  // 按订单号查询，不存在则抛异常
    }

    /**
     * 生成投资订单号：INV + yyyyMMddHHmmss + 4位随机数
     *
     * @return 唯一订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));  // 当前时间格式化
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);  // 生成 1000-9999 随机数，ThreadLocalRandom 线程安全
        return "INV" + timestamp + random;  // 拼接订单号前缀 INV + 时间戳 + 随机数
    }
}
