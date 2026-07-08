// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

// 导入 JPA（Jakarta Persistence API）全部注解，包含 @Entity、@Table、@Id 等，用于将 POJO 映射为数据库实体
import jakarta.persistence.*;

// 导入 LocalDateTime 时间类型，用于记录注册、创建等业务时间戳
import java.time.LocalDateTime;

/**
 * 前台学生用户实体
 * <p>
 * 学号实名认证机制：studentId 唯一约束，作为校园身份标识。
 * 密码采用 BCrypt 加密存储，不保存明文。
 * </p>
 */
// 标识当前类为 JPA 实体，Hibernate 会将其映射为数据库表
@Entity
// 指定数据库表名为 users，避免默认按类名生成表（默认会是 user）
@Table(name = "users")
public class User {

    /** 主键，自增 */
    // 声明该字段为数据库主键
    @Id
    // 主键生成策略：IDENTITY 表示由数据库自增分配（如 MySQL AUTO_INCREMENT）
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 学号（唯一），作为实名认证依据 */
    // 非空且唯一，长度上限 20；唯一约束保证一个学号只能注册一次
    @Column(nullable = false, unique = true, length = 20)
    private String studentId;

    /** 登录用户名（唯一） */
    // 非空且唯一，长度上限 50；登录时使用
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt 加密后的密码 */
    // 非空，长度上限 100；BCrypt 哈希串较长，需预留空间
    @Column(nullable = false, length = 100)
    private String password;

    /** 昵称，用于前台展示 */
    // 可空，长度上限 50
    @Column(length = 50)
    private String nickname;

    /** 联系电话（可选） */
    // 可空，长度上限 20
    @Column(length = 20)
    private String phone;

    /** 头像 URL（可选） */
    // 可空，长度上限 255，适配标准 URL 长度
    @Column(length = 255)
    private String avatar;

    /** 账号状态：ACTIVE/BANNED */
    // 声明枚举以字符串形式持久化（默认是 ORDINAL 序号，STRING 更直观且便于排查）
    @Enumerated(EnumType.STRING)
    // 非空，长度上限 20，足以容纳枚举常量名
    @Column(nullable = false, length = 20)
    private UserStatus status;

    /** 注册时间 */
    // 非空，注册时必须写入
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法（JPA 要求） */
    // JPA 规范要求实体必须提供无参构造方法，便于通过反射实例化
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
        // 设置学号，作为实名认证唯一标识
        this.studentId = studentId;
        // 设置登录用户名
        this.username = username;
        // 设置已加密密码（业务层通过 BCrypt 处理后传入，避免保存明文）
        this.password = password;
        // 设置昵称，用于前台展示
        this.nickname = nickname;
        // 默认新注册用户为正常状态，符合"白名单"原则：注册即正常，违规后才封禁
        this.status = UserStatus.ACTIVE;
        // 注册时间取当前系统时间
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    // 获取主键 ID，供查询与关联使用
    public Long getId() {
        return id;
    }

    // 设置主键 ID，通常仅由 JPA 自动填充，业务层不主动调用
    public void setId(Long id) {
        this.id = id;
    }

    // 获取学号，用于实名认证校验
    public String getStudentId() {
        return studentId;
    }

    // 设置学号
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    // 获取登录用户名
    public String getUsername() {
        return username;
    }

    // 设置登录用户名
    public void setUsername(String username) {
        this.username = username;
    }

    // 获取已加密的密码，供登录校验使用
    public String getPassword() {
        return password;
    }

    // 设置密码（须为已 BCrypt 加密的密文）
    public void setPassword(String password) {
        this.password = password;
    }

    // 获取昵称，用于前台展示
    public String getNickname() {
        return nickname;
    }

    // 设置昵称
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // 获取联系电话
    public String getPhone() {
        return phone;
    }

    // 设置联系电话
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // 获取头像 URL
    public String getAvatar() {
        return avatar;
    }

    // 设置头像 URL
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    // 获取账号状态，用于登录与权限校验
    public UserStatus getStatus() {
        return status;
    }

    // 设置账号状态（如封禁/解封操作）
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    // 获取注册时间
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 设置注册时间
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
