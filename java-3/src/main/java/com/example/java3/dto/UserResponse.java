package com.example.java3.dto;

/**
 * 学生用户信息响应 DTO
 */
public class UserResponse {

    /** 用户 ID */
    private Long id;

    /** 学号 */
    private String studentId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 联系电话 */
    private String phone;

    /** 头像 URL */
    private String avatar;

    /** 账号状态 */
    private String status;

    /** 注册时间 */
    private String createdAt;

    /**
     * 由 User 实体构造响应
     *
     * @param id        用户 ID
     * @param studentId 学号
     * @param username  用户名
     * @param nickname  昵称
     * @param phone     电话
     * @param avatar    头像
     * @param status    状态
     * @param createdAt 注册时间
     */
    public UserResponse(Long id, String studentId, String username, String nickname,
                        String phone, String avatar, String status, String createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.username = username;
        this.nickname = nickname;
        this.phone = phone;
        this.avatar = avatar;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
