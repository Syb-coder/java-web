package com.example.java3.dto;

/**
 * 用户个人信息更新请求 DTO（不含密码、学号）
 */
public class UserProfileRequest {

    /** 昵称 */
    private String nickname;

    /** 联系电话 */
    private String phone;

    /** 头像 URL */
    private String avatar;

    // ===== Getter / Setter =====

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
