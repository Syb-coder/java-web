package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入非空校验注解
import jakarta.validation.constraints.Size;  // 导入长度范围校验注解

/**
 * C端用户注册请求 DTO
 */
public class RegisterRequest {  // C端用户注册请求 DTO 类定义

    @NotBlank(message = "用户名不能为空")  // 非空字符串校验:null、""、"   "均不通过
    @Size(min = 3, max = 20, message = "用户名长度需在3-20字符之间")  // 长度范围校验:用户名字符数必须在 3~20 之间
    private String username;  // 注册用户名

    @NotBlank(message = "密码不能为空")  // 非空字符串校验:防止空密码
    @Size(min = 6, max = 20, message = "密码长度需在6-20字符之间")  // 长度范围校验:密码字符数必须在 6~20 之间,兼顾安全性与易用性
    private String password;  // 注册密码(明文传输,服务端 BCrypt 加密存储)

    @NotBlank(message = "手机号不能为空")  // 非空字符串校验:手机号必填
    private String phone;  // 手机号(用于短信验证与找回密码)

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

    // —— phone 字段的 getter/setter ——
    public String getPhone() {  // 获取手机号
        return phone;  // 返回手机号
    }

    public void setPhone(String phone) {  // 设置手机号
        this.phone = phone;  // 赋值手机号
    }
}
