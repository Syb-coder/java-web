package com.example.java12.dto;  // DTO 层包

import com.example.java12.model.User;  // 用户实体
import com.example.java12.model.UserStatus;  // 用户状态枚举

import java.time.LocalDateTime;  // 时间类型

/**
 * 用户信息响应 DTO
 * <p>
 * 用于个人中心展示用户资料，不包含密码等敏感字段。
 * </p>
 */
public class UserResponse {

    /** 用户 ID */
    private Long id;

    /** 登录账号 */
    private String account;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatar;

    /** 个性签名 */
    private String signature;

    /** 角色：USER / ADMIN */
    private String role;

    /** 账户状态：NORMAL / BANNED */
    private String status;

    /** 发帖数 */
    private Integer postCount;

    /** 注册时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 从实体构造响应 DTO
     *
     * @param user 用户实体
     */
    public UserResponse(User user) {
        this.id = user.getId();
        this.account = user.getAccount();
        this.nickname = user.getNickname();
        this.avatar = user.getAvatar();
        this.signature = user.getSignature();
        this.role = user.getRole().name();
        this.status = user.getStatus().name();
        this.postCount = user.getPostCount();
        this.createTime = user.getCreateTime();
        this.updateTime = user.getUpdateTime();
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
