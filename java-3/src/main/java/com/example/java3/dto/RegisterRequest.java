// 声明当前类所属的包路径，统一放在 dto 子包下，用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，Validation 框架用它确保字符串非 null 且去除首尾空白后长度大于 0
import jakarta.validation.constraints.NotBlank;
// 导入 Size 校验注解，限制字符串长度或集合元素个数范围
import jakarta.validation.constraints.Size;

/**
 * 学生注册请求 DTO
 * <p>
 * 接收前端注册表单提交的数据，由 Controller 配合 @Valid 注解触发自动校验。
 * </p>
 */
public class RegisterRequest {

    // 学号字段，作为校园实名认证依据，必须填写且长度受控
    /** 学号（实名认证依据） */
    // NotBlank：禁止 null 和空白字符串；message 指定校验失败时的提示文案
    @NotBlank(message = "学号不能为空")
    // Size：限定学号字符串长度在 6-20 之间，防止过短或超长输入
    @Size(min = 6, max = 20, message = "学号长度需在 6-20 之间")
    private String studentId;

    // 用户名字段，作为登录账号使用，必须填写
    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    // 限定用户名长度 3-50 之间，保证可读性与唯一性约束
    @Size(min = 3, max = 50, message = "用户名长度需在 3-50 之间")
    private String username;

    // 密码字段，前端传入明文，服务端使用 BCrypt 加密后存储，不可为空
    /** 密码（明文，服务端加密） */
    @NotBlank(message = "密码不能为空")
    // 限定密码长度 6-30，兼顾安全性与易用性
    @Size(min = 6, max = 30, message = "密码长度需在 6-30 之间")
    private String password;

    // 昵称字段，用于前端显示，必填
    /** 昵称 */
    @NotBlank(message = "昵称不能为空")
    // Size 只限制最大长度 50，无下限要求
    @Size(max = 50, message = "昵称长度不能超过 50")
    private String nickname;

    // 联系电话字段，可选字段，无校验注解
    /** 联系电话（可选） */
    private String phone;

    // ===== Getter / Setter =====
    // 以下方法为 JavaBean 规范的访问器，供 Spring MVC 数据绑定与 JSON 序列化使用

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

    // 获取密码明文
    public String getPassword() {
        return password;
    }

    // 设置密码明文
    public void setPassword(String password) {
        this.password = password;
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
}
