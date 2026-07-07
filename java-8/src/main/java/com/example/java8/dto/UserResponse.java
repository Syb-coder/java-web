package com.example.java8.dto;

import java.time.LocalDateTime;

/**
 * 前台用户响应 DTO
 *
 * @param id          用户 ID
 * @param username    用户名
 * @param nickname    昵称
 * @param phone       手机号
 * @param createTime  注册时间
 * @param lastLoginAt 最近登录时间
 */
public record UserResponse(Long id, String username, String nickname, String phone,
                           LocalDateTime createTime, LocalDateTime lastLoginAt) {
}
