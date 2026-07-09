package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.DecimalMin;  // 导入最小值校验注解(支持 BigDecimal)
import jakarta.validation.constraints.NotNull;  // 导入非 null 校验注解

import java.math.BigDecimal;  // 导入高精度十进制类,用于金额表示

/**
 * 金额操作请求 DTO
 * <p>
 * 充值与提现共用此 DTO，通过接口区分业务类型，
 * 金额必须大于 0.01 元。
 * </p>
 */
public class AmountRequest {  // 金额操作请求 DTO 类定义(充值/提现共用)

    /** 操作金额（元），最小 0.01 */
    @NotNull(message = "金额不能为空")  // 非 null 校验:仅拦截 null,允许空字符串(此处针对对象类型)
    @DecimalMin(value = "0.01", message = "金额必须大于 0.01")  // 最小值校验:金额不得低于 0.01 元
    private BigDecimal amount;  // 操作金额(元),使用 BigDecimal 避免浮点精度丢失

    /** 备注（可选） */
    private String remark;  // 业务备注(选填),如"工资充值""日常提现"

    // —— amount 字段的 getter/setter ——
    public BigDecimal getAmount() {  // 获取操作金额
        return amount;  // 返回操作金额
    }

    public void setAmount(BigDecimal amount) {  // 设置操作金额
        this.amount = amount;  // 赋值操作金额
    }

    // —— remark 字段的 getter/setter ——
    public String getRemark() {  // 获取备注
        return remark;  // 返回备注
    }

    public void setRemark(String remark) {  // 设置备注
        this.remark = remark;  // 赋值备注
    }
}
