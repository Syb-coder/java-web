package com.example.java11.dto;

/**
 * 登录响应 DTO
 * <p>
 * 用于登录成功后返回用户基本信息与访问令牌，前端据此维护登录态。
 * </p>
 */
public class LoginResponse {

    /** 用户 ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatar;

    /** 角色标识（如 USER、ADMIN） */
    private String role;

    /** 访问令牌 */
    private String token;

    /**
     * 默认无参构造器
     */
    public LoginResponse() {
    }

    /**
     * 全参构造器
     *
     * @param id       用户 ID
     * @param username 用户名
     * @param nickname 昵称
     * @param avatar   头像 URL
     * @param role     角色标识
     * @param token    访问令牌
     */
    public LoginResponse(Long id, String username, String nickname, String avatar, String role, String token) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.role = role;
        this.token = token;
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
