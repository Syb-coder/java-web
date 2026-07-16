package com.example.java11.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 注册请求 DTO
 * <p>
 * 用于接收用户注册提交的信息，包含用户名、密码、邮箱三个字段。
 * 用户名长度限制为 3-20 字符，密码长度限制为 6-32 字符，邮箱需符合标准格式。
 * </p>
 */
public class RegisterRequest {

    /** 用户名（3-20 字符） */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度需在 3-20 字符之间")
    private String username;

    /** 密码（6-32 字符） */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度需在 6-32 字符之间")
    private String password;

    /** 邮箱地址 */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 默认无参构造器
     */
    public RegisterRequest() {
    }

    /**
     * 全参构造器
     *
     * @param username 用户名
     * @param password 密码
     * @param email    邮箱地址
     */
    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
