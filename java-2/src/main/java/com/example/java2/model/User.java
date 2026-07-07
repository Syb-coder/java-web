// 声明包路径
package com.example.java2.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 前台用户实体
 * <p>
 * 注册并使用网络安全学习平台的普通用户账号。密码使用 BCrypt 加密存储。
 * 一个用户可收藏多篇文章、点赞多篇文章、发表多条评论、留下多条答题记录。
 * </p>
 * <p>
 * 实体关系：User 与 Article 为多对多（通过 Favorite / ArticleLike 中间表承载）；
 * User 与 Comment 为一对多；User 与 TestRecord 为一对多。本表为前台用户体系核心表。
 * </p>
 */
@Entity  // 标识为 JPA 实体，由 EntityManager 统一管理生命周期
// 表名取 users 而非 user：避免与多数 SQL 方言中的保留字 USER 冲突，防止 DDL 执行失败
@Table(name = "users")
public class User {

    /** 主键 ID，自增 */
    // IDENTITY 策略依赖数据库自增列，简化插入流程；不选 SEQUENCE 是为兼容 MySQL/SQLite 等无序列的方言
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录用户名（唯一） */
    // nullable=false+unique=true：注册必填且全局唯一，防止重复账号注册；length=50：覆盖常见用户名长度上限
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt 加密后的密码 */
    // length=100：BCrypt 密文固定 60 字符，留余量兼容未来切换更强哈希算法（如 Argon2）
    @Column(nullable = false, length = 100)
    private String password;

    /** 昵称，用于展示 */
    // length=50：昵称为展示用短文本，与 username 同长度足够，无需更大字段
    @Column(length = 50)
    private String nickname;

    /** 账号状态：true 正常，false 禁用（被管理员封禁） */
    // nullable=false：boolean 基本类型默认 false，显式约束避免 JPA 空值歧义与封禁逻辑误判
    @Column(nullable = false)
    private boolean enabled = true;

    /** 最近登录时间 */
    private LocalDateTime lastLoginAt;

    /** 注册时间 */
    private LocalDateTime createTime;

    /** 无参构造方法：JPA 规范要求 */
    public User() {
    }

    /**
     * 全参构造方法（不含自动赋值字段）
     *
     * @param username 用户名
     * @param password 已 BCrypt 加密的密码
     * @param nickname 昵称
     */
    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    /**
     * 获取密码密文。
     * 业务约束：返回值始终为 BCrypt 加密后的密文，禁止返回明文；
     * 调用方不得将其输出到日志或前端响应，避免 Session 劫持与凭证泄露。
     */
    public String getPassword() { return password; }

    /**
     * 设置密码。
     * 业务约束：调用方必须先经 BCrypt 加密后再传入，本方法不做哈希处理，
     * 防止重复哈希导致校验失败。
     */
    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    /**
     * 判断账号是否启用。
     * 业务约束：false 表示被管理员封禁，此时登录校验应直接拒绝；
     * 已登录 Session 在 enabled 切换为 false 后应被失效，避免封禁用户继续操作。
     */
    public boolean isEnabled() { return enabled; }

    /**
     * 设置账号启用状态。
     * 业务约束：仅后台管理员可调用，调用后需同步清理该用户的活跃 Session。
     */
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
