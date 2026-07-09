package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入非空字符串校验注解
import jakarta.validation.constraints.NotNull;  // 导入非 null 校验注解

/**
 * 商户审核请求 DTO
 * <p>
 * 运营人员对入驻商户进行审核，action 取值 APPROVE（通过）/REJECT（驳回）。
 * 驳回时需填写 rejectReason。
 * </p>
 */
public class MerchantAuditRequest {  // 商户审核请求 DTO 类定义

    /** 待审核商户 ID */
    @NotNull(message = "商户 ID 不能为空")  // 非 null 校验:商户 ID 必填
    private Long merchantId;  // 待审核的商户 ID

    /** 审核动作：APPROVE/REJECT */
    @NotBlank(message = "审核动作不能为空")  // 非空字符串校验:审核动作必填
    private String action;  // 审核动作:APPROVE(通过)/REJECT(驳回)

    /** 驳回原因（REJECT 时必填） */
    private String rejectReason;  // 驳回原因,仅 action=REJECT 时必填(服务端校验)

    // —— merchantId 字段的 getter/setter ——
    public Long getMerchantId() {  // 获取商户 ID
        return merchantId;  // 返回商户 ID
    }

    public void setMerchantId(Long merchantId) {  // 设置商户 ID
        this.merchantId = merchantId;  // 赋值商户 ID
    }

    // —— action 字段的 getter/setter ——
    public String getAction() {  // 获取审核动作
        return action;  // 返回审核动作
    }

    public void setAction(String action) {  // 设置审核动作
        this.action = action;  // 赋值审核动作
    }

    // —— rejectReason 字段的 getter/setter ——
    public String getRejectReason() {  // 获取驳回原因
        return rejectReason;  // 返回驳回原因
    }

    public void setRejectReason(String rejectReason) {  // 设置驳回原因
        this.rejectReason = rejectReason;  // 赋值驳回原因
    }
}
