package com.example.java9.model;  // 实体类所在包，归属于 model 层

// JPA 持久化相关注解导入
import jakarta.persistence.Column;  // 字段列映射注解
import jakarta.persistence.Entity;  // 实体标识注解
import jakarta.persistence.EnumType;  // 枚举映射类型
import jakarta.persistence.Enumerated;  // 枚举存储方式注解
import jakarta.persistence.GeneratedValue;  // 主键生成策略注解
import jakarta.persistence.GenerationType;  // 主键生成策略枚举
import jakarta.persistence.Id;  // 主键标识注解
import jakarta.persistence.PreUpdate;  // 更新前回调注解
import jakarta.persistence.Table;  // 表名映射注解

// JDK 通用类型导入
import java.math.BigDecimal;  // 高精度十进制，用于金额计算
import java.time.LocalDateTime;  // 时间戳类型

/**
 * 交易流水实体
 * <p>
 * 对应 transactions 表，底层资金清算的统一流水记录。
 * 所有资金变动（充值、提现、投资、赎回、支付、退款）均会生成一条流水，
 * balanceAfter 记录变动后的账户余额，便于审计与对账。
 * </p>
 */
@Entity  // JPA 实体标识
@Table(name = "transactions")  // 映射到 transactions 表
public class Transaction {  // 交易流水实体，资金变动的不可变历史记录

    /** 主键 ID，自增 */
    @Id  // JPA 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略
    private Long id;  // 主键 ID，自增长

    /** 流水号（唯一） */
    @Column(nullable = false, unique = true, length = 32)  // 非空且唯一，长度32
    private String transactionNo;  // 流水号，业务唯一标识，用于对账与客诉追溯

    /** 账户 ID（用户或商户） */
    @Column(nullable = false)  // 非空
    private Long accountId;  // 账户 ID，关联 users 或 merchants 表

    /** 账户类型：USER/MERCHANT（用字符串存储，避免新增枚举） */
    @Column(nullable = false, length = 10)  // 非空，长度10
    private String accountType;  // 账户类型，区分用户与商户，用字符串存储便于扩展

    /** 交易类型：RECHARGE/WITHDRAW/INVEST/REDEEM/PAY/REFUND */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储
    @Column(nullable = false, length = 20)  // 非空，长度20
    private TransactionType type;  // 交易类型，决定资金流向与会计科目

    /** 交易金额（正数表示入账，负数表示出账） */
    @Column(nullable = false, precision = 18, scale = 2)  // 非空，金额精度2位
    private BigDecimal amount;  // 交易金额，正入负出，便于汇总求和

    /** 交易后余额 */
    @Column(nullable = false, precision = 18, scale = 2)  // 非空，金额精度2位
    private BigDecimal balanceAfter;  // 交易后账户余额，审计关键字段

    /** 关联订单号（可为空，如充值无关联订单） */
    @Column(length = 32)  // 长度32
    private String relatedOrderNo;  // 关联订单号，便于追溯到具体业务订单

    /** 备注 */
    @Column(length = 200)  // 长度200
    private String remark;  // 备注信息，记录交易上下文

    /** 创建时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime createdAt;  // 创建时间戳，与流水号配合定位

    /** 最后修改时间 */
    private LocalDateTime updateTime;  // 最后修改时间戳

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public Transaction() {  // JPA 无参构造器
    }

    public Transaction(String transactionNo, Long accountId, String accountType, TransactionType type,
                       BigDecimal amount, BigDecimal balanceAfter, String relatedOrderNo, String remark) {  // 流水生成构造器
        this.transactionNo = transactionNo;  // 赋值流水号
        this.accountId = accountId;  // 赋值账户 ID
        this.accountType = accountType;  // 赋值账户类型
        this.type = type;  // 赋值交易类型
        this.amount = amount;  // 赋值交易金额
        this.balanceAfter = balanceAfter;  // 赋值交易后余额
        this.relatedOrderNo = relatedOrderNo;  // 赋值关联订单号
        this.remark = remark;  // 赋值备注
        this.createdAt = LocalDateTime.now();  // 服务端生成流水时间
        this.updateTime = LocalDateTime.now();  // 初始化修改时间
    }

    public Long getId() {  // 获取主键 ID
        return id;
    }

    public void setId(Long id) {  // 设置主键 ID
        this.id = id;
    }

    public String getTransactionNo() {  // 获取流水号
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {  // 设置流水号
        this.transactionNo = transactionNo;
    }

    public Long getAccountId() {  // 获取账户 ID
        return accountId;
    }

    public void setAccountId(Long accountId) {  // 设置账户 ID
        this.accountId = accountId;
    }

    public String getAccountType() {  // 获取账户类型
        return accountType;
    }

    public void setAccountType(String accountType) {  // 设置账户类型
        this.accountType = accountType;
    }

    public TransactionType getType() {  // 获取交易类型
        return type;
    }

    public void setType(TransactionType type) {  // 设置交易类型
        this.type = type;
    }

    public BigDecimal getAmount() {  // 获取交易金额
        return amount;
    }

    public void setAmount(BigDecimal amount) {  // 设置交易金额
        this.amount = amount;
    }

    public BigDecimal getBalanceAfter() {  // 获取交易后余额
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {  // 设置交易后余额
        this.balanceAfter = balanceAfter;
    }

    public String getRelatedOrderNo() {  // 获取关联订单号
        return relatedOrderNo;
    }

    public void setRelatedOrderNo(String relatedOrderNo) {  // 设置关联订单号
        this.relatedOrderNo = relatedOrderNo;
    }

    public String getRemark() {  // 获取备注
        return remark;
    }

    public void setRemark(String remark) {  // 设置备注
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
