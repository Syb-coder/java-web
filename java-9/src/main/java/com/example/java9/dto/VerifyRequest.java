package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入非空校验注解

/**
 * 实名认证请求 DTO
 * <p>
 * C 端用户提交真实姓名与身份证号进行实名认证，
 * 服务端校验身份证格式与公安系统一致性。
 * </p>
 */
public class VerifyRequest {  // 实名认证请求 DTO 类定义

    /** 真实姓名 */
    @NotBlank(message = "真实姓名不能为空")  // 非空字符串校验:null、""、纯空白均不通过
    private String realName;  // 真实姓名(须与身份证一致)

    /** 身份证号（18 位） */
    @NotBlank(message = "身份证号不能为空")  // 非空字符串校验:身份证号必填
    private String idCard;  // 18 位身份证号(服务端校验格式与公安系统一致性)

    // —— realName 字段的 getter/setter ——
    public String getRealName() {  // 获取真实姓名
        return realName;  // 返回真实姓名
    }

    public void setRealName(String realName) {  // 设置真实姓名
        this.realName = realName;  // 赋值真实姓名
    }

    // —— idCard 字段的 getter/setter ——
    public String getIdCard() {  // 获取身份证号
        return idCard;  // 返回身份证号
    }

    public void setIdCard(String idCard) {  // 设置身份证号
        this.idCard = idCard;  // 赋值身份证号
    }
}
