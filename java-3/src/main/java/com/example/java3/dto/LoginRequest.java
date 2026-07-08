package com.example.java3.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求 DTO（学生与管理员共用）
 */
public class LoginRequest {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码（明文） */
    @NotBlank(message = "密码不能为空")
    private String password;

    // ===== Getter / Setter =====

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
