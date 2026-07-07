// 声明包路径，归类为 dto 层，存放数据传输对象（DTO）
package com.example.java1.dto;

// 导入 @NotBlank 校验注解，约束字符串非 null 且去空白后非空
import jakarta.validation.constraints.NotBlank;

/**
 * 修改密码请求 DTO
 *
 * @param oldPassword 旧密码（明文）
 * @param newPassword 新密码（明文，至少 6 位）
 */
public record ChangePasswordRequest(
        @NotBlank(message = "旧密码不能为空") String oldPassword, // @NotBlank 校验旧密码，防止空提交绕过旧密码校验导致越权改密
        @NotBlank(message = "新密码不能为空") String newPassword // @NotBlank 校验新密码，业务层另校验长度 ≥ 6 位以保证密码强度
) {
}
