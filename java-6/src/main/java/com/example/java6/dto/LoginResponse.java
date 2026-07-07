package com.example.java6.dto; // 定义 DTO 接口所在包，DTO 用于分层间的数据传输，避免直接暴露实体

import com.example.java6.model.AdminUser; // 导入管理员实体类，作为 from 工厂方法的入参类型

/**
 * 登录响应 DTO
 * 登录认证成功后返回给前端的管理员基本信息
 * 不包含密码等敏感字段，避免敏感信息泄露
 *
 * @param id          管理员 ID
 * @param username    用户名
 * @param displayName 显示名称
 */
public record LoginResponse( // Record 关键字声明不可变 DTO，自动生成构造器与访问方法
        Long id, // 管理员主键 ID，用于后续接口标识当前登录用户
        String username, // 用户名，登录账号
        String displayName // 显示名称，用于前端展示（如顶部导航栏的用户欢迎语）
) {
    /**
     * 从实体构造响应
     * 采用静态工厂方法封装实体到 DTO 的转换逻辑，集中管理映射规则
     * 隔离实体结构变化对 DTO 的影响，符合依赖倒置原则
     *
     * @param user 管理员实体
     * @return 登录响应
     */
    public static LoginResponse from(AdminUser user) { // 静态工厂方法，从 AdminUser 实体创建 LoginResponse
        return new LoginResponse( // 调用 Record 自动生成的全参构造器
                user.getId(), // 提取实体主键 ID
                user.getUsername(), // 提取用户名
                user.getDisplayName() // 提取显示名称
        );
    }
}
