// 声明包路径
package com.example.java2.dto;

// 导入实体类
import com.example.java2.model.User;

/**
 * 前台用户响应 DTO
 * <p>
 * 用于个人中心展示、后台用户列表展示。不包含密码字段，避免敏感信息泄露。
 * </p>
 *
 * @param id          用户 ID
 * @param username    用户名
 * @param nickname    昵称
 * @param enabled     是否启用
 * @param lastLoginAt 最近登录时间
 * @param createTime  注册时间
 * @param updateTime  最后修改时间
 */
// 采用 record 声明：不可变响应 DTO，自动生成 accessor/equals/hashCode/toString；刻意省略 password 字段，避免敏感信息经接口泄露
public record UserResponse(
        Long id,             // 用户主键 ID
        String username,     // 登录账号名
        String nickname,     // 昵称，可为 null（用户未设置时）
        boolean enabled,     // 账号启用状态：false 表示被禁用，无法登录
        String lastLoginAt,  // 最近登录时间字符串，null 表示从未登录
        String createTime,   // 注册时间字符串，用于个人中心展示
        String updateTime    // 最后修改时间字符串
) {
    /**
     * 由用户实体构造响应
     *
     * @param user 用户实体
     * @return 用户响应
     */
    // 使用静态工厂方法 from()：相比构造方法可读性更好，且集中处理时间类型转字符串的逻辑，避免调用方重复判空
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.isEnabled(),
                // 时间对象转字符串前先判空，防止 NPE
                user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null,
                user.getCreateTime() != null ? user.getCreateTime().toString() : null,
                user.getUpdateTime() != null ? user.getUpdateTime().toString() : null
        );
    }
}
