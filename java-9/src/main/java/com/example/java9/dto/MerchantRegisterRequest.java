package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入非空校验注解
import jakarta.validation.constraints.Size;  // 导入长度范围校验注解

/**
 * 商户入驻请求 DTO
 */
public class MerchantRegisterRequest {  // 商户入驻请求 DTO 类定义

    @NotBlank(message = "用户名不能为空")  // 非空字符串校验:null、""、纯空白均不通过
    @Size(min = 3, max = 20, message = "用户名长度需在3-20字符之间")  // 长度范围校验:用户名 3~20 字符
    private String username;  // 商户登录用户名

    @NotBlank(message = "密码不能为空")  // 非空字符串校验:防止空密码
    @Size(min = 6, max = 20, message = "密码长度需在6-20字符之间")  // 长度范围校验:密码 6~20 字符
    private String password;  // 商户登录密码(服务端 BCrypt 加密存储)

    @NotBlank(message = "商户名称不能为空")  // 非空字符串校验:商户名称必填
    private String merchantName;  // 商户对外展示名称

    @NotBlank(message = "联系电话不能为空")  // 非空字符串校验:联系电话必填
    private String contactPhone;  // 商户联系电话(用于审核沟通)

    @NotBlank(message = "营业执照号不能为空")  // 非空字符串校验:营业执照号必填
    private String licenseNo;  // 营业执照号(用于资质审核)

    /** 商户简介 */
    private String description;  // 商户简介(选填),用于展示商户经营内容

    // —— username 字段的 getter/setter ——
    public String getUsername() {  // 获取用户名
        return username;  // 返回用户名
    }

    public void setUsername(String username) {  // 设置用户名
        this.username = username;  // 赋值用户名
    }

    // —— password 字段的 getter/setter ——
    public String getPassword() {  // 获取密码
        return password;  // 返回密码
    }

    public void setPassword(String password) {  // 设置密码
        this.password = password;  // 赋值密码
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
}
