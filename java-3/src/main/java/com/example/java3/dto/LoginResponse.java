// 声明当前类所属的包路径，统一放在 dto 子包下
package com.example.java3.dto;

/**
 * 登录响应 DTO
 * <p>
 * 登录成功后返回给前端的基础用户信息，前端据此跳转页面并缓存角色权限。
 * </p>
 */
public class LoginResponse {

    // 用户主键 ID，作为后续接口调用的身份标识
    /** 用户 ID */
    private Long id;

    // 用户名，用于展示
    /** 用户名 */
    private String username;

    // 昵称/显示名，前端导航栏展示用
    /** 昵称/显示名 */
    private String displayName;

    // 角色字段：USER 表示学生，ADMIN 表示管理员，决定前端路由权限
    /** 角色：USER / ADMIN */
    private String role;

    // 学号字段，仅学生角色有值，管理员为 null
    /** 学号（仅学生有） */
    private String studentId;

    // 全参构造方法，登录成功后由 Service 层组装并返回给前端
    public LoginResponse(Long id, String username, String displayName, String role, String studentId) {
        this.id = id;                       // 赋值用户 ID
        this.username = username;           // 赋值用户名
        this.displayName = displayName;     // 赋值显示名
        this.role = role;                   // 赋值角色
        this.studentId = studentId;         // 赋值学号
    }

    // 获取用户 ID
    public Long getId() {
        return id;
    }

    // 设置用户 ID
    public void setId(Long id) {
        this.id = id;
    }

    // 获取用户名
    public String getUsername() {
        return username;
    }

    // 设置用户名
    public void setUsername(String username) {
        this.username = username;
    }

    // 获取显示名
    public String getDisplayName() {
        return displayName;
    }

    // 设置显示名
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    // 获取角色标识
    public String getRole() {
        return role;
    }

    // 设置角色标识
    public void setRole(String role) {
        this.role = role;
    }

    // 获取学号
    public String getStudentId() {
        return studentId;
    }

    // 设置学号
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
