package com.example.java3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 纠纷处理请求 DTO
 */
public class DisputeRequest {

    /** 纠纷处理备注 */
    @NotBlank(message = "处理备注不能为空")
    @Size(max = 500, message = "备注不能超过 500 字")
    private String remark;

    // ===== Getter / Setter =====

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
