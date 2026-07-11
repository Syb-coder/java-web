package com.example.java9.model;  // 实体类所在包，归属于 model 层

// JPA 持久化相关注解导入
import jakarta.persistence.Column;  // 字段列映射注解，控制 nullable/unique/length 等约束
import jakarta.persistence.Entity;  // 标识类为 JPA 实体，对应数据库表
import jakarta.persistence.EnumType;  // 枚举映射类型枚举（ORDINAL/STRING）
import jakarta.persistence.Enumerated;  // 指定枚举在数据库中的存储方式
import jakarta.persistence.GeneratedValue;  // 主键生成策略注解
import jakarta.persistence.GenerationType;  // 主键生成策略枚举
import jakarta.persistence.Id;  // 主键标识注解
import jakarta.persistence.PreUpdate;  // 更新前回调注解
import jakarta.persistence.Table;  // 指定实体对应的数据库表名

// JDK 时间类型导入
import java.time.LocalDateTime;  // 用于记录创建/更新时间戳

/**
 * 管理员用户实体
 * <p>
 * 对应 admin_users 表，存储运营管理员与风控专员账号信息。
 * 密码使用 BCrypt 加密存储，避免明文落库。
 * </p>
 */
@Entity  // JPA 实体标识，Hibernate 会扫描并管理该类的 ORM 映射
@Table(name = "admin_users")  // 映射到 admin_users 表，与 C 端 users 表区分
public class AdminUser {  // 后台管理员实体，承载登录凭证与角色信息

    /** 主键 ID，自增 */
    @Id  // JPA 主键标识，标记该字段为数据库主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略，由数据库分配
    private Long id;  // 主键 ID，自增长

    /** 登录用户名（唯一） */
    @Column(nullable = false, unique = true, length = 50)  // 非空且唯一，字段长度50
    private String username;  // 登录用户名，全局唯一，用于登录认证

    /** BCrypt 加密后的密码 */
    @Column(nullable = false, length = 100)  // 非空，长度100以容纳 BCrypt 加密后的固定长度串
    private String password;  // 密码字段，存储 BCrypt 加密结果，禁止明文落库

    /** 真实姓名 */
    @Column(length = 50)  // 字段长度50，允许为空（创建时可后补）
    private String realName;  // 管理员真实姓名，用于审计日志展示

    /** 角色：OPERATION 运营 / RISK 风控 */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储，便于 SQL 排查与避免 ORDINAL 顺序变更风险
    @Column(nullable = false, length = 20)  // 非空，长度20覆盖枚举名长度
    private AdminRole role;  // 管理员角色，用于权限拦截与菜单分发

    /** 创建时间 */
    @Column(nullable = false)  // 非空，确保每次创建都有时间记录用于审计
    private LocalDateTime createdAt;  // 创建时间戳，由构造器自动赋值

    /** 最后修改时间 */
    private LocalDateTime updateTime;  // 最后修改时间戳

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public AdminUser() {  // JPA 要求的无参构造器，供 Hibernate 反射实例化使用
    }

    public AdminUser(String username, String password, String realName, AdminRole role) {  // 业务构造器，创建时必填字段一次赋值
        this.username = username;  // 赋值用户名
        this.password = password;  // 赋值密码（调用方应先 BCrypt 加密再传入）
        this.realName = realName;  // 赋值真实姓名
        this.role = role;  // 赋值角色
        this.createdAt = LocalDateTime.now();  // 创建时间由服务端生成，避免依赖数据库默认值
        this.updateTime = LocalDateTime.now();  // 初始化修改时间
    }

    public Long getId() {  // 获取主键 ID
        return id;
    }

    public void setId(Long id) {  // 设置主键 ID（一般由 JPA 维护，业务层不应主动调用）
        this.id = id;
    }

    public String getUsername() {  // 获取登录用户名
        return username;
    }

    public void setUsername(String username) {  // 设置登录用户名
        this.username = username;
    }

    public String getPassword() {  // 获取加密后的密码，用于登录校验
        return password;
    }

    public void setPassword(String password) {  // 设置密码，调用方需保证已 BCrypt 加密
        this.password = password;
    }

    public String getRealName() {  // 获取真实姓名
        return realName;
    }

    public void setRealName(String realName) {  // 设置真实姓名
        this.realName = realName;
    }

    public AdminRole getRole() {  // 获取管理员角色，用于权限拦截器判定
        return role;
    }

    public void setRole(AdminRole role) {  // 设置管理员角色
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
