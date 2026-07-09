package com.example.java9.model;  // 实体类所在包，归属于 model 层

// JPA 持久化相关注解导入
import jakarta.persistence.Column;  // 字段列映射注解
import jakarta.persistence.Entity;  // 实体标识注解
import jakarta.persistence.EnumType;  // 枚举映射类型
import jakarta.persistence.Enumerated;  // 枚举存储方式注解
import jakarta.persistence.GeneratedValue;  // 主键生成策略注解
import jakarta.persistence.GenerationType;  // 主键生成策略枚举
import jakarta.persistence.Id;  // 主键标识注解
import jakarta.persistence.Table;  // 表名映射注解

// JDK 通用类型导入
import java.math.BigDecimal;  // 高精度十进制，用于金额计算
import java.time.LocalDateTime;  // 时间戳类型

/**
 * 投资订单实体
 * <p>
 * 对应 investment_orders 表，记录用户申购理财产品的订单。
 * expectedReturn 为预期收益（amount * annualRate * duration / 365）。
 * </p>
 */
@Entity  // JPA 实体标识
@Table(name = "investment_orders")  // 映射到 investment_orders 表
public class InvestmentOrder {  // 投资订单实体，承载用户申购请求与预期收益

    /** 主键 ID，自增 */
    @Id  // JPA 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略
    private Long id;  // 主键 ID，自增长

    /** 订单号（唯一，便于展示与查询） */
    @Column(nullable = false, unique = true, length = 32)  // 非空且唯一，长度32适配业务订单号生成规则
    private String orderNo;  // 订单号，业务唯一标识，对客展示与对账使用

    /** 投资用户 ID */
    @Column(nullable = false)  // 非空
    private Long userId;  // 投资用户 ID，关联 users 表

    /** 理财产品 ID */
    @Column(nullable = false)  // 非空
    private Long productId;  // 理财产品 ID，关联 financial_products 表

    /** 投资金额 */
    @Column(nullable = false, precision = 18, scale = 2)  // 非空，金额精度2位
    private BigDecimal amount;  // 投资本金，从用户余额扣减

    /** 预期收益 */
    @Column(nullable = false, precision = 18, scale = 2)  // 非空，金额精度2位
    private BigDecimal expectedReturn;  // 预期收益 = amount * annualRate * duration / 365

    /** 订单状态：PENDING/CONFIRMED/REDEEMED */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储
    @Column(nullable = false, length = 20)  // 非空，长度20
    private InvestmentStatus status = InvestmentStatus.PENDING;  // 订单状态，默认待确认

    /** 创建时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime createdAt;  // 创建时间戳

    public InvestmentOrder() {  // JPA 无参构造器
    }

    public InvestmentOrder(String orderNo, Long userId, Long productId, BigDecimal amount, BigDecimal expectedReturn) {  // 申购构造器
        this.orderNo = orderNo;  // 赋值订单号
        this.userId = userId;  // 赋值用户 ID
        this.productId = productId;  // 赋值产品 ID
        this.amount = amount;  // 赋值投资金额
        this.expectedReturn = expectedReturn;  // 赋值预期收益
        this.status = InvestmentStatus.PENDING;  // 新订单默认待确认
        this.createdAt = LocalDateTime.now();  // 服务端生成下单时间
    }

    public Long getId() {  // 获取主键 ID
        return id;
    }

    public void setId(Long id) {  // 设置主键 ID
        this.id = id;
    }

    public String getOrderNo() {  // 获取订单号
        return orderNo;
    }

    public void setOrderNo(String orderNo) {  // 设置订单号
        this.orderNo = orderNo;
    }

    public Long getUserId() {  // 获取投资用户 ID
        return userId;
    }

    public void setUserId(Long userId) {  // 设置投资用户 ID
        this.userId = userId;
    }

    public Long getProductId() {  // 获取理财产品 ID
        return productId;
    }

    public void setProductId(Long productId) {  // 设置理财产品 ID
        this.productId = productId;
    }

    public BigDecimal getAmount() {  // 获取投资金额
        return amount;
    }

    public void setAmount(BigDecimal amount) {  // 设置投资金额
        this.amount = amount;
    }

    public BigDecimal getExpectedReturn() {  // 获取预期收益
        return expectedReturn;
    }

    public void setExpectedReturn(BigDecimal expectedReturn) {  // 设置预期收益
        this.expectedReturn = expectedReturn;
    }

    public InvestmentStatus getStatus() {  // 获取订单状态
        return status;
    }

    public void setStatus(InvestmentStatus status) {  // 设置订单状态
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;
    }
}
