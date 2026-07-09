package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import java.math.BigDecimal;  // 导入高精度十进制类,用于金额表示
import java.time.LocalDateTime;  // 导入日期时间类

/**
 * 支付订单响应 DTO
 * <p>
 * 返回支付订单的详情，包含订单号、商户信息、支付渠道与状态。
 * </p>
 */
public class PaymentResponse {  // 支付订单响应 DTO 类定义

    /** 订单 ID */
    private Long id;  // 订单唯一标识(主键)

    /** 订单号（业务唯一） */
    private String orderNo;  // 业务订单号(对用户可见)

    /** 支付用户 ID */
    private Long userId;  // 发起支付的用户 ID

    /** 商户 ID（无商户场景为空） */
    private Long merchantId;  // 商户 ID,充值等无商户场景为 null

    /** 商户名称（冗余，便于展示） */
    private String merchantName;  // 商户名称(冗余字段,便于前端直接展示)

    /** 支付金额（元） */
    private BigDecimal amount;  // 支付金额(元)

    /** 支付渠道：ALIPAY/WECHAT/BANK */
    private String channel;  // 支付渠道:ALIPAY/WECHAT/BANK

    /** 订单状态：PENDING/SUCCESS/FAILED/REFUNDED */
    private String status;  // 订单状态:PENDING(待支付)/SUCCESS(成功)/FAILED(失败)/REFUNDED(已退款)

    /** 支付描述 */
    private String description;  // 支付描述

    /** 订单创建时间 */
    private LocalDateTime createdAt;  // 订单创建时间戳

    // —— id 字段的 getter/setter ——
    public Long getId() {  // 获取订单 ID
        return id;  // 返回订单 ID
    }

    public void setId(Long id) {  // 设置订单 ID
        this.id = id;  // 赋值订单 ID
    }

    // —— orderNo 字段的 getter/setter ——
    public String getOrderNo() {  // 获取订单号
        return orderNo;  // 返回订单号
    }

    public void setOrderNo(String orderNo) {  // 设置订单号
        this.orderNo = orderNo;  // 赋值订单号
    }

    // —— userId 字段的 getter/setter ——
    public Long getUserId() {  // 获取用户 ID
        return userId;  // 返回用户 ID
    }

    public void setUserId(Long userId) {  // 设置用户 ID
        this.userId = userId;  // 赋值用户 ID
    }

    // —— merchantId 字段的 getter/setter ——
    public Long getMerchantId() {  // 获取商户 ID
        return merchantId;  // 返回商户 ID
    }

    public void setMerchantId(Long merchantId) {  // 设置商户 ID
        this.merchantId = merchantId;  // 赋值商户 ID
    }

    // —— merchantName 字段的 getter/setter ——
    public String getMerchantName() {  // 获取商户名称
        return merchantName;  // 返回商户名称
    }

    public void setMerchantName(String merchantName) {  // 设置商户名称
        this.merchantName = merchantName;  // 赋值商户名称
    }

    // —— amount 字段的 getter/setter ——
    public BigDecimal getAmount() {  // 获取支付金额
        return amount;  // 返回支付金额
    }

    public void setAmount(BigDecimal amount) {  // 设置支付金额
        this.amount = amount;  // 赋值支付金额
    }

    // —— channel 字段的 getter/setter ——
    public String getChannel() {  // 获取支付渠道
        return channel;  // 返回支付渠道
    }

    public void setChannel(String channel) {  // 设置支付渠道
        this.channel = channel;  // 赋值支付渠道
    }

    // —— status 字段的 getter/setter ——
    public String getStatus() {  // 获取订单状态
        return status;  // 返回订单状态
    }

    public void setStatus(String status) {  // 设置订单状态
        this.status = status;  // 赋值订单状态
    }

    // —— description 字段的 getter/setter ——
    public String getDescription() {  // 获取支付描述
        return description;  // 返回支付描述
    }

    public void setDescription(String description) {  // 设置支付描述
        this.description = description;  // 赋值支付描述
    }

    // —— createdAt 字段的 getter/setter ——
    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;  // 返回创建时间
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;  // 赋值创建时间
    }
}
