// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

// 导入 JPA（Jakarta Persistence API）全部注解，包含 @Entity、@Table、@Id 等，用于将 POJO 映射为数据库实体
import jakarta.persistence.*;

// 导入 LocalDateTime 时间类型，用于记录管理员账号创建时间
import java.time.LocalDateTime;

/**
 * 管理员用户实体
 * <p>
 * 平台管理员账号，用于后台审核商品、封禁用户、发布公告等管控操作。
 * </p>
 */
// 标识当前类为 JPA 实体，Hibernate 会将其映射为数据库表
@Entity
// 指定表名为 admin_users，与前台用户表 users 区分
@Table(name = "admin_users")
public class AdminUser {

    /** 主键，自增 */
    // 声明该字段为数据库主键
    @Id
    // 主键生成策略：IDENTITY 表示由数据库自增分配
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 管理员用户名（唯一） */
    // 非空且唯一，长度上限 50；管理员登录凭证
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt 加密后的密码 */
    // 非空，长度上限 100；存储 BCrypt 哈希密文
    @Column(nullable = false, length = 100)
    private String password;

    /** 显示名称 */
    // 可空，长度上限 50；用于后台界面展示
    @Column(length = 50)
    private String displayName;

    /** 创建时间 */
    // 非空，账号创建时写入
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法（JPA 要求） */
    // JPA 规范要求实体必须提供无参构造方法，便于通过反射实例化
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
        // 设置管理员登录用户名
        this.username = username;
        // 设置已加密密码（业务层 BCrypt 处理后传入）
        this.password = password;
        // 设置后台展示名称
        this.displayName = displayName;
        // 创建时间取当前系统时间
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    // 获取主键 ID
    public Long getId() {
        return id;
    }

    // 设置主键 ID，通常由 JPA 自动填充
    public void setId(Long id) {
        this.id = id;
    }

    // 获取管理员用户名，用于登录校验
    public String getUsername() {
        return username;
    }

    // 设置管理员用户名
    public void setUsername(String username) {
        this.username = username;
    }

    // 获取已加密密码
    public String getPassword() {
        return password;
    }

    // 设置密码（须为已 BCrypt 加密的密文）
    public void setPassword(String password) {
        this.password = password;
    }

    // 获取显示名称，用于后台界面展示
    public String getDisplayName() {
        return displayName;
    }

    // 设置显示名称
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    // 获取账号创建时间
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 设置账号创建时间
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
