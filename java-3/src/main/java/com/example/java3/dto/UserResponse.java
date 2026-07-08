// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

/**
 * 学生用户信息响应 DTO
 * <p>
 * 用于个人中心展示与后台用户列表查询，不包含密码等敏感字段。
 * </p>
 */
public class UserResponse {

    // 用户主键 ID
    /** 用户 ID */
    private Long id;

    // 学号，校园实名认证依据
    /** 学号 */
    private String studentId;

    // 用户名，登录账号
    /** 用户名 */
    private String username;

    // 昵称，前端展示用
    /** 昵称 */
    private String nickname;

    // 联系电话，可选字段
    /** 联系电话 */
    private String phone;

    // 头像 URL，前端 <img src> 直接使用
    /** 头像 URL */
    private String avatar;

    // 账号状态：ACTIVE 表示正常，BANNED 表示封禁
    /** 账号状态 */
    private String status;

    // 注册时间字符串（已格式化），前端直接展示
    /** 注册时间 */
    private String createdAt;

    /**
     * 由 User 实体构造响应
     * <p>
     * Service 层将实体字段逐项拷贝到 DTO，避免暴露实体内部结构。
     * </p>
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
        this.id = id;                       // 赋值用户 ID
        this.studentId = studentId;         // 赋值学号
        this.username = username;           // 赋值用户名
        this.nickname = nickname;           // 赋值昵称
        this.phone = phone;                 // 赋值电话
        this.avatar = avatar;               // 赋值头像 URL
        this.status = status;               // 赋值账号状态
        this.createdAt = createdAt;         // 赋值注册时间
    }

    // 获取用户 ID
    public Long getId() {
        return id;
    }

    // 设置用户 ID
    public void setId(Long id) {
        this.id = id;
    }

    // 获取学号
    public String getStudentId() {
        return studentId;
    }

    // 设置学号
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    // 获取用户名
    public String getUsername() {
        return username;
    }

    // 设置用户名
    public void setUsername(String username) {
        this.username = username;
    }

    // 获取昵称
    public String getNickname() {
        return nickname;
    }

    // 设置昵称
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // 获取联系电话
    public String getPhone() {
        return phone;
    }

    // 设置联系电话
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // 获取头像 URL
    public String getAvatar() {
        return avatar;
    }

    // 设置头像 URL
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    // 获取账号状态
    public String getStatus() {
        return status;
    }

    // 设置账号状态
    public void setStatus(String status) {
        this.status = status;
    }

    // 获取注册时间
    public String getCreatedAt() {
        return createdAt;
    }

    // 设置注册时间
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
