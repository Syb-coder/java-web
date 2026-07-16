package com.example.java12.dto;  // DTO 层包

import jakarta.validation.constraints.NotBlank;  // 非空校验
import jakarta.validation.constraints.Size;  // 长度校验

/**
 * 修改密码请求 DTO
 */
public class ChangePasswordRequest {

    /** 旧密码（需验证身份） */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /** 新密码（≥6字符） */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 50, message = "新密码长度需至少6位")
    private String newPassword;

    /** 确认新密码 */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
