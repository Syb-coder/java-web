package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求 DTO
 * <p>
 * 为什么用独立 DTO 而非 User 实体：避免将密码等敏感字段暴露在请求体中，
 * 同时利用 @Valid 注解进行参数校验，职责单一。
 * </p>
 */
public class LoginRequest {

    /** 用户名：登录时必填 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码：登录时必填 */
    @NotBlank(message = "密码不能为空")
    private String password;

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
