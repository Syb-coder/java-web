package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入 Jakarta Bean Validation 的非空校验注解

/**
 * 登录请求 DTO（管理员/C端用户/商户通用）
 */
public class LoginRequest {  // 登录请求 DTO 类定义,管理员/C端用户/商户共用此结构

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")  // 非空字符串校验:null、空字符串""、纯空白字符均不通过
    private String username;  // 登录用户名字段

    /** 密码（明文传输，服务端 BCrypt 校验） */
    @NotBlank(message = "密码不能为空")  // 非空字符串校验:防止提交空密码造成无意义请求
    private String password;  // 登录密码字段,前端明文传输,服务端用 BCrypt 比对哈希

    // —— username 字段的 getter/setter ——
    public String getUsername() {  // 获取用户名
        return username;  // 返回当前对象的用户名
    }

    public void setUsername(String username) {  // 设置用户名
        this.username = username;  // 将入参赋值给成员变量, this 用于区分同名参数
    }

    // —— password 字段的 getter/setter ——
    public String getPassword() {  // 获取密码
        return password;  // 返回当前对象的密码
    }

    public void setPassword(String password) {  // 设置密码
        this.password = password;  // 将入参赋值给成员变量
    }
}
