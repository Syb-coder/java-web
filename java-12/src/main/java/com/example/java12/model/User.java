package com.example.java12.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;  // JPA 注解
import java.time.LocalDateTime;  // 时间类型

/**
 * 用户实体
 * <p>
 * 对应 users 表，存储网文论坛注册用户的账户信息与个人资料。
 * 包含登录凭证、个人资料、发帖计数与账户状态等字段。
 * </p>
 */
@Entity  // 声明为 JPA 实体
@Table(name = "users")  // 映射到 users 表
public class User {

    /** 主键 ID，自增 */
    @Id  // 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增策略
    private Long id;

    /** 登录账号（字母数字组合，4-20字符，唯一） */
    @Column(nullable = false, unique = true, length = 20)  // 非空、唯一、长度限制
    private String account;

    /** 昵称（2-15字符，唯一，用于前台展示） */
    @Column(nullable = false, unique = true, length = 15)
    private String nickname;

    /** BCrypt 加密后的密码 */
    @Column(nullable = false, length = 100)
    private String password;

    /** 头像 URL（默认占位图） */
    @Column(length = 500)
    private String avatar;

    /** 个性签名（选填） */
    @Column(length = 200)
    private String signature;

    /** 用户角色：USER 普通用户 / ADMIN 管理员 */
    @Enumerated(EnumType.STRING)  // 枚举以字符串形式存储
    @Column(nullable = false, length = 10)
    private Role role;

    /** 账户状态：NORMAL 正常 / BANNED 封禁 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UserStatus status;

    /** 发帖数（冗余计数，避免频繁 count 查询） */
    @Column(nullable = false)
    private Integer postCount;

    /** 昵称最后修改时间（PRD 规则：30天内仅可修改一次） */
    private LocalDateTime nicknameUpdateTime;

    /** 注册时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

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
     * @param account  登录账号
     * @param nickname 昵称
     * @param password BCrypt 加密后的密码
     */
    public User(String account, String nickname, String password) {
        this.account = account;
        this.nickname = nickname;
        this.password = password;
        this.role = Role.USER;
        this.status = UserStatus.NORMAL;
        this.postCount = 0;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新前回调，自动刷新 updateTime
     */
    @PreUpdate  // JPA 生命周期回调：update 前触发
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    // ===== getter / setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public LocalDateTime getNicknameUpdateTime() {
        return nicknameUpdateTime;
    }

    public void setNicknameUpdateTime(LocalDateTime nicknameUpdateTime) {
        this.nicknameUpdateTime = nicknameUpdateTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
