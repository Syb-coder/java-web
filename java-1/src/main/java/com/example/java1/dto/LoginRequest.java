// 声明包路径，归类为 dto 层，存放数据传输对象（DTO）
package com.example.java1.dto;

// 导入 @NotBlank 校验注解，约束字符串非 null 且去空白后非空
import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求 DTO
 * <p>
 * 用于管理员与读者的登录接口。username 字段对管理员为用户名，对读者为学号/工号。
 * 使用 record 关键字（JDK 16+）简化不可变 DTO 定义。
 * </p>
 *
 * @param username 用户名/学号
 * @param password 明文密码
 */
public record LoginRequest(
        @NotBlank(message = "用户名不能为空") String username, // @NotBlank 防止空用户名提交，登录前必须定位账号
        @NotBlank(message = "密码不能为空") String password // @NotBlank 防止空密码提交，密码经 BCrypt 校验
) {
}
