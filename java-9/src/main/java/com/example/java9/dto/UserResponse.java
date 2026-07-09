package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import java.math.BigDecimal;  // 导入高精度十进制类,用于金额表示避免浮点误差
import java.time.LocalDateTime;  // 导入日期时间类,用于记录创建/更新时间

/**
 * C端用户信息响应 DTO
 * <p>
 * 用于返回 C 端用户的基本账户信息，
 * 包括账户余额、实名认证状态及账户状态。
 * </p>
 */
public class UserResponse {  // C端用户信息响应 DTO 类定义

    /** 账户 ID */
    private Long id;  // 账户唯一标识

    /** 用户名（登录名） */
    private String username;  // 登录用户名

    /** 真实姓名（实名认证后填充） */
    private String realName;  // 真实姓名,未实名时为 null

    /** 身份证号（实名认证后填充，脱敏返回） */
    private String idCard;  // 身份证号,服务端脱敏后返回(如 110***********1234)

    /** 手机号 */
    private String phone;  // 用户绑定的手机号

    /** 账户余额（可用于投资/提现） */
    private BigDecimal balance;  // 账户可用余额(元),使用 BigDecimal 保证精度

    /** 是否已实名认证 */
    private Boolean verified;  // 实名认证标志:true=已认证,false=未认证

    /** 账户状态：ACTIVE/FROZEN/CLOSED */
    private String status;  // 账户状态:ACTIVE(正常)/FROZEN(冻结)/CLOSED(注销)

    /** 账户创建时间 */
    private LocalDateTime createdAt;  // 账户创建时间戳

    // —— id 字段的 getter/setter ——
    public Long getId() {  // 获取账户 ID
        return id;  // 返回账户 ID
    }

    public void setId(Long id) {  // 设置账户 ID
        this.id = id;  // 赋值账户 ID
    }

    // —— username 字段的 getter/setter ——
    public String getUsername() {  // 获取用户名
        return username;  // 返回用户名
    }

    public void setUsername(String username) {  // 设置用户名
        this.username = username;  // 赋值用户名
    }

    // —— realName 字段的 getter/setter ——
    public String getRealName() {  // 获取真实姓名
        return realName;  // 返回真实姓名
    }

    public void setRealName(String realName) {  // 设置真实姓名
        this.realName = realName;  // 赋值真实姓名
    }

    // —— idCard 字段的 getter/setter ——
    public String getIdCard() {  // 获取身份证号(已脱敏)
        return idCard;  // 返回身份证号
    }

    public void setIdCard(String idCard) {  // 设置身份证号
        this.idCard = idCard;  // 赋值身份证号
    }

    // —— phone 字段的 getter/setter ——
    public String getPhone() {  // 获取手机号
        return phone;  // 返回手机号
    }

    public void setPhone(String phone) {  // 设置手机号
        this.phone = phone;  // 赋值手机号
    }

    // —— balance 字段的 getter/setter ——
    public BigDecimal getBalance() {  // 获取账户余额
        return balance;  // 返回账户余额
    }

    public void setBalance(BigDecimal balance) {  // 设置账户余额
        this.balance = balance;  // 赋值账户余额
    }

    // —— verified 字段的 getter/setter ——
    public Boolean getVerified() {  // 获取实名认证状态
        return verified;  // 返回是否已实名认证
    }

    public void setVerified(Boolean verified) {  // 设置实名认证状态
        this.verified = verified;  // 赋值实名认证状态
    }

    // —— status 字段的 getter/setter ——
    public String getStatus() {  // 获取账户状态
        return status;  // 返回账户状态字符串
    }

    public void setStatus(String status) {  // 设置账户状态
        this.status = status;  // 赋值账户状态
    }

    // —— createdAt 字段的 getter/setter ——
    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;  // 返回创建时间
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;  // 赋值创建时间
    }
}
