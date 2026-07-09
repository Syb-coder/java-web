package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入非空字符串校验注解
import jakarta.validation.constraints.NotNull;  // 导入非 null 校验注解

import java.math.BigDecimal;  // 导入高精度十进制类,用于金额表示

/**
 * 支付请求 DTO
 * <p>
 * C 端用户发起支付，merchantId 可空表示无商户场景（如充值）。
 * 支付渠道支持 ALIPAY/WECHAT/BANK。
 * </p>
 */
public class PaymentRequest {  // 支付请求 DTO 类定义

    /** 商户 ID（可空，无商户场景如充值时为空） */
    private Long merchantId;  // 商户 ID,充值等无商户场景可为 null

    /** 支付金额（元） */
    @NotNull(message = "支付金额不能为空")  // 非 null 校验:支付金额必填
    private BigDecimal amount;  // 支付金额(元)

    /** 支付渠道：ALIPAY/WECHAT/BANK */
    @NotBlank(message = "支付渠道不能为空")  // 非空字符串校验:支付渠道必填
    private String channel;  // 支付渠道:ALIPAY(支付宝)/WECHAT(微信)/BANK(银行卡)

    /** 支付描述（可选） */
    private String description;  // 支付描述(选填),如"购买商品xxx"

    // —— merchantId 字段的 getter/setter ——
    public Long getMerchantId() {  // 获取商户 ID
        return merchantId;  // 返回商户 ID
    }

    public void setMerchantId(Long merchantId) {  // 设置商户 ID
        this.merchantId = merchantId;  // 赋值商户 ID
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

    // —— description 字段的 getter/setter ——
    public String getDescription() {  // 获取支付描述
        return description;  // 返回支付描述
    }

    public void setDescription(String description) {  // 设置支付描述
        this.description = description;  // 赋值支付描述
    }
}
