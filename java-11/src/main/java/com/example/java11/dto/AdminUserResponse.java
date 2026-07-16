package com.example.java11.dto;

import java.time.LocalDateTime;

/**
 * 管理员用户响应 DTO
 * <p>
 * 用于返回管理员账户的基本信息，包含用户名、角色及时间信息。
 * 主要用于管理后台的管理员列表展示。
 * </p>
 */
public class AdminUserResponse {

    /** 管理员 ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 角色（ADMIN/MODERATOR） */
    private String role;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 默认无参构造器
     */
    public AdminUserResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
