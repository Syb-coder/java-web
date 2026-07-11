package com.example.java9.model;  // 实体类所在包，归属于 model 层

// JPA 持久化相关注解导入
import jakarta.persistence.Column;  // 字段列映射注解
import jakarta.persistence.Entity;  // 实体标识注解
import jakarta.persistence.EnumType;  // 枚举映射类型
import jakarta.persistence.Enumerated;  // 枚举存储方式注解
import jakarta.persistence.GeneratedValue;  // 主键生成策略注解
import jakarta.persistence.GenerationType;  // 主键生成策略枚举
import jakarta.persistence.Id;  // 主键标识注解
import jakarta.persistence.PreUpdate;  // 更新前回调注解
import jakarta.persistence.Table;  // 表名映射注解

// JDK 通用类型导入
import java.math.BigDecimal;  // 高精度十进制，用于金额计算避免浮点误差
import java.time.LocalDateTime;  // 时间戳类型

/**
 * C端普通用户实体
 * <p>
 * 对应 users 表，存储投资人/消费者的账户、实名、资产信息。
 * balance 字段表示可用余额，所有资金操作均需更新此字段并记录交易流水。
 * </p>
 */
@Entity  // JPA 实体标识
@Table(name = "users")  // 映射到 users 表，与 admin_users 区分
public class User {  // C 端用户实体，承载账户、实名与资产信息

    /** 主键 ID，自增 */
    @Id  // JPA 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略
    private Long id;  // 主键 ID，自增长

    /** 登录用户名（唯一） */
    @Column(nullable = false, unique = true, length = 50)  // 非空且唯一，长度50
    private String username;  // 登录用户名，全局唯一

    /** BCrypt 加密后的密码 */
    @Column(nullable = false, length = 100)  // 非空，长度100容纳 BCrypt 加密串
    private String password;  // 密码字段，BCrypt 加密存储

    /** 真实姓名（实名认证字段） */
    @Column(length = 50)  // 长度50，允许为空（未实名时为 null）
    private String realName;  // 真实姓名，实名认证后填写

    /** 身份证号（实名认证字段，唯一） */
    @Column(unique = true, length = 18)  // 唯一约束防重复实名，长度18匹配二代证
    private String idCard;  // 身份证号，唯一索引保证一人一证

    /** 手机号 */
    @Column(length = 20)  // 长度20覆盖国际区号格式
    private String phone;  // 手机号，用于短信验证与登录

    /** 可用余额（精度 2 位小数，必须使用 BigDecimal 避免浮点误差） */
    @Column(nullable = false, precision = 18, scale = 2)  // 非空，总位数18位，小数2位，对应金额精度
    private BigDecimal balance = BigDecimal.ZERO;  // 可用余额，初始化为0，资金操作的核心字段

    /** 实名状态：true 已实名，false 未实名 */
    @Column(nullable = false)  // 非空，避免 null 三态导致判定歧义
    private Boolean verified = false;  // 实名标志，默认未实名，影响投资等业务准入

    /** 账户状态：NORMAL 正常 / FROZEN 冻结 */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储，便于排查
    @Column(nullable = false, length = 20)  // 非空，长度20
    private UserStatus status = UserStatus.NORMAL;  // 账户状态，默认正常

    /** 注册时间 */
    @Column(nullable = false)  // 非空，注册必有时间
    private LocalDateTime createdAt;  // 注册时间戳

    /** 最后修改时间 */
    private LocalDateTime updateTime;  // 最后修改时间戳

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public User() {  // JPA 无参构造器
    }

    public User(String username, String password, String phone) {  // 注册构造器，仅必填字段
        this.username = username;  // 赋值用户名
        this.password = password;  // 赋值密码（已 BCrypt 加密）
        this.phone = phone;  // 赋值手机号
        this.balance = BigDecimal.ZERO;  // 新用户余额为0
        this.verified = false;  // 新用户默认未实名
        this.status = UserStatus.NORMAL;  // 新用户默认正常状态
        this.createdAt = LocalDateTime.now();  // 服务端生成注册时间
        this.updateTime = LocalDateTime.now();  // 初始化修改时间
    }

    public Long getId() {  // 获取主键 ID
        return id;
    }

    public void setId(Long id) {  // 设置主键 ID
        this.id = id;
    }

    public String getUsername() {  // 获取登录用户名
        return username;
    }

    public void setUsername(String username) {  // 设置登录用户名
        this.username = username;
    }

    public String getPassword() {  // 获取密码
        return password;
    }

    public void setPassword(String password) {  // 设置密码
        this.password = password;
    }

    public String getRealName() {  // 获取真实姓名
        return realName;
    }

    public void setRealName(String realName) {  // 设置真实姓名
        this.realName = realName;
    }

    public String getIdCard() {  // 获取身份证号
        return idCard;
    }

    public void setIdCard(String idCard) {  // 设置身份证号
        this.idCard = idCard;
    }

    public String getPhone() {  // 获取手机号
        return phone;
    }

    public void setPhone(String phone) {  // 设置手机号
        this.phone = phone;
    }

    public BigDecimal getBalance() {  // 获取可用余额
        return balance;
    }

    public void setBalance(BigDecimal balance) {  // 设置可用余额，调用方需保证资金操作原子性
        this.balance = balance;
    }

    public Boolean getVerified() {  // 获取实名状态
        return verified;
    }

    public void setVerified(Boolean verified) {  // 设置实名状态
        this.verified = verified;
    }

    public UserStatus getStatus() {  // 获取账户状态
        return status;
    }

    public void setStatus(UserStatus status) {  // 设置账户状态
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {  // 获取注册时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置注册时间
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
