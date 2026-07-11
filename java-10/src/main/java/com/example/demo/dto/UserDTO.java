package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * 用户数据传输对象
 * <p>
 * 为什么用 DTO 而非直接返回 User 实体：实体包含 password 字段，
 * 通过 DTO 转换时排除密码，保证安全。同时 DTO 可灵活控制字段暴露范围。
 * 为什么 password 用 @JsonProperty(WRITE_ONLY)：
 * 允许前端请求体中传入密码（反序列化），但响应时不会输出密码（序列化跳过），
 * 从根本上杜绝密码泄露。
 * </p>
 */
public class UserDTO {

    /** 用户 ID（响应时返回，请求时忽略） */
    private Long id;

    /** 用户名（创建时必填） */
    private String username;

    /**
     * 密码（仅创建/更新时接收，响应时不输出）
     * WRITE_ONLY：Jackson 反序列化时读取此字段，序列化时跳过此字段
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 角色：前端使用小写（admin/teacher/student），后端存储大写 */
    private String role;

    /** 联系电话 */
    private String phone;

    /** 创建时间（响应时返回） */
    private LocalDateTime createdAt;

    /** 更新时间（响应时返回） */
    private LocalDateTime updatedAt;

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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
