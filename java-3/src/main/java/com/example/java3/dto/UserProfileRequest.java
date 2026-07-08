// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

/**
 * 用户个人信息更新请求 DTO（不含密码、学号）
 * <p>
 * 用于个人中心资料编辑，仅允许修改非敏感字段，密码与学号走专用接口。
 * </p>
 */
public class UserProfileRequest {

    // 昵称字段，可选更新项
    /** 昵称 */
    private String nickname;

    // 联系电话字段，可选更新项
    /** 联系电话 */
    private String phone;

    // 头像 URL 字段，前端上传文件后回传地址
    /** 头像 URL */
    private String avatar;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法，供 Spring 数据绑定使用

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
}
