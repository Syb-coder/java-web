package com.example.java6.dto;

/**
 * 修改密码请求 DTO
 *
 * @param oldPassword 旧密码（明文，需校验）
 * @param newPassword 新密码（明文，将与旧密码不同）
 */
public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {
}
