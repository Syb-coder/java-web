package com.example.java6.model; // 定义实体类所在包,model 包存放所有 JPA 实体与枚举

import jakarta.persistence.Column; // 导入 JPA @Column 注解,用于自定义数据库列属性(长度、可空、唯一性等)
import jakarta.persistence.Entity; // 导入 JPA @Entity 注解,标记类为数据库表映射实体
import jakarta.persistence.GeneratedValue; // 导入 JPA @GeneratedValue 注解,指定主键生成策略
import jakarta.persistence.GenerationType; // 导入主键生成策略枚举,IDENTITY 表示由数据库自增
import jakarta.persistence.Id; // 导入 JPA @Id 注解,标记字段为主键
import jakarta.persistence.Table; // 导入 JPA @Table 注解,用于显式指定数据库表名

import java.time.LocalDateTime; // 导入 JDK 8+ 日期时间 API,用于记录管理员创建与最近登录时间

/**
 * 管理员账号实体
 *
 * <p>存储后台管理员登录凭证，密码采用 BCrypt 加密存储。</p>
 */
@Entity // 标记此类为 JPA 实体,Hibernate 启动时会根据其结构自动在 H2 数据库中建表
@Table(name = "admin_user") // 指定数据库表名为 admin_user,避免使用类名作为默认表名,提升 SQL 可读性
public class AdminUser {

    /** 主键 ID */
    @Id // 标记该字段为数据库表主键,JPA 据此生成主键查询与外键关联
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增策略,由 H2 数据库自动分配递增值,避免应用层并发冲突
    private Long id; // 管理员唯一标识 ID,使用 Long 包装类型以支持 null(未持久化状态)

    /** 登录用户名（唯一） */
    @Column(nullable = false, length = 50, unique = true) // 列约束:非空、最大长度 50、唯一索引,保证用户名不重复
    private String username; // 登录用户名,作为管理员登录凭证之一

    /** BCrypt 加密后的密码 */
    @Column(nullable = false, length = 100) // 列约束:非空、最大长度 100(BCrypt 哈希固定 60 字符,预留扩展空间)
    private String password; // 存储 BCrypt 加密后的密码密文,严禁明文存储以保障账号安全

    /** 显示名称 */
    @Column(length = 50) // 列约束:最大长度 50,允许为空(非必填字段)
    private String displayName; // 管理员在后台展示的名称,用于界面友好显示

    /** 创建时间 */
    @Column(nullable = false) // 列约束:非空,创建管理员记录时必须填写
    private LocalDateTime createdAt; // 管理员账号创建时间,用于审计追溯

    /** 最近登录时间 */
    private LocalDateTime lastLoginAt; // 最近一次成功登录时间,用于安全审计与异常登录检测;无 @Column 默认按字段名映射

    public AdminUser() {
        // JPA 规范要求实体类必须提供无参构造器,Hibernate 通过反射调用此构造器实例化对象
    }

    public AdminUser(String username, String password, String displayName) {
        // 业务便捷构造器:用于快速创建管理员账号并初始化必填字段
        this.username = username; // 赋值登录用户名
        this.password = password; // 赋值加密后的密码(由调用方完成 BCrypt 加密)
        this.displayName = displayName; // 赋值显示名称
        this.createdAt = LocalDateTime.now(); // 自动填充创建时间为当前时刻,避免调用方遗漏
    }

    public Long getId() {
        // 获取主键 ID,供外部读取持久化标识
        return id;
    }

    public void setId(Long id) {
        // 设置主键 ID,通常仅在反序列化或测试场景使用,业务流程不应手动修改主键
        this.id = id;
    }

    public String getUsername() {
        // 获取登录用户名,用于登录校验与展示
        return username;
    }

    public void setUsername(String username) {
        // 设置登录用户名,用于创建或修改管理员账号
        this.username = username;
    }

    public String getPassword() {
        // 获取密码密文,登录校验时与用户输入的明文经 BCrypt 比对
        return password;
    }

    public void setPassword(String password) {
        // 设置密码密文,调用方需先完成 BCrypt 加密再赋值
        this.password = password;
    }

    public String getDisplayName() {
        // 获取显示名称,供前端展示与后台列表渲染
        return displayName;
    }

    public void setDisplayName(String displayName) {
        // 设置显示名称,允许管理员自行修改
        this.displayName = displayName;
    }

    public LocalDateTime getCreatedAt() {
        // 获取账号创建时间,用于审计日志与权限管理
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        // 设置账号创建时间,主要用于数据迁移或测试场景
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        // 获取最近登录时间,用于检测长期未登录账号与异常登录行为
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        // 设置最近登录时间,登录成功后由认证服务调用更新
        this.lastLoginAt = lastLoginAt;
    }
}
