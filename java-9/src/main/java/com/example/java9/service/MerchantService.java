package com.example.java9.service;  // 声明服务层包路径

import com.example.java9.model.Merchant;  // 导入商户实体
import com.example.java9.model.MerchantStatus;  // 导入商户状态枚举（PENDING/APPROVED/REJECTED）
import com.example.java9.model.Transaction;  // 导入交易流水实体
import com.example.java9.model.TransactionType;  // 导入交易类型枚举
import com.example.java9.repository.MerchantRepository;  // 导入商户 Repository
import org.springframework.stereotype.Service;  // 导入 Spring Service 注解
import org.springframework.transaction.annotation.Transactional;  // 导入事务注解

import java.math.BigDecimal;  // 导入高精度十进制类，金额计算必须用 BigDecimal 避免 double 精度丢失
import java.time.LocalDateTime;  // 导入时间类
import java.util.List;  // 导入集合 List

/**
 * B端商户服务
 * <p>
 * 负责商户入驻审核、收款、提现及流水查询。
 * 商户资金变动同样通过 {@link TransactionService} 记录流水，accountType 标记为 MERCHANT。
 * </p>
 */
@Service  // 标记为 Spring Service Bean
public class MerchantService {

    private final MerchantRepository merchantRepository;  // 商户 Repository，final 保证不可变
    private final TransactionService transactionService;  // 交易流水服务，final 保证不可变

    // 构造器注入：保证依赖不可变、显式暴露依赖、便于单元测试，Spring 启动时即可发现循环依赖
    public MerchantService(MerchantRepository merchantRepository, TransactionService transactionService) {
        this.merchantRepository = merchantRepository;  // 注入商户 Repository
        this.transactionService = transactionService;  // 注入交易流水服务
    }

    /**
     * 商户审核（运营操作）
     * <p>
     * APPROVE：审核通过，状态置为 APPROVED；
     * REJECT：审核拒绝，状态置为 REJECTED 并保存驳回原因。
     * </p>
     *
     * @param merchantId   商户 ID
     * @param action       审核动作（APPROVE/REJECT）
     * @param rejectReason 驳回原因（仅 REJECT 时生效）
     * @return 更新后的商户实体
     * @throws IllegalArgumentException 商户不存在或动作非法
     */
    @Transactional  // 声明事务边界：审核状态与驳回原因写入原子化
    public Merchant auditMerchant(Long merchantId, String action, String rejectReason) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));  // 查询商户，不存在则抛异常
        if ("APPROVE".equalsIgnoreCase(action)) {  // 使用 equalsIgnoreCase 容错大小写
            merchant.setStatus(MerchantStatus.APPROVED);  // 审核通过，商户可登录并收款
        } else if ("REJECT".equalsIgnoreCase(action)) {
            merchant.setStatus(MerchantStatus.REJECTED);  // 审核拒绝
            merchant.setRejectReason(rejectReason);  // 保存驳回原因便于商户申诉
        } else {
            throw new IllegalArgumentException("无效的审核动作：" + action);  // 非法动作快速失败
        }
        merchant.setUpdateTime(LocalDateTime.now());  // 更新修改时间
        return merchantRepository.save(merchant);  // 持久化并返回
    }

    /**
     * 查询所有商户
     *
     * @return 全部商户列表
     */
    public List<Merchant> findAll() {
        return merchantRepository.findAll();  // 查询全部商户
    }

    /**
     * 根据状态查询商户
     *
     * @param status 商户状态
     * @return 符合状态的商户列表
     */
    public List<Merchant> findByStatus(MerchantStatus status) {
        return merchantRepository.findByStatus(status);  // 按状态查询商户
    }

    /**
     * 根据 ID 查询商户
     *
     * @param id 商户 ID
     * @return 商户实体
     * @throws IllegalArgumentException 商户不存在
     */
    public Merchant findById(Long id) {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));  // 查询商户，不存在则抛异常
    }

    /**
     * 商户收款（用户付款给商户）
     * <p>
     * 增加商户余额并记录 PAY 类型流水，关联订单号便于对账。
     * </p>
     *
     * @param merchantId 商户 ID
     * @param amount     收款金额
     * @param orderNo    关联支付订单号
     * @return 更新后的商户实体
     * @throws IllegalArgumentException 商户不存在
     */
    @Transactional  // 声明事务边界：商户余额增加与流水记录原子化，保证资金与流水一致
    public Merchant receivePayment(Long merchantId, BigDecimal amount, String orderNo) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));  // 查询商户
        merchant.setBalance(merchant.getBalance().add(amount));  // 余额增加，使用 BigDecimal.add 避免浮点精度丢失
        merchant.setUpdateTime(LocalDateTime.now());  // 更新修改时间
        Merchant saved = merchantRepository.save(merchant);  // 持久化更新后的余额
        transactionService.recordTransaction(merchantId, "MERCHANT", TransactionType.PAY,
                amount, saved.getBalance(), orderNo, "用户付款");  // 记录商户收款流水，关联订单号便于对账
        return saved;  // 返回更新后的商户
    }

    /**
     * 商户提现
     * <p>
     * 校验余额后扣减并记录提现流水（负数出账）。
     * </p>
     *
     * @param merchantId 商户 ID
     * @param amount     提现金额
     * @param remark     备注
     * @return 更新后的商户实体
     * @throws IllegalArgumentException 商户不存在或余额不足
     */
    @Transactional  // 声明事务边界：余额扣减与流水记录原子化
    public Merchant withdraw(Long merchantId, BigDecimal amount, String remark) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));  // 查询商户
        // 余额校验：不足则拒绝提现
        // 使用 compareTo 而非 equals：BigDecimal equals 比较 scale，compareTo 只比较数值大小
        if (merchant.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("余额不足");  // 余额不足快速失败
        }
        merchant.setBalance(merchant.getBalance().subtract(amount));  // 余额扣减，使用 BigDecimal.subtract 避免浮点精度丢失
        merchant.setUpdateTime(LocalDateTime.now());  // 更新修改时间
        Merchant saved = merchantRepository.save(merchant);  // 持久化更新后的余额
        // 提现为出账，流水金额取负
        transactionService.recordTransaction(merchantId, "MERCHANT", TransactionType.WITHDRAW,
                amount.negate(), saved.getBalance(), null, remark);  // negate 将正数转为负数表示出账
        return saved;  // 返回更新后的商户
    }

    /**
     * 查询商户流水
     *
     * @param merchantId 商户 ID
     * @return 该商户的交易流水列表（按时间倒序）
     */
    public List<Transaction> getMerchantTransactions(Long merchantId) {
        return transactionService.findByAccountIdAndType(merchantId, "MERCHANT");  // 按账户 ID 与类型 MERCHANT 查询流水
    }
}
