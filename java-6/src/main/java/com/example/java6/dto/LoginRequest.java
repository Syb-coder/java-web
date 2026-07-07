package com.example.java6.dto;

/**
 * 登录请求 DTO
 *
 * @param username 用户名
 * @param password 密码（明文，传输后立即用于校验）
 */
public record LoginRequest(
        String username,
        String password
) {
}
