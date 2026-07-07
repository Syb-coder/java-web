package com.example.java8.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 修改密码请求 DTO
 *
 * @param oldPassword 旧密码（明文）
 * @param newPassword 新密码（明文，至少 6 位）
 */
public record ChangePasswordRequest(
        @NotBlank(message = "旧密码不能为空") String oldPassword,
        @NotBlank(message = "新密码不能为空") String newPassword
) {
}
