package com.example.java6.dto; // 定义 DTO 接口所在包，DTO 用于分层间的数据传输，避免直接暴露实体

/**
 * 登录请求 DTO
 * 使用 Java Record 定义不可变数据载体，天然线程安全且简洁
 * 用于接收后台管理员登录时提交的账号密码
 *
 * @param username 用户名
 * @param password 密码（明文，传输后立即用于校验）
 */
public record LoginRequest( // Record 关键字声明不可变 DTO，编译器自动生成构造器、getter、equals/hashCode/toString
        String username, // 用户名字段，对应管理员账号的唯一标识
        String password // 密码字段，明文传输（生产环境应使用 HTTPS 加密通道）
) {
}
