package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户实体：系统账号信息
 * <p>
 * 为什么用 @Table(name = "users")：user 在某些数据库是保留关键字，使用 users 避免冲突
 * 为什么 password 字段不在 JSON 中返回：安全考虑，密码哈希值不应暴露给前端
 * 为什么 role 存大写 ADMIN/TEACHER/STUDENT：与数据库常量约定一致，API 层负责大小写转换
 * 为什么手写 getter/setter：项目未引入 Lombok，避免额外依赖
 * </p>
 */
@Entity
@Table(name = "users")
public class User {

    /** 主键 ID，自增策略，数据库层面保证唯一性 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录用户名，全局唯一，长度限制 50 */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** 密码哈希值（BCrypt 加密），长度 100 容纳 BCrypt 输出（固定 60 字符，留余量） */
    @Column(nullable = false, length = 100)
    private String password;

    /** 真实姓名，用于展示 */
    @Column(name = "real_name", length = 50)
    private String realName;

    /** 角色：ADMIN / TEACHER / STUDENT，大写存储 */
    @Column(nullable = false, length = 20)
    private String role;

    /** 联系电话，11 位手机号 */
    @Column(length = 20)
    private String phone;

    /** 创建时间：由 Hibernate 自动填充，不可更新 */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间：每次修改自动刷新 */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 无参构造：JPA 规范要求，Hibernate 实例化实体时调用 */
    public User() {
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
