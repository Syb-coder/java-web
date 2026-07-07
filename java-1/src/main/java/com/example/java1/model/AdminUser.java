// 声明包路径，存放 JPA 实体类
package com.example.java1.model; // 声明包路径为 com.example.java1.model，集中存放实体类

// 导入 JPA 注解
import jakarta.persistence.Column; // 引入 JPA 列映射注解，用于定义字段与数据库列的约束
import jakarta.persistence.Entity; // 引入 JPA 实体注解，标记类为可持久化的数据库实体
import jakarta.persistence.GeneratedValue; // 引入 JPA 主键生成策略注解，声明主键生成方式
import jakarta.persistence.GenerationType; // 引入主键生成策略枚举，IDENTITY 表示由数据库自增
import jakarta.persistence.Id; // 引入 JPA 主键注解，标记字段为表主键
import jakarta.persistence.Table; // 引入 JPA 表映射注解，指定实体对应的表名

// 导入时间类型
import java.time.LocalDateTime; // 引入 Java 8 日期时间类，含时分秒，用于记录登录与创建时间

/**
 * 管理员用户实体
 * <p>
 * 后台管理系统操作员的账号信息。密码使用 BCrypt 加密存储，不可逆。
 * 管理员负责图书编目、读者管理、借还书审核等操作。
 * </p>
 * <p>
 * 设计要点：
 * 1. 密码字段长度设为 100，BCrypt 加密结果固定 60 字符，预留扩展空间；
 * 2. username 设置唯一约束，避免登录时出现歧义账号；
 * 3. lastLoginAt 与 createTime 分离，前者用于安全审计，后者用于追溯账号建立时间。
 * </p>
 */
// @Entity 标识该类为 JPA 实体，Hibernate 启动时会根据其结构生成 DDL
@Entity
// @Table 指定表名（显式指定避免命名歧义，类名 AdminUser 默认会映射为 admin_user，此处显式声明保证可控）
@Table(name = "admin_users")
public class AdminUser { // 定义公共实体类 AdminUser，对应管理员账号领域模型

    /** 主键 ID，自增 */
    @Id
    // @GeneratedValue 指定主键生成策略为数据库自增（H2 支持 IDENTITY，无需应用层维护主键）
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键字段，Long 类型对应数据库 BIGINT，包装类型允许 JPA 检测 null 判断是否为新实体

    /** 登录用户名（唯一） */
    // @Column 配置列约束：非空、唯一、长度 50；唯一约束防止重复注册导致登录歧义
    @Column(nullable = false, unique = true, length = 50)
    private String username; // 用户名字段，作为登录凭据之一

    /** BCrypt 加密后的密码 */
    // 密码字段长度设为 100，BCrypt 加密结果固定为 60 字符，预留扩展空间（如更换算法或加盐）
    @Column(nullable = false, length = 100)
    private String password; // 密码字段，仅存储密文，明文由 AuthService 在登录时比对

    /** 昵称，用于显示 */
    @Column(length = 50)
    private String nickname; // 昵称字段，用于界面展示，可空以便用户后续完善

    /** 最近登录时间，登录成功时由 AuthService 更新 */
    private LocalDateTime lastLoginAt; // 最近登录时间，用于安全审计与异常登录排查

    /** 创建时间，注册时设置 */
    private LocalDateTime createTime; // 账号创建时间，注册时一次性写入，后续不再修改

    /** 无参构造方法：JPA 规范要求，Hibernate 实例化实体时调用 */
    public AdminUser() { // 无参构造方法，供 JPA/Hibernate 通过反射创建实例
    }

    /**
     * 全参构造方法：用于 DataInitializer 初始化管理员
     * <p>
     * 仅在系统初始化阶段使用，业务运行期不直接 new 管理员，
     * 避免绕过 AuthService 的密码加密与唯一性校验逻辑。
     * </p>
     *
     * @param username 用户名
     * @param password 已 BCrypt 加密的密码
     * @param nickname 昵称
     */
    public AdminUser(String username, String password, String nickname) { // 业务构造方法，封装初始化核心字段
        this.username = username; // 赋值用户名
        this.password = password; // 赋值已加密密码（调用方负责加密，避免在此重复加密）
        this.nickname = nickname; // 赋值昵称
        this.createTime = LocalDateTime.now(); // 创建时间在构造时写入，保证时间一致性
    }

    // ===== Getter / Setter =====
    // JPA 通过反射调用 Setter 完成实体属性注入，通过 Getter 读取字段值
    public Long getId() { return id; } // 主键 ID 的 getter，供查询与外部引用获取标识
    public void setId(Long id) { this.id = id; } // 主键 ID 的 setter，通常由 JPA 自动填充，业务层不应手动设置
    public String getUsername() { return username; } // 用户名的 getter，供登录校验与展示使用
    public void setUsername(String username) { this.username = username; } // 用户名的 setter，供修改账号时赋值
    public String getPassword() { return password; } // 密码的 getter，供 AuthService 进行 BCrypt 比对
    public void setPassword(String password) { this.password = password; } // 密码的 setter，赋值需保证传入的已是密文
    public String getNickname() { return nickname; } // 昵称的 getter，供界面展示
    public void setNickname(String nickname) { this.nickname = nickname; } // 昵称的 setter，供修改个人信息时赋值
    public LocalDateTime getLastLoginAt() { return lastLoginAt; } // 最近登录时间的 getter，供安全审计展示
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; } // 最近登录时间的 setter，登录成功时由 AuthService 调用
    public LocalDateTime getCreateTime() { return createTime; } // 创建时间的 getter，供审计与排序使用
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; } // 创建时间的 setter，仅供持久化层或初始化使用
}
