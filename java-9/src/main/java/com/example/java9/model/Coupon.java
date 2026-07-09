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
 * 优惠券实体
 * <p>
 * 对应 coupons 表，用户领取的理财优惠券。
 * amount 为优惠金额，minAmount 为使用门槛（满减条件）。
 * </p>
 */
@Entity  // JPA 实体标识
@Table(name = "coupons")  // 映射到 coupons 表
public class Coupon {  // 优惠券实体，承载用户领取的满减券

    /** 主键 ID，自增 */
    @Id  // JPA 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略
    private Long id;  // 主键 ID，自增长

    /** 所属用户 ID */
    @Column(nullable = false)  // 非空
    private Long userId;  // 所属用户 ID，关联 users 表

    /** 关联活动 ID */
    @Column(nullable = false)  // 非空
    private Long activityId;  // 关联活动 ID，追溯发券来源

    /** 优惠券标题 */
    @Column(nullable = false, length = 100)  // 非空，长度100
    private String title;  // 优惠券标题，C 端展示

    /** 优惠金额 */
    @Column(nullable = false, precision = 18, scale = 2)  // 非空，金额精度2位
    private BigDecimal amount;  // 优惠金额，核销时抵扣订单

    /** 使用门槛（满 minAmount 元可用） */
    @Column(nullable = false, precision = 18, scale = 2)  // 非空，金额精度2位
    private BigDecimal minAmount;  // 使用门槛，订单金额需≥此值才可核销

    /** 状态：UNUSED/USED/EXPIRED */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储
    @Column(nullable = false, length = 10)  // 非空，长度10
    private CouponStatus status = CouponStatus.UNUSED;  // 状态，默认未使用

    /** 过期时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime expiredAt;  // 过期时间，定时任务扫描置为 EXPIRED

    /** 创建时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime createdAt;  // 创建时间戳

    public Coupon() {  // JPA 无参构造器
    }

    public Coupon(Long userId, Long activityId, String title, BigDecimal amount,
                  BigDecimal minAmount, LocalDateTime expiredAt) {  // 发券构造器
        this.userId = userId;  // 赋值用户 ID
        this.activityId = activityId;  // 赋值活动 ID
        this.title = title;  // 赋值优惠券标题
        this.amount = amount;  // 赋值优惠金额
        this.minAmount = minAmount;  // 赋值使用门槛
        this.status = CouponStatus.UNUSED;  // 新券默认未使用
        this.expiredAt = expiredAt;  // 赋值过期时间
        this.createdAt = LocalDateTime.now();  // 服务端生成发券时间
    }

    public Long getId() {  // 获取主键 ID
        return id;
    }

    public void setId(Long id) {  // 设置主键 ID
        this.id = id;
    }

    public Long getUserId() {  // 获取所属用户 ID
        return userId;
    }

    public void setUserId(Long userId) {  // 设置所属用户 ID
        this.userId = userId;
    }

    public Long getActivityId() {  // 获取关联活动 ID
        return activityId;
    }

    public void setActivityId(Long activityId) {  // 设置关联活动 ID
        this.activityId = activityId;
    }

    public String getTitle() {  // 获取优惠券标题
        return title;
    }

    public void setTitle(String title) {  // 设置优惠券标题
        this.title = title;
    }

    public BigDecimal getAmount() {  // 获取优惠金额
        return amount;
    }

    public void setAmount(BigDecimal amount) {  // 设置优惠金额
        this.amount = amount;
    }

    public BigDecimal getMinAmount() {  // 获取使用门槛
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {  // 设置使用门槛
        this.minAmount = minAmount;
    }

    public CouponStatus getStatus() {  // 获取状态
        return status;
    }

    public void setStatus(CouponStatus status) {  // 设置状态
        this.status = status;
    }

    public LocalDateTime getExpiredAt() {  // 获取过期时间
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {  // 设置过期时间
        this.expiredAt = expiredAt;
    }

    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;
    }
}
