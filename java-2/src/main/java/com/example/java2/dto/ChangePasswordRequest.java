// 声明包路径
package com.example.java2.dto;

// 导入校验注解
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改密码请求 DTO
 * <p>
 * 通用 DTO，前台用户与后台管理员共用。修改密码时需校验旧密码并设置合规新密码。
 * </p>
 *
 * @param oldPassword 旧密码（明文）
 * @param newPassword 新密码（明文，至少 6 位，不得与旧密码相同）
 */
// 采用 record 声明：不可变对象，自动生成 accessor/equals/hashCode/toString，作为修改密码请求 DTO 保证传输过程不被篡改
public record ChangePasswordRequest(
        // 旧密码需非空白，后端会与数据库密文比对验证身份
        @NotBlank(message = "旧密码不能为空") String oldPassword,
        // 新密码需非空白
        @NotBlank(message = "新密码不能为空")
        // @Size min=6 与注册密码策略保持一致，避免弱口令
        @Size(min = 6, message = "新密码至少 6 位") String newPassword
) {
}
