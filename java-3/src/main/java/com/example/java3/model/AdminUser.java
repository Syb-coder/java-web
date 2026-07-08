package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 管理员用户实体
 * <p>
 * 平台管理员账号，用于后台审核商品、封禁用户、发布公告等管控操作。
 * </p>
 */
@Entity
@Table(name = "admin_users")
public class AdminUser {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 管理员用户名（唯一） */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt 加密后的密码 */
    @Column(nullable = false, length = 100)
    private String password;

    /** 显示名称 */
    @Column(length = 50)
    private String displayName;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法（JPA 要求） */
    public AdminUser() {
    }

    /**
     * 业务构造方法
     *
     * @param username    管理员用户名
     * @param password    已加密密码
     * @param displayName 显示名称
     */
    public AdminUser(String username, String password, String displayName) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
