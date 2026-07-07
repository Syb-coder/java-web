// 声明包路径
package com.example.java8.model;

// 导入 JPA 列注解
import jakarta.persistence.Column;
// 导入 JPA 实体注解
import jakarta.persistence.Entity;
// 导入主键自增策略注解
import jakarta.persistence.GeneratedValue;
// 导入主键生成策略枚举
import jakarta.persistence.GenerationType;
// 导入主键注解
import jakarta.persistence.Id;
// 导入表名注解
import jakarta.persistence.Table;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 管理员用户实体
 * <p>
 * 后台管理系统操作员的账号信息。密码使用 BCrypt 加密存储，不可逆。
 * 与前台 {@link User} 分表存储，权限完全隔离——管理员不能下单，前台用户不能进入后台。
 * </p>
 */
// @Entity 标识该类为 JPA 实体，Hibernate 启动时会根据其结构生成 DDL
@Entity
// @Table 指定表名（默认为类名首字母小写，此处显式指定避免命名歧义）
@Table(name = "admin_users")
public class AdminUser {

    /** 主键 ID，自增 */
    // @Id 标识主键字段
    @Id
    // @GeneratedValue 指定主键生成策略为数据库自增（H2 支持 IDENTITY）
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录用户名（唯一） */
    // @Column 配置列约束：非空、唯一、长度 50
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt 加密后的密码 */
    // 密码字段长度设为 100，BCrypt 加密结果固定为 60 字符，预留扩展空间
    @Column(nullable = false, length = 100)
    private String password;

    /** 昵称，用于显示 */
    @Column(length = 50)
    private String nickname;

    /** 最近登录时间，登录成功时由 AuthService 更新 */
    private LocalDateTime lastLoginAt;

    /** 创建时间，注册时设置 */
    private LocalDateTime createTime;

    /** 无参构造方法：JPA 规范要求，Hibernate 实例化实体时调用 */
    public AdminUser() {
    }

    /**
     * 全参构造方法：用于 DataInitializer 初始化管理员
     *
     * @param username 用户名
     * @param password 已 BCrypt 加密的密码
     * @param nickname 昵称
     */
    public AdminUser(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.createTime = LocalDateTime.now();
    }

    // ===== 以下是各字段的 Getter / Setter =====
    // JPA 通过反射调用 Setter 完成实体属性注入，通过 Getter 读取字段值
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
