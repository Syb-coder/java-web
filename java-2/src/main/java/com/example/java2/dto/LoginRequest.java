// 声明包路径
package com.example.java2.dto;

// 导入校验注解
import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求 DTO
 * <p>
 * 同时用于前台用户登录与管理员登录，仅包含用户名与明文密码。
 * </p>
 *
 * @param username 用户名
 * @param password 明文密码
 */
// 采用 record 声明：天然不可变，自动生成 accessor/equals/hashCode/toString，作为登录请求 DTO 避免被中途篡改
public record LoginRequest(
        // @NotBlank 同时拦截 null 与空白串，防止前端误传空表单触发空指针
        @NotBlank(message = "用户名不能为空") String username,
        @NotBlank(message = "密码不能为空") String password
) {
}
