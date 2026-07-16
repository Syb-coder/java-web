package com.example.java12.dto;  // DTO 层包

/**
 * 登录响应 DTO
 * <p>
 * 登录成功后返回用户信息与 Token，前端将 Token 存储在 Cookie 或 localStorage 中，
 * 后续请求通过 Authorization 请求头携带 Token 进行身份校验。
 * </p>
 */
public class LoginResponse {

    /** 用户 ID */
    private Long id;

    /** 登录账号 */
    private String account;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatar;

    /** 角色：USER / ADMIN */
    private String role;

    /** 身份 Token（UUID 格式，前端存储后在请求头携带） */
    private String token;

    /**
     * 全参构造器
     */
    public LoginResponse(Long id, String account, String nickname, String avatar, String role, String token) {
        this.id = id;
        this.account = account;
        this.nickname = nickname;
        this.avatar = avatar;
        this.role = role;
        this.token = token;
    }

    // ===== getter / setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
