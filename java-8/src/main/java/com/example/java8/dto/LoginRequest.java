package com.example.java8.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 通用登录请求 DTO
 * <p>管理员与前台用户复用同一结构。</p>
 *
 * @param username 用户名
 * @param password 明文密码
 */
public record LoginRequest(
        @NotBlank(message = "用户名不能为空") String username,
        @NotBlank(message = "密码不能为空") String password
) {
}
