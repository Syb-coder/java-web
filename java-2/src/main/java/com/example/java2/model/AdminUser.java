// 声明包路径
package com.example.java2.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 后台管理员实体
 * <p>
 * 用于平台后台运维：管理文章、分类、题库、用户、评论。
 * 密码使用 BCrypt 加密存储，初始化时通过 DataInitializer 注入默认账号。
 * </p>
 * <p>
 * 实体关系：与前台 User 物理隔离（独立表 + 独立 Session 域），
 * 不与业务实体建立外键关联，避免管理员账号变动影响业务数据完整性。
 * </p>
 */
@Entity  // 标识为 JPA 实体
// 表名 admin_user 与 users 物理分离：管理员与普通用户权限域不同，独立表便于权限隔离与审计
@Table(name = "admin_user")
public class AdminUser {

    /** 主键 ID，自增 */
    // IDENTITY 策略：依赖数据库自增列，与 User 表保持一致，便于运维统一管理
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录用户名（唯一） */
    // nullable=false+unique=true：管理员账号全局唯一，防止重复创建导致登录身份混乱；length=50：管理员账号简短
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt 加密后的密码 */
    // length=100：BCrypt 密文 60 字符，留余量兼容未来算法升级，与 User.password 保持一致
    @Column(nullable = false, length = 100)
    private String password;

    /** 展示名称，如"超级管理员" */
    // length=50：展示名称如"超级管理员"足够，无长文案需求
    @Column(length = 50)
    private String displayName;

    /** 最近登录时间 */
    private LocalDateTime lastLoginAt;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 最后修改时间 */
    private LocalDateTime updateTime;

    /** 无参构造方法：JPA 规范要求 */
    public AdminUser() {
    }

    /**
     * 全参构造方法
     *
     * @param username    用户名
     * @param password    已 BCrypt 加密的密码
     * @param displayName 展示名称
     */
    public AdminUser(String username, String password, String displayName) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    /**
     * 获取管理员密码密文。
     * 业务约束：返回值为 BCrypt 密文，禁止明文外泄；
     * 管理员密码具备后台高权限，泄露将危及全站数据安全，故严禁日志记录。
     */
    public String getPassword() { return password; }

    /**
     * 设置管理员密码。
     * 业务约束：调用方需先完成 BCrypt 加密；建议改密后强制下线所有管理员 Session。
     */
    public void setPassword(String password) { this.password = password; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
