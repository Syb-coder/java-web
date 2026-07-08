package com.example.java3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 学生注册请求 DTO
 */
public class RegisterRequest {

    /** 学号（实名认证依据） */
    @NotBlank(message = "学号不能为空")
    @Size(min = 6, max = 20, message = "学号长度需在 6-20 之间")
    private String studentId;

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度需在 3-50 之间")
    private String username;

    /** 密码（明文，服务端加密） */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度需在 6-30 之间")
    private String password;

    /** 昵称 */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过 50")
    private String nickname;

    /** 联系电话（可选） */
    private String phone;

    // ===== Getter / Setter =====

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
