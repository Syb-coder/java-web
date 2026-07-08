package com.example.java3.dto;

/**
 * 登录响应 DTO
 */
public class LoginResponse {

    /** 用户 ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 昵称/显示名 */
    private String displayName;

    /** 角色：USER / ADMIN */
    private String role;

    /** 学号（仅学生有） */
    private String studentId;

    public LoginResponse(Long id, String username, String displayName, String role, String studentId) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
        this.studentId = studentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
