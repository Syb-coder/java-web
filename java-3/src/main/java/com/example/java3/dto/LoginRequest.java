// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，确保字符串非 null 且非空白
import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求 DTO（学生与管理员共用）
 * <p>
 * 接收前端登录表单数据，由 Controller 触发 @Valid 自动校验后交由 Service 鉴权。
 * </p>
 */
public class LoginRequest {

    // 用户名字段，登录凭证之一，必须填写
    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    // 密码字段，明文传输（建议 HTTPS 加密通道），服务端用 BCrypt 比对
    /** 密码（明文） */
    @NotBlank(message = "密码不能为空")
    private String password;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法，供 Spring 数据绑定与 JSON 序列化使用

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
}
