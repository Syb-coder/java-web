package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.DecimalMin;  // 导入最小值校验注解(支持 BigDecimal)
import jakarta.validation.constraints.NotNull;  // 导入非 null 校验注解

import java.math.BigDecimal;  // 导入高精度十进制类,用于金额表示

/**
 * 投资申购请求 DTO
 * <p>
 * C 端用户对指定理财产品发起申购，
 * 申购金额不得低于产品起投金额且不小于 0.01 元。
 * </p>
 */
public class InvestRequest {  // 投资申购请求 DTO 类定义

    /** 目标理财产品 ID */
    @NotNull(message = "产品 ID 不能为空")  // 非 null 校验:产品 ID 必填
    private Long productId;  // 目标理财产品 ID

    /** 申购金额（元），最小 0.01 */
    @NotNull(message = "申购金额不能为空")  // 非 null 校验:申购金额必填
    @DecimalMin(value = "0.01", message = "申购金额必须大于 0.01")  // 最小值校验:申购金额不得低于 0.01 元
    private BigDecimal amount;  // 申购金额(元),服务端还会校验是否达到产品起投金额

    // —— productId 字段的 getter/setter ——
    public Long getProductId() {  // 获取产品 ID
        return productId;  // 返回产品 ID
    }

    public void setProductId(Long productId) {  // 设置产品 ID
        this.productId = productId;  // 赋值产品 ID
    }

    // —— amount 字段的 getter/setter ——
    public BigDecimal getAmount() {  // 获取申购金额
        return amount;  // 返回申购金额
    }

    public void setAmount(BigDecimal amount) {  // 设置申购金额
        this.amount = amount;  // 赋值申购金额
    }
}
