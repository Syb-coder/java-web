package com.example.java12.dto;  // DTO 层包

import jakarta.validation.constraints.NotBlank;  // 非空校验
import jakarta.validation.constraints.Pattern;  // 正则校验
import jakarta.validation.constraints.Size;  // 长度校验

/**
 * 注册请求 DTO
 */
public class RegisterRequest {

    /** 登录账号（字母数字组合，4-20字符） */
    @NotBlank(message = "账号不能为空")
    @Size(min = 4, max = 20, message = "账号长度需为4-20字符")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "账号仅支持字母和数字")
    private String account;

    /** 昵称（2-15字符） */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 15, message = "昵称长度需为2-15字符")
    private String nickname;

    /** 密码（≥6字符） */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度需至少6位")
    private String password;

    /** 确认密码（前端校验一致后提交） */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
