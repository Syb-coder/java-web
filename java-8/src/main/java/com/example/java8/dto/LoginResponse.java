package com.example.java8.dto;

import com.example.java8.model.AdminUser;

/**
 * 登录响应 DTO
 * <p>登录成功后返回当前用户的基本信息（不含密码），供前端展示与登录态校验。</p>
 *
 * @param id       用户 ID
 * @param username 用户名
 * @param nickname 昵称
 */
public record LoginResponse(Long id, String username, String nickname) {

    /**
     * 从管理员实体构造响应
     *
     * @param admin 管理员实体
     * @return 登录响应
     */
    public static LoginResponse from(AdminUser admin) {
        return new LoginResponse(admin.getId(), admin.getUsername(), admin.getNickname());
    }

    /**
     * 从前台用户实体构造响应
     *
     * @param user      前台用户实体
     * @param isAdmin   是否为管理员（此处保留 false，仅供前台用户使用）
     * @return 登录响应
     */
    public static LoginResponse from(com.example.java8.model.User user, boolean isAdmin) {
        return new LoginResponse(user.getId(), user.getUsername(), user.getNickname());
    }
}
