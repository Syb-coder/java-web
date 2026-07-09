package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入非空校验注解
import jakarta.validation.constraints.Size;  // 导入长度范围校验注解

/**
 * 修改密码请求 DTO
 * <p>
 * 需验证旧密码，新密码最少 6 位，修改后自动失效会话。
 * </p>
 */
public class ChangePasswordRequest {  // 修改密码请求 DTO 类定义

    @NotBlank(message = "旧密码不能为空")  // 非空字符串校验:旧密码必填,用于服务端二次验证身份
    private String oldPassword;  // 旧密码(服务端用 BCrypt 比对当前账户哈希)

    @NotBlank(message = "新密码不能为空")  // 非空字符串校验:新密码必填
    @Size(min = 6, max = 20, message = "新密码长度需在6-20字符之间")  // 长度范围校验:新密码 6~20 字符,保障最低安全强度
    private String newPassword;  // 新密码(修改后服务端 BCrypt 加密存储,并使现有会话失效)

    // —— oldPassword 字段的 getter/setter ——
    public String getOldPassword() {  // 获取旧密码
        return oldPassword;  // 返回旧密码
    }

    public void setOldPassword(String oldPassword) {  // 设置旧密码
        this.oldPassword = oldPassword;  // 赋值旧密码
    }

    // —— newPassword 字段的 getter/setter ——
    public String getNewPassword() {  // 获取新密码
        return newPassword;  // 返回新密码
    }

    public void setNewPassword(String newPassword) {  // 设置新密码
        this.newPassword = newPassword;  // 赋值新密码
    }
}
