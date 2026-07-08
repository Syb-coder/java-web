package com.example.java3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 商品审核请求 DTO
 */
public class AuditRequest {

    /** 审核结果：APPROVED / REJECTED */
    @NotBlank(message = "审核结果不能为空")
    private String status;

    /** 审核备注（驳回时必填） */
    @Size(max = 500, message = "审核备注不能超过 500 字")
    private String remark;

    // ===== Getter / Setter =====

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
