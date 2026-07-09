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
import java.math.BigDecimal;  // 高精度十进制，用于金额与收益率
import java.time.LocalDateTime;  // 时间戳类型

/**
 * 理财产品实体
 * <p>
 * 对应 financial_products 表，底层理财核心业务数据。
 * annualRate 为年化收益率（如 0.045 表示 4.5%）。
 * totalAmount 为产品募集总额，investedAmount 为已投金额，两者差值为可投额度。
 * </p>
 */
@Entity  // JPA 实体标识
@Table(name = "financial_products")  // 映射到 financial_products 表
public class FinancialProduct {  // 理财产品实体，承载产品属性与募集进度

    /** 主键 ID，自增 */
    @Id  // JPA 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略
    private Long id;  // 主键 ID，自增长

    /** 产品名称 */
    @Column(nullable = false, length = 100)  // 非空，长度100
    private String name;  // 产品名称，C 端列表与详情展示

    /** 产品类型：DEPOSIT/FUND/INSURANCE */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储
    @Column(nullable = false, length = 20)  // 非空，长度20
    private ProductType type;  // 产品类型，决定风控策略与收益计算规则

    /** 年化收益率（如 0.045 表示 4.5%） */
    @Column(nullable = false, precision = 6, scale = 4)  // 非空，6位总长4位小数，支持百万分位精度
    private BigDecimal annualRate;  // 年化收益率，小数表示（0.045 = 4.5%）

    /** 起投金额 */
    @Column(nullable = false, precision = 18, scale = 2)  // 非空，金额精度2位
    private BigDecimal minAmount;  // 起投金额，低于此金额不允许下单

    /** 投资期限（天） */
    @Column(nullable = false)  // 非空
    private Integer duration;  // 投资期限（天），影响预期收益计算与到期赎回时间

    /** 风险等级（1-5，1 最低 5 最高） */
    @Column(nullable = false)  // 非空
    private Integer riskLevel;  // 风险等级1-5，与用户风险测评匹配后才允许投资

    /** 募集总额 */
    @Column(nullable = false, precision = 18, scale = 2)  // 非空，金额精度2位
    private BigDecimal totalAmount;  // 募集总额，产品募集上限

    /** 已投金额 */
    @Column(nullable = false, precision = 18, scale = 2)  // 非空，金额精度2位
    private BigDecimal investedAmount = BigDecimal.ZERO;  // 已投金额，初始0，下单累加，满额触发售罄

    /** 产品状态：ON_SALE/SOLD_OUT/OFF_SHELF */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储
    @Column(nullable = false, length = 20)  // 非空，长度20
    private ProductStatus status = ProductStatus.ON_SALE;  // 产品状态，默认在售

    /** 产品描述（可能较长，扩展长度避免 255 截断） */
    @Column(length = 1000)  // 长度1000，覆盖详细产品说明
    private String description;  // 产品描述，C 端详情页展示

    /** 创建时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime createdAt;  // 创建时间戳

    public FinancialProduct() {  // JPA 无参构造器
    }

    public FinancialProduct(String name, ProductType type, BigDecimal annualRate, BigDecimal minAmount,
                            Integer duration, Integer riskLevel, BigDecimal totalAmount, String description) {  // 上架构造器
        this.name = name;  // 赋值产品名称
        this.type = type;  // 赋值产品类型
        this.annualRate = annualRate;  // 赋值年化收益率
        this.minAmount = minAmount;  // 赋值起投金额
        this.duration = duration;  // 赋值投资期限
        this.riskLevel = riskLevel;  // 赋值风险等级
        this.totalAmount = totalAmount;  // 赋值募集总额
        this.investedAmount = BigDecimal.ZERO;  // 新产品已投金额为0
        this.status = ProductStatus.ON_SALE;  // 新产品默认在售
        this.description = description;  // 赋值产品描述
        this.createdAt = LocalDateTime.now();  // 服务端生成上架时间
    }

    public Long getId() {  // 获取主键 ID
        return id;
    }

    public void setId(Long id) {  // 设置主键 ID
        this.id = id;
    }

    public String getName() {  // 获取产品名称
        return name;
    }

    public void setName(String name) {  // 设置产品名称
        this.name = name;
    }

    public ProductType getType() {  // 获取产品类型
        return type;
    }

    public void setType(ProductType type) {  // 设置产品类型
        this.type = type;
    }

    public BigDecimal getAnnualRate() {  // 获取年化收益率
        return annualRate;
    }

    public void setAnnualRate(BigDecimal annualRate) {  // 设置年化收益率
        this.annualRate = annualRate;
    }

    public BigDecimal getMinAmount() {  // 获取起投金额
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {  // 设置起投金额
        this.minAmount = minAmount;
    }

    public Integer getDuration() {  // 获取投资期限
        return duration;
    }

    public void setDuration(Integer duration) {  // 设置投资期限
        this.duration = duration;
    }

    public Integer getRiskLevel() {  // 获取风险等级
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {  // 设置风险等级
        this.riskLevel = riskLevel;
    }

    public BigDecimal getTotalAmount() {  // 获取募集总额
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {  // 设置募集总额
        this.totalAmount = totalAmount;
    }

    public BigDecimal getInvestedAmount() {  // 获取已投金额
        return investedAmount;
    }

    public void setInvestedAmount(BigDecimal investedAmount) {  // 设置已投金额，需配合乐观锁防并发超卖
        this.investedAmount = investedAmount;
    }

    public ProductStatus getStatus() {  // 获取产品状态
        return status;
    }

    public void setStatus(ProductStatus status) {  // 设置产品状态
        this.status = status;
    }

    public String getDescription() {  // 获取产品描述
        return description;
    }

    public void setDescription(String description) {  // 设置产品描述
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;
    }
}
