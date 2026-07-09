// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

// 导入参数校验注解
import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求 DTO
 * <p>
 * 用户端（读者）与管理端（管理员）共用此 DTO，通过 loginType 字段区分登录类型。
 * </p>
 */
public class LoginRequest {

    /** 登录账号：读者为学号/工号，管理员为用户名 */
    @NotBlank(message = "账号不能为空") // 非空校验
    private String username;

    /** 登录密码（明文，由前端传入，后端用 BCrypt 比对） */
    @NotBlank(message = "密码不能为空") // 非空校验
    private String password;

    /** 登录类型：reader=读者端，admin=管理端 */
    @NotBlank(message = "登录类型不能为空") // 非空校验
    private String loginType;

    // ===== Getter / Setter =====
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getLoginType() { return loginType; }
    public void setLoginType(String loginType) { this.loginType = loginType; }
}
