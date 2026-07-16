package com.example.java11.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求 DTO
 * <p>
 * 用于接收用户登录提交的凭证信息，包含用户名与密码两个字段。
 * 字段均通过 jakarta.validation 进行非空校验。
 * </p>
 */
public class LoginRequest {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 默认无参构造器
     */
    public LoginRequest() {
    }

    /**
     * 全参构造器
     *
     * @param username 用户名
     * @param password 密码
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

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
