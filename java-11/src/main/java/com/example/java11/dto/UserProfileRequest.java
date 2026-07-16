package com.example.java11.dto;

import jakarta.validation.constraints.Size;

/**
 * 用户资料更新请求 DTO
 * <p>
 * 用于接收用户提交的个人资料修改请求，所有字段均为可选更新项。
 * 各字段通过 @Size 注解限制最大长度，防止超长数据写入数据库。
 * </p>
 */
public class UserProfileRequest {

    /** 昵称（最长 50 字符） */
    @Size(max = 50, message = "昵称长度不能超过 50 字符")
    private String nickname;

    /** 头像 URL（最长 500 字符） */
    @Size(max = 500, message = "头像 URL 长度不能超过 500 字符")
    private String avatar;

    /** 个性签名（最长 200 字符） */
    @Size(max = 200, message = "签名长度不能超过 200 字符")
    private String signature;

    /** 个人简介（最长 1000 字符） */
    @Size(max = 1000, message = "简介长度不能超过 1000 字符")
    private String bio;

    /** 邮箱地址 */
    private String email;

    /**
     * 默认无参构造器
     */
    public UserProfileRequest() {
    }

    /**
     * 全参构造器
     *
     * @param nickname  昵称
     * @param avatar    头像 URL
     * @param signature 个性签名
     * @param bio       个人简介
     * @param email     邮箱地址
     */
    public UserProfileRequest(String nickname, String avatar, String signature, String bio, String email) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.signature = signature;
        this.bio = bio;
        this.email = email;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
