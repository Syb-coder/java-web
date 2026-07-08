package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 前台学生用户实体
 * <p>
 * 学号实名认证机制：studentId 唯一约束，作为校园身份标识。
 * 密码采用 BCrypt 加密存储，不保存明文。
 * </p>
 */
@Entity
@Table(name = "users")
public class User {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 学号（唯一），作为实名认证依据 */
    @Column(nullable = false, unique = true, length = 20)
    private String studentId;

    /** 登录用户名（唯一） */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt 加密后的密码 */
    @Column(nullable = false, length = 100)
    private String password;

    /** 昵称，用于前台展示 */
    @Column(length = 50)
    private String nickname;

    /** 联系电话（可选） */
    @Column(length = 20)
    private String phone;

    /** 头像 URL（可选） */
    @Column(length = 255)
    private String avatar;

    /** 账号状态：ACTIVE/BANNED */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    /** 注册时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法（JPA 要求） */
    public User() {
    }

    /**
     * 业务构造方法：注册时使用
     *
     * @param studentId 学号
     * @param username  用户名
     * @param password  已加密密码
     * @param nickname  昵称
     */
    public User(String studentId, String username, String password, String nickname) {
        this.studentId = studentId;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
