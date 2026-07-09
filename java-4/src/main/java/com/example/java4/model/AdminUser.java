// 声明包路径，存放 JPA 实体类
package com.example.java4.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 管理员实体
 * <p>
 * 后台管理端的登录主体，拥有图书管理、分类管理、读者管理、借阅审核等权限。
 * 密码使用 BCrypt 加密存储，与读者实体分离，职责清晰。
 * </p>
 * <p>
 * 设计要点：
 * 1. username 唯一约束防止重复注册；
 * 2. 密码字段长度 100，BCrypt 密文 60 字符，预留扩展空间；
 * 3. createTime 记录账号建立时间，用于审计。
 * </p>
 */
@Entity // 声明本类为 JPA 实体，Hibernate 会为其创建 ORM 映射
@Table(name = "admin_users") // 指定映射表名为 admin_users，复数命名体现一条记录代表一位管理员
public class AdminUser {

    /** 主键 ID，自增 */
    @Id // 声明该字段为数据库主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键生成策略为数据库自增
    private Long id;

    /** 管理员用户名（唯一，作为登录账号） */
    @Column(nullable = false, unique = true, length = 50) // 非空、唯一、长度 50
    private String username;

    /** 登录密码（BCrypt 加密） */
    @Column(nullable = false, length = 100) // 非空、长度 100，BCrypt 密文 60 字符预留扩展
    private String password;

    /** 管理员真实姓名 */
    @Column(nullable = false, length = 50) // 非空、长度 50
    private String realName;

    /** 最近登录时间 */
    private LocalDateTime lastLoginAt;

    /** 账号创建时间 */
    @Column(nullable = false, updatable = false) // 非空、不可更新
    private LocalDateTime createTime;

    /** 最后修改时间，更新时由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /** 更新前自动设置最后修改时间 */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /** 无参构造方法，JPA 规范要求 */
    public AdminUser() {
    }

    /**
     * 业务构造方法：创建新管理员时使用
     *
     * @param username 用户名
     * @param password 已 BCrypt 加密的密码
     * @param realName 真实姓名
     */
    public AdminUser(String username, String password, String realName) {
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
