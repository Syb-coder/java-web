package com.example.java9.service;  // 声明服务层包路径

import com.example.java9.model.Merchant;  // 导入商户实体
import com.example.java9.model.MerchantStatus;  // 导入商户状态枚举
import com.example.java9.model.PaymentChannel;  // 导入支付渠道枚举（ALIPAY/WECHAT/BANK）
import com.example.java9.model.PaymentOrder;  // 导入支付订单实体
import com.example.java9.model.PaymentStatus;  // 导入支付状态枚举（PENDING/PAID/REFUNDED）
import com.example.java9.model.TransactionType;  // 导入交易类型枚举
import com.example.java9.model.User;  // 导入用户实体
import com.example.java9.model.UserStatus;  // 导入用户状态枚举
import com.example.java9.repository.PaymentOrderRepository;  // 导入支付订单 Repository
import org.springframework.stereotype.Service;  // 导入 Spring Service 注解
import org.springframework.transaction.annotation.Transactional;  // 导入事务注解

import java.math.BigDecimal;  // 导入高精度十进制类，金额计算必须用 BigDecimal 避免 double 精度丢失
import java.time.LocalDateTime;  // 导入时间类
import java.time.format.DateTimeFormatter;  // 导入时间格式化器
import java.util.List;  // 导入集合 List
import java.util.concurrent.ThreadLocalRandom;  // 导入线程本地随机数生成器

/**
 * 统一支付收银台服务
 * <p>
 * 负责支付订单的创建、确认扣款、退款及查询。
 * 支付时校验用户状态与商户资质，扣款后商户收款并记录流水、触发风控检查。
 * 退款时返还用户金额并扣减商户余额。
 * </p>
 */
@Service  // 标记为 Spring Service Bean
public class PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;  // 支付订单 Repository，final 保证不可变
    private final UserService userService;  // 用户服务，final 保证不可变
    private final MerchantService merchantService;  // 商户服务，final 保证不可变
    private final TransactionService transactionService;  // 交易流水服务，final 保证不可变
    private final RiskService riskService;  // 风控服务，final 保证不可变

    // 构造器注入：保证依赖不可变、显式暴露依赖、便于单元测试，Spring 启动时即可发现循环依赖
    public PaymentService(PaymentOrderRepository paymentOrderRepository,
                          UserService userService,
                          MerchantService merchantService,
                          TransactionService transactionService,
                          RiskService riskService) {
        this.paymentOrderRepository = paymentOrderRepository;  // 注入支付订单 Repository
        this.userService = userService;  // 注入用户服务
        this.merchantService = merchantService;  // 注入商户服务
        this.transactionService = transactionService;  // 注入交易流水服务
        this.riskService = riskService;  // 注入风控服务
    }

    /**
     * 创建支付订单
     * <p>
     * 校验用户状态非冻结、商户已通过审核后，解析支付渠道并创建待支付订单。
     * </p>
     *
     * @param userId      付款用户 ID
     * @param merchantId  收款商户 ID（可为空，表示平台内部支付）
     * @param amount      支付金额
     * @param channel     支付渠道字符串（ALIPAY/WECHAT/BANK）
     * @param description 订单描述
     * @return 待支付订单
     * @throws IllegalArgumentException 用户不存在或支付渠道无效
     * @throws IllegalStateException    用户已冻结或商户未通过审核
     */
    @Transactional  // 声明事务边界：用户/商户校验与订单创建原子化
    public PaymentOrder createPayment(Long userId, Long merchantId, BigDecimal amount,
                                      String channel, String description) {
        // 1. 查找用户，校验状态非冻结
        User user = userService.findById(userId);  // 查询用户，不存在则抛异常
        if (user.getStatus() == UserStatus.FROZEN) {  // 冻结用户拒绝支付，防止资金外逃
            throw new IllegalStateException("用户账户已冻结");
        }

        // 2. 商户非空时校验状态为已通过审核
        if (merchantId != null) {  // 商户 ID 非空才校验（平台内部支付时商户可为空）
            Merchant merchant = merchantService.findById(merchantId);  // 查询商户
            if (merchant.getStatus() != MerchantStatus.APPROVED) {  // 未审核通过的商户拒绝收款
                throw new IllegalStateException("商户未通过审核");
            }
        }

        // 3. 解析支付渠道字符串为枚举，无效则拒绝
        if (channel == null) {
            throw new IllegalArgumentException("支付渠道不能为空");  // 渠道为空快速失败
        }
        PaymentChannel paymentChannel;  // 声明支付渠道枚举变量
        try {
            paymentChannel = PaymentChannel.valueOf(channel.toUpperCase());  // 将字符串转为大写后解析为枚举，容错大小写
        } catch (IllegalArgumentException e) {  // 捕获枚举解析异常
            throw new IllegalArgumentException("无效的支付渠道：" + channel);  // 转换为业务异常
        }

        // 4. 创建订单：orderNo = "PAY" + 时间戳 + 随机数, status = PENDING
        String orderNo = generateOrderNo();  // 生成唯一订单号
        PaymentOrder order = new PaymentOrder(orderNo, userId, merchantId, amount, paymentChannel, description);  // 构造支付订单，初始状态 PENDING
        return paymentOrderRepository.save(order);  // 持久化并返回待支付订单
    }

    /**
     * 确认支付（扣款）
     * <p>
     * 校验订单状态为待支付、用户余额充足后扣减余额、商户收款，
     * 订单状态置为已支付并记录用户支付流水、触发风控检查。
     * </p>
     *
     * @param orderNo 订单号
     * @return 已支付订单
     * @throws IllegalArgumentException 订单不存在或余额不足
     * @throws IllegalStateException    订单状态不允许支付
     */
    @Transactional  // 声明事务边界：用户扣款、商户收款、订单状态更新、流水记录、风控检查在同一事务，任一失败整体回滚，保证资金安全
    public PaymentOrder confirmPayment(String orderNo) {
        // 1. 查找订单，校验状态为待支付
        PaymentOrder order = paymentOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("支付订单不存在"));  // 按订单号查询，不存在则抛异常
        if (order.getStatus() != PaymentStatus.PENDING) {  // 非待支付状态拒绝重复支付
            throw new IllegalStateException("订单状态不允许支付");
        }

        // 2. 校验用户余额充足
        User user = userService.findById(order.getUserId());  // 查询付款用户
        // 使用 compareTo 比较金额，避免 BigDecimal equals 因 scale 不同导致判断错误
        if (user.getBalance().compareTo(order.getAmount()) < 0) {
            throw new IllegalArgumentException("余额不足");  // 余额不足快速失败
        }

        // 3. 扣减用户余额（实体为受管态，事务内 dirty checking 自动持久化）
        user.setBalance(user.getBalance().subtract(order.getAmount()));  // 余额扣减，使用 BigDecimal.subtract 避免浮点精度丢失

        // 4. 有商户时触发商户收款（内部记录商户流水）
        if (order.getMerchantId() != null) {  // 商户非空时触发收款
            merchantService.receivePayment(order.getMerchantId(), order.getAmount(), orderNo);  // 商户收款并记录商户流水
        }

        // 5. 订单状态改为已支付
        order.setStatus(PaymentStatus.PAID);  // 标记为已支付
        order.setUpdateTime(LocalDateTime.now());  // 更新修改时间
        PaymentOrder saved = paymentOrderRepository.save(order);  // 持久化订单状态

        // 6. 记录用户支付流水（出账取负，备注使用订单描述）
        transactionService.recordTransaction(order.getUserId(), "USER", TransactionType.PAY,
                order.getAmount().negate(), user.getBalance(), orderNo, order.getDescription());  // negate 将正数转为负数表示出账

        // 7. 风控检查
        riskService.checkTransactionRisk(order.getUserId(), order.getAmount(), orderNo);  // 触发风控规则引擎

        // 8. 返回订单
        return saved;  // 返回已支付订单
    }

    /**
     * 退款
     * <p>
     * 校验订单状态为已支付后，返还用户金额、扣减商户余额（如有），
     * 订单状态置为已退款并记录用户退款流水。
     * </p>
     *
     * @param orderNo 订单号
     * @return 已退款订单
     * @throws IllegalArgumentException 订单不存在
     * @throws IllegalStateException    订单状态不允许退款
     */
    @Transactional  // 声明事务边界：用户退款、商户扣款、订单状态更新、流水记录在同一事务，保证资金双向一致
    public PaymentOrder refund(String orderNo) {
        // 1. 查找订单，校验状态为已支付
        PaymentOrder order = paymentOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("支付订单不存在"));  // 按订单号查询
        if (order.getStatus() != PaymentStatus.PAID) {  // 非已支付状态拒绝退款
            throw new IllegalStateException("订单状态不允许退款");
        }

        // 2. 用户余额增加（实体为受管态，事务内 dirty checking 自动持久化）
        User user = userService.findById(order.getUserId());  // 查询用户
        user.setBalance(user.getBalance().add(order.getAmount()));  // 余额返还，使用 BigDecimal.add 避免浮点精度丢失

        // 3. 有商户时扣减商户余额（实体为受管态，事务内 dirty checking 自动持久化）
        if (order.getMerchantId() != null) {  // 商户非空时需扣回已收款
            Merchant merchant = merchantService.findById(order.getMerchantId());  // 查询商户
            merchant.setBalance(merchant.getBalance().subtract(order.getAmount()));  // 商户余额扣减，使用 BigDecimal.subtract
        }

        // 4. 订单状态改为已退款
        order.setStatus(PaymentStatus.REFUNDED);  // 标记为已退款，防止重复退款
        order.setUpdateTime(LocalDateTime.now());  // 更新修改时间
        PaymentOrder saved = paymentOrderRepository.save(order);  // 持久化订单状态

        // 5. 记录用户退款流水（入账取正）
        transactionService.recordTransaction(order.getUserId(), "USER", TransactionType.REFUND,
                order.getAmount(), user.getBalance(), orderNo, "订单退款");  // 退款金额为正表示入账

        // 6. 返回订单
        return saved;  // 返回已退款订单
    }

    /**
     * 查询用户支付订单
     *
     * @param userId 用户 ID
     * @return 该用户的支付订单列表
     */
    public List<PaymentOrder> findByUserId(Long userId) {
        return paymentOrderRepository.findByUserId(userId);  // 按用户 ID 查询支付订单
    }

    /**
     * 查询商户支付订单
     *
     * @param merchantId 商户 ID
     * @return 该商户的支付订单列表
     */
    public List<PaymentOrder> findByMerchantId(Long merchantId) {
        return paymentOrderRepository.findByMerchantId(merchantId);  // 按商户 ID 查询支付订单
    }

    /**
     * 查询所有支付订单（管理后台用）
     *
     * @return 全部支付订单列表
     */
    public List<PaymentOrder> findAll() {
        return paymentOrderRepository.findAll();  // 查询全部支付订单
    }

    /**
     * 根据订单号查询
     *
     * @param orderNo 订单号
     * @return 支付订单
     * @throws IllegalArgumentException 订单不存在
     */
    public PaymentOrder findByOrderNo(String orderNo) {
        return paymentOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("支付订单不存在"));  // 按订单号查询，不存在则抛异常
    }

    /**
     * 生成支付订单号：PAY + yyyyMMddHHmmss + 4位随机数
     *
     * @return 唯一订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));  // 当前时间格式化
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);  // 生成 1000-9999 随机数，ThreadLocalRandom 线程安全
        return "PAY" + timestamp + random;  // 拼接订单号前缀 PAY + 时间戳 + 随机数
    }
}
