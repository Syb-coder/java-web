package com.example.java12.dto;  // DTO 层包

import jakarta.validation.constraints.NotBlank;  // 非空校验
import jakarta.validation.constraints.Size;  // 长度校验

/**
 * 登录请求 DTO
 */
public class LoginRequest {

    /** 登录账号 */
    @NotBlank(message = "账号不能为空")
    @Size(min = 4, max = 20, message = "账号长度需为4-20字符")
    private String account;

    /** 密码（明文，后端校验后不存储） */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度需至少6位")
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
