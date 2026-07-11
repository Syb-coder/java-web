package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import java.math.BigDecimal;  // 导入高精度十进制类,用于金额表示
import java.time.LocalDateTime;  // 导入日期时间类

/**
 * 商户信息响应 DTO
 * <p>
 * 返回商户的详细信息，包含资质、账户余额与审核状态。
 * </p>
 */
public class MerchantResponse {  // 商户信息响应 DTO 类定义

    /** 商户 ID */
    private Long id;  // 商户唯一标识(主键)

    /** 登录用户名 */
    private String username;  // 商户登录用户名

    /** 商户名称 */
    private String merchantName;  // 商户对外展示名称

    /** 联系电话 */
    private String contactPhone;  // 商户联系电话

    /** 营业执照号 */
    private String licenseNo;  // 营业执照号(资质信息)

    /** 商户简介 */
    private String description;  // 商户简介

    /** 商户账户余额（元） */
    private BigDecimal balance;  // 商户账户可用余额(元)

    /** 商户状态：PENDING/APPROVED/REJECTED/FROZEN */
    private String status;  // 商户状态:PENDING(待审核)/APPROVED(已通过)/REJECTED(已驳回)/FROZEN(已冻结)

    /** 审核驳回原因（仅 REJECTED 状态有值） */
    private String rejectReason;  // 驳回原因,仅 status=REJECTED 时有值

    /** 商户创建时间 */
    private LocalDateTime createdAt;  // 商户创建时间戳

    /** 商户最后修改时间 */
    private LocalDateTime updateTime;  // 商户最后修改时间戳

    // —— id 字段的 getter/setter ——
    public Long getId() {  // 获取商户 ID
        return id;  // 返回商户 ID
    }

    public void setId(Long id) {  // 设置商户 ID
        this.id = id;  // 赋值商户 ID
    }

    // —— username 字段的 getter/setter ——
    public String getUsername() {  // 获取用户名
        return username;  // 返回用户名
    }

    public void setUsername(String username) {  // 设置用户名
        this.username = username;  // 赋值用户名
    }

    // —— merchantName 字段的 getter/setter ——
    public String getMerchantName() {  // 获取商户名称
        return merchantName;  // 返回商户名称
    }

    public void setMerchantName(String merchantName) {  // 设置商户名称
        this.merchantName = merchantName;  // 赋值商户名称
    }

    // —— contactPhone 字段的 getter/setter ——
    public String getContactPhone() {  // 获取联系电话
        return contactPhone;  // 返回联系电话
    }

    public void setContactPhone(String contactPhone) {  // 设置联系电话
        this.contactPhone = contactPhone;  // 赋值联系电话
    }

    // —— licenseNo 字段的 getter/setter ——
    public String getLicenseNo() {  // 获取营业执照号
        return licenseNo;  // 返回营业执照号
    }

    public void setLicenseNo(String licenseNo) {  // 设置营业执照号
        this.licenseNo = licenseNo;  // 赋值营业执照号
    }

    // —— description 字段的 getter/setter ——
    public String getDescription() {  // 获取商户简介
        return description;  // 返回商户简介
    }

    public void setDescription(String description) {  // 设置商户简介
        this.description = description;  // 赋值商户简介
    }

    // —— balance 字段的 getter/setter ——
    public BigDecimal getBalance() {  // 获取账户余额
        return balance;  // 返回账户余额
    }

    public void setBalance(BigDecimal balance) {  // 设置账户余额
        this.balance = balance;  // 赋值账户余额
    }

    // —— status 字段的 getter/setter ——
    public String getStatus() {  // 获取商户状态
        return status;  // 返回商户状态
    }

    public void setStatus(String status) {  // 设置商户状态
        this.status = status;  // 赋值商户状态
    }

    // —— rejectReason 字段的 getter/setter ——
    public String getRejectReason() {  // 获取驳回原因
        return rejectReason;  // 返回驳回原因
    }

    public void setRejectReason(String rejectReason) {  // 设置驳回原因
        this.rejectReason = rejectReason;  // 赋值驳回原因
    }

    // —— createdAt 字段的 getter/setter ——
    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;  // 返回创建时间
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;  // 赋值创建时间
    }

    // —— updateTime 字段的 getter/setter ——
    public LocalDateTime getUpdateTime() {  // 获取最后修改时间
        return updateTime;  // 返回最后修改时间
    }

    public void setUpdateTime(LocalDateTime updateTime) {  // 设置最后修改时间
        this.updateTime = updateTime;  // 赋值最后修改时间
    }
}
