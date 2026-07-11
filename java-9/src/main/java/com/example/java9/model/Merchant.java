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
import java.math.BigDecimal;  // 高精度十进制，用于金额计算
import java.time.LocalDateTime;  // 时间戳类型

/**
 * B端商户实体
 * <p>
 * 对应 merchants 表，存储线下/线上商家入驻信息。
 * 商户入驻需运营审核，审核通过后方可收款。
 * balance 表示商户可结算余额。
 * </p>
 */
@Entity  // JPA 实体标识
@Table(name = "merchants")  // 映射到 merchants 表
public class Merchant {  // B 端商户实体，承载入驻信息与可结算余额

    /** 主键 ID，自增 */
    @Id  // JPA 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略
    private Long id;  // 主键 ID，自增长

    /** 商户登录账号（唯一） */
    @Column(nullable = false, unique = true, length = 50)  // 非空且唯一，长度50
    private String username;  // 商户登录账号，全局唯一

    /** BCrypt 加密后的密码 */
    @Column(nullable = false, length = 100)  // 非空，长度100容纳 BCrypt 加密串
    private String password;  // 密码字段，BCrypt 加密存储

    /** 商户名称（店铺名） */
    @Column(nullable = false, length = 100)  // 非空，长度100
    private String merchantName;  // 商户名称，C 端展示用

    /** 联系电话 */
    @Column(length = 20)  // 长度20
    private String contactPhone;  // 商户联系电话，用于运营对接

    /** 营业执照号（入驻审核关键字段） */
    @Column(length = 30)  // 长度30，覆盖统一社会信用代码长度
    private String licenseNo;  // 营业执照号，入驻审核的关键依据

    /** 商户简介 */
    @Column(length = 500)  // 长度500
    private String description;  // 商户简介，C 端展示

    /** 可结算余额 */
    @Column(nullable = false, precision = 18, scale = 2)  // 非空，金额精度2位
    private BigDecimal balance = BigDecimal.ZERO;  // 可结算余额，收款累加，提现扣减

    /** 入驻状态：PENDING/APPROVED/REJECTED */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储
    @Column(nullable = false, length = 20)  // 非空，长度20
    private MerchantStatus status = MerchantStatus.PENDING;  // 入驻状态，默认待审核

    /** 驳回原因（审核拒绝时填写） */
    @Column(length = 200)  // 长度200
    private String rejectReason;  // 驳回原因，REJECTED 状态时填写以便商户补全资料

    /** 创建时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime createdAt;  // 创建时间戳

    /** 最后修改时间 */
    private LocalDateTime updateTime;  // 最后修改时间戳

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public Merchant() {  // JPA 无参构造器
    }

    public Merchant(String username, String password, String merchantName, String contactPhone, String licenseNo) {  // 入驻申请构造器
        this.username = username;  // 赋值登录账号
        this.password = password;  // 赋值密码（已 BCrypt 加密）
        this.merchantName = merchantName;  // 赋值商户名称
        this.contactPhone = contactPhone;  // 赋值联系电话
        this.licenseNo = licenseNo;  // 赋值营业执照号
        this.balance = BigDecimal.ZERO;  // 新商户余额为0
        this.status = MerchantStatus.PENDING;  // 新商户默认待审核
        this.createdAt = LocalDateTime.now();  // 服务端生成入驻时间
        this.updateTime = LocalDateTime.now();  // 初始化修改时间
    }

    public Long getId() {  // 获取主键 ID
        return id;
    }

    public void setId(Long id) {  // 设置主键 ID
        this.id = id;
    }

    public String getUsername() {  // 获取登录账号
        return username;
    }

    public void setUsername(String username) {  // 设置登录账号
        this.username = username;
    }

    public String getPassword() {  // 获取密码
        return password;
    }

    public void setPassword(String password) {  // 设置密码
        this.password = password;
    }

    public String getMerchantName() {  // 获取商户名称
        return merchantName;
    }

    public void setMerchantName(String merchantName) {  // 设置商户名称
        this.merchantName = merchantName;
    }

    public String getContactPhone() {  // 获取联系电话
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {  // 设置联系电话
        this.contactPhone = contactPhone;
    }

    public String getLicenseNo() {  // 获取营业执照号
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {  // 设置营业执照号
        this.licenseNo = licenseNo;
    }

    public String getDescription() {  // 获取商户简介
        return description;
    }

    public void setDescription(String description) {  // 设置商户简介
        this.description = description;
    }

    public BigDecimal getBalance() {  // 获取可结算余额
        return balance;
    }

    public void setBalance(BigDecimal balance) {  // 设置可结算余额
        this.balance = balance;
    }

    public MerchantStatus getStatus() {  // 获取入驻状态
        return status;
    }

    public void setStatus(MerchantStatus status) {  // 设置入驻状态
        this.status = status;
    }

    public String getRejectReason() {  // 获取驳回原因
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {  // 设置驳回原因
        this.rejectReason = rejectReason;
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
