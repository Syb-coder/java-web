package com.example.java6.dto; // 定义 DTO 接口所在包，DTO 用于分层间的数据传输，避免直接暴露实体

/**
 * 修改密码请求 DTO
 * 用于后台管理员修改自身密码时提交的请求体
 * 服务端需校验旧密码正确性，并保证新密码与旧密码不同
 *
 * @param oldPassword 旧密码（明文，需校验）
 * @param newPassword 新密码（明文，将与旧密码不同）
 */
public record ChangePasswordRequest( // Record 关键字声明不可变 DTO，自动生成构造器与访问方法
        String oldPassword, // 旧密码字段，服务端需与数据库存储的密码进行比对验证
        String newPassword // 新密码字段，校验通过后覆盖存储（生产环境应加密存储而非明文）
) {
}
