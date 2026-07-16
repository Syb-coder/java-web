package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 修改密码请求 DTO
 * <p>
 * 为什么用独立 DTO：修改密码需要验证原密码，字段语义与登录请求不同，职责单一。
 * 为什么三个字段都加 @NotBlank：用户名、原密码、新密码均为必填，空值无业务意义。
 * </p>
 */
public class ChangePasswordRequest {

    /** 用户名：定位待修改密码的账号 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 原密码（明文）：用于身份验证，确认操作者持有正确凭证 */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /** 新密码（明文）：将加密后存储，替代原密码 */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
