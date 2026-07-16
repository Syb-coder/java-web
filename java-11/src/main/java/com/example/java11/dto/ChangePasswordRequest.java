package com.example.java11.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改密码请求 DTO
 * <p>
 * 用于接收用户修改密码时提交的旧密码与新密码。
 * 新密码长度最少 6 位，保障账户安全性。
 * </p>
 */
public class ChangePasswordRequest {

    /** 旧密码 */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /** 新密码（最少 6 位） */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "新密码长度需在 6-32 字符之间")
    private String newPassword;

    /**
     * 默认无参构造器
     */
    public ChangePasswordRequest() {
    }

    /**
     * 全参构造器
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

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
}
