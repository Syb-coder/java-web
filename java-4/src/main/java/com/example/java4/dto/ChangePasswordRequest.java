// 声明包路径，存放数据传输对象（DTO）
package com.example.java4.dto;

// 导入参数校验注解
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改密码请求 DTO
 * <p>
 * 读者与管理员共用，修改密码时需验证旧密码，新密码最少 6 位。
 * </p>
 */
public class ChangePasswordRequest {

    /** 旧密码（明文，后端用 BCrypt 比对验证） */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /** 新密码（明文，后端 BCrypt 加密后存储） */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度需在 6-20 个字符之间")
    private String newPassword;

    // ===== Getter / Setter =====
    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
