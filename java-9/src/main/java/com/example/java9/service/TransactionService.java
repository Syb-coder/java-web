package com.example.java9.service;  // 声明服务层包路径

import com.example.java9.model.Transaction;  // 导入交易流水实体
import com.example.java9.model.TransactionType;  // 导入交易类型枚举（RECHARGE/WITHDRAW/INVEST 等）
import com.example.java9.repository.TransactionRepository;  // 导入流水 Repository
import org.springframework.stereotype.Service;  // 导入 Spring Service 注解
import org.springframework.transaction.annotation.Transactional;  // 导入事务注解

import java.math.BigDecimal;  // 导入高精度十进制类，金额计算必须用 BigDecimal 避免 double 浮点精度丢失
import java.time.LocalDateTime;  // 导入时间类
import java.time.format.DateTimeFormatter;  // 导入时间格式化器
import java.util.List;  // 导入集合 List
import java.util.concurrent.ThreadLocalRandom;  // 导入线程本地随机数生成器

/**
 * 交易流水服务
 * <p>
 * 底层核心服务，所有资金变动（充值/提现/投资/赎回/支付/退款）
 * 均通过本服务记录流水，保证资金链路可审计。
 * </p>
 */
@Service  // 标记为 Spring Service Bean，由 IoC 容器统一管理生命周期
public class TransactionService {

    private final TransactionRepository transactionRepository;  // 流水 Repository，声明为 final 保证依赖不可变

    // 构造器注入：相比 @Autowired 字段注入，可保证依赖不可变（final）、显式暴露所需依赖、便于单元测试 mock，且能被 Spring 检测循环依赖
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;  // 注入流水 Repository
    }

    /**
     * 记录一条交易流水
     * <p>
     * 调用方需先完成余额更新，再将更新后的余额传入 balanceAfter。
     * </p>
     *
     * @param accountId      账户 ID（用户或商户）
     * @param accountType    账户类型（USER/MERCHANT）
     * @param type           交易类型
     * @param amount         交易金额（正数入账，负数出账）
     * @param balanceAfter   交易后余额
     * @param relatedOrderNo 关联订单号（可为空）
     * @param remark         备注
     * @return 已保存的流水实体
     */
    @Transactional  // 声明事务边界：流水写入要么成功提交，要么异常时整体回滚，保证审计数据一致性
    public Transaction recordTransaction(Long accountId, String accountType, TransactionType type,
                                         BigDecimal amount, BigDecimal balanceAfter,
                                         String relatedOrderNo, String remark) {
        // 构造流水实体，流水号由 generateTransactionNo() 现场生成保证唯一
        Transaction tx = new Transaction(
                generateTransactionNo(),  // 生成唯一流水号：TX + 时间戳 + 4 位随机数
                accountId,  // 账户 ID
                accountType,  // 账户类型 USER/MERCHANT
                type,  // 交易类型枚举
                amount,  // 交易金额，使用 BigDecimal 避免 double 浮点精度丢失
                balanceAfter,  // 交易后余额，用于审计对账
                relatedOrderNo,  // 关联订单号
                remark  // 备注
        );
        return transactionRepository.save(tx);  // 持久化流水并返回带主键 ID 的实体
    }

    /**
     * 查询账户流水（按时间倒序）
     *
     * @param accountId 账户 ID
     * @return 流水列表
     */
    public List<Transaction> findByAccountId(Long accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);  // 调用 Repository 按创建时间倒序查询
    }

    /**
     * 查询指定类型账户的流水
     *
     * @param accountId   账户 ID
     * @param accountType 账户类型
     * @return 流水列表
     */
    public List<Transaction> findByAccountIdAndType(Long accountId, String accountType) {
        return transactionRepository.findByAccountIdAndAccountTypeOrderByCreatedAtDesc(accountId, accountType);  // 按账户 ID 与类型联合查询并倒序
    }

    /**
     * 查询全部流水（管理后台用）
     */
    public List<Transaction> findAll() {
        return transactionRepository.findAll();  // 查询全部流水
    }

    /**
     * 生成唯一流水号：TX + 时间戳 + 4位随机数
     */
    public String generateTransactionNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));  // 当前时间格式化为 yyyyMMddHHmmss
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);  // 生成 1000-9999 随机数，ThreadLocalRandom 相比 Random 性能更优且线程安全
        return "TX" + timestamp + random;  // 拼接流水号前缀 TX + 时间戳 + 随机数
    }
}
