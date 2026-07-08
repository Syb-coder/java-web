// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，确保字符串非 null 且非空白
import jakarta.validation.constraints.NotBlank;
// 导入 Size 校验注解，限制字符串长度范围
import jakarta.validation.constraints.Size;

/**
 * 修改密码请求 DTO
 * <p>
 * 用户在个人中心修改密码时提交，需先校验旧密码正确性再写入新密码。
 * </p>
 */
public class ChangePasswordRequest {

    // 旧密码字段，需与服务端存储的 BCrypt 哈希比对验证
    /** 旧密码 */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    // 新密码字段，长度至少 6 位，服务端加密后覆盖原密码
    /** 新密码（最少 6 位） */
    @NotBlank(message = "新密码不能为空")
    // Size：限定新密码长度 6-30 位，保证安全性
    @Size(min = 6, max = 30, message = "新密码长度需在 6-30 之间")
    private String newPassword;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法

    // 获取旧密码明文
    public String getOldPassword() {
        return oldPassword;
    }

    // 设置旧密码明文
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    // 获取新密码明文
    public String getNewPassword() {
        return newPassword;
    }

    // 设置新密码明文
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
