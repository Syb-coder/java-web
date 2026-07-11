package com.example.demo.dto;

/**
 * 登录成功响应 DTO
 * <p>
 * 为什么独立于 UserDTO：登录响应不需要 id、createdAt 等字段，
 * 专门针对登录场景精简字段，减少不必要的数据传输。
 * 不含 password 字段，从根本上避免密码泄露。
 * </p>
 */
public class LoginResponse {

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 角色（小写：admin/teacher/student） */
    private String role;

    /** 联系电话 */
    private String phone;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
