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
 * 支付订单实体
 * <p>
 * 对应 payment_orders 表，统一支付收银台订单。
 * userId 为付款方，merchantId 为收款方（可为空，表示平台内部支付）。
 * </p>
 */
@Entity  // JPA 实体标识
@Table(name = "payment_orders")  // 映射到 payment_orders 表
public class PaymentOrder {  // 支付订单实体，承载收银台订单信息

    /** 主键 ID，自增 */
    @Id  // JPA 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略
    private Long id;  // 主键 ID，自增长

    /** 订单号（唯一） */
    @Column(nullable = false, unique = true, length = 32)  // 非空且唯一，长度32
    private String orderNo;  // 支付订单号，业务唯一标识

    /** 付款用户 ID */
    @Column(nullable = false)  // 非空
    private Long userId;  // 付款用户 ID，关联 users 表

    /** 收款商户 ID（可为空，平台内部场景） */
    private Long merchantId;  // 收款商户 ID，可为空（如充值场景无收款方）

    /** 支付金额 */
    @Column(nullable = false, precision = 18, scale = 2)  // 非空，金额精度2位
    private BigDecimal amount;  // 支付金额，付款方扣减，收款方入账

    /** 支付渠道：ALIPAY/WECHAT/BANK */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储
    @Column(nullable = false, length = 20)  // 非空，长度20
    private PaymentChannel channel;  // 支付渠道，决定路由策略与手续费率

    /** 订单状态：PENDING/PAID/FAILED/REFUNDED */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储
    @Column(nullable = false, length = 20)  // 非空，长度20
    private PaymentStatus status = PaymentStatus.PENDING;  // 订单状态，默认待支付

    /** 订单描述 */
    @Column(length = 200)  // 长度200
    private String description;  // 订单描述，渠道侧展示用

    /** 创建时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime createdAt;  // 创建时间戳

    /** 最后修改时间 */
    private LocalDateTime updateTime;  // 最后修改时间戳

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public PaymentOrder() {  // JPA 无参构造器
    }

    public PaymentOrder(String orderNo, Long userId, Long merchantId, BigDecimal amount,
                        PaymentChannel channel, String description) {  // 下单构造器
        this.orderNo = orderNo;  // 赋值订单号
        this.userId = userId;  // 赋值付款用户 ID
        this.merchantId = merchantId;  // 赋值收款商户 ID
        this.amount = amount;  // 赋值支付金额
        this.channel = channel;  // 赋值支付渠道
        this.status = PaymentStatus.PENDING;  // 新订单默认待支付
        this.description = description;  // 赋值订单描述
        this.createdAt = LocalDateTime.now();  // 服务端生成下单时间
        this.updateTime = LocalDateTime.now();  // 初始化修改时间
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

    public Long getUserId() {  // 获取付款用户 ID
        return userId;
    }

    public void setUserId(Long userId) {  // 设置付款用户 ID
        this.userId = userId;
    }

    public Long getMerchantId() {  // 获取收款商户 ID
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {  // 设置收款商户 ID
        this.merchantId = merchantId;
    }

    public BigDecimal getAmount() {  // 获取支付金额
        return amount;
    }

    public void setAmount(BigDecimal amount) {  // 设置支付金额
        this.amount = amount;
    }

    public PaymentChannel getChannel() {  // 获取支付渠道
        return channel;
    }

    public void setChannel(PaymentChannel channel) {  // 设置支付渠道
        this.channel = channel;
    }

    public PaymentStatus getStatus() {  // 获取订单状态
        return status;
    }

    public void setStatus(PaymentStatus status) {  // 设置订单状态
        this.status = status;
    }

    public String getDescription() {  // 获取订单描述
        return description;
    }

    public void setDescription(String description) {  // 设置订单描述
        this.description = description;
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
