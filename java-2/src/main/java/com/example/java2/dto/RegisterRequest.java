// 声明包路径
package com.example.java2.dto;

// 导入校验注解
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户注册请求 DTO
 * <p>
 * 前台用户注册提交数据。用户名与密码均必填，密码长度不少于 6 位。
 * </p>
 *
 * @param username 用户名
 * @param password 明文密码（后端 BCrypt 加密后存储）
 * @param nickname 昵称（可选）
 */
// 采用 record 声明：天然不可变，编译器自动生成 accessor、equals、hashCode、toString，适合作为请求 DTO
public record RegisterRequest(
        // @NotBlank 拒绝 null 与纯空白字符串；区别于 @NotNull（仅拒绝 null，空串可通过）。message 为校验失败时返回前端的提示语
        @NotBlank(message = "用户名不能为空") String username,
        // 密码必须非空白
        @NotBlank(message = "密码不能为空")
        // @Size 限定字符串长度，min=6 是兼顾安全性与用户易用性的最低门槛（低于 6 位易被暴力破解）
        @Size(min = 6, message = "密码至少 6 位") String password,
        // 昵称为可选字段，注册时可不填，由前端默认值或后端兜底处理
        String nickname
) {
}
