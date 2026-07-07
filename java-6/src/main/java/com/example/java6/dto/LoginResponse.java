package com.example.java6.dto;

import com.example.java6.model.AdminUser;

/**
 * 登录响应 DTO
 *
 * @param id          管理员 ID
 * @param username    用户名
 * @param displayName 显示名称
 */
public record LoginResponse(
        Long id,
        String username,
        String displayName
) {
    /**
     * 从实体构造响应
     *
     * @param user 管理员实体
     * @return 登录响应
     */
    public static LoginResponse from(AdminUser user) {
        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName()
        );
    }
}
