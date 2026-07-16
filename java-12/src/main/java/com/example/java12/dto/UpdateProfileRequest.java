package com.example.java12.dto;  // DTO 层包

import jakarta.validation.constraints.Size;  // 长度校验

/**
 * 修改个人资料请求 DTO
 */
public class UpdateProfileRequest {

    /** 新昵称（2-15字符，唯一，30天内仅可修改一次） */
    @Size(min = 2, max = 15, message = "昵称长度需为2-15字符")
    private String nickname;

    /** 头像 URL（上传后返回的路径） */
    @Size(max = 500, message = "头像URL过长")
    private String avatar;

    /** 个性签名（选填） */
    @Size(max = 200, message = "签名最长200字符")
    private String signature;

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
}
