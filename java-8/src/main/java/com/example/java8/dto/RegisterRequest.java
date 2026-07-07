package com.example.java8.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户注册请求 DTO
 *
 * @param username 用户名
 * @param password 明文密码（至少 6 位）
 * @param nickname 昵称
 * @param phone    手机号（可选）
 */
public record RegisterRequest(
        @NotBlank(message = "用户名不能为空") String username,
        @NotBlank(message = "密码不能为空") String password,
        @NotBlank(message = "昵称不能为空") String nickname,
        String phone
) {
}
