package com.example.java11.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 普通用户实体
 * <p>
 * 对应 users 表，存储二次元讨论网站注册用户的账户信息与个人资料。
 * 包含登录凭证、个人资料、社交计数与账户状态等核心字段。
 * </p>
 */
@Entity
@Table(name = "users")
public class User {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录用户名（唯一） */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt 加密后的密码 */
    @Column(nullable = false, length = 100)
    private String password;

    /** 邮箱地址 */
    @Column(length = 100)
    private String email;

    /** 昵称，用于前台展示 */
    @Column(length = 50)
    private String nickname;

    /** 头像 URL */
    @Column(length = 500)
    private String avatar;

    /** 个性签名 */
    @Column(length = 200)
    private String signature;

    /** 个人简介 */
    @Column(length = 1000)
    private String bio;

    /** 发帖数 */
    @Column(nullable = false)
    private Integer postCount;

    /** 粉丝数 */
    @Column(nullable = false)
    private Integer followerCount;

    /** 关注数 */
    @Column(nullable = false)
    private Integer followingCount;

    /** 账户状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    /** 注册时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public User() {
    }

    /**
     * 业务构造器：注册新用户时使用
     *
     * @param username 登录用户名
     * @param password BCrypt 加密后的密码
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = UserStatus.NORMAL;
        this.postCount = 0;
        this.followerCount = 0;
        this.followingCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新前回调，自动刷新 updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
