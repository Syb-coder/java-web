// 声明包路径
package com.example.java2.dto;

// 导入实体类
import com.example.java2.model.AdminUser;
import com.example.java2.model.User;

/**
 * 登录响应 DTO
 * <p>
 * 登录成功后返回用户基本信息，前端用于展示与后续请求身份标识（依赖 Session）。
 * 同时兼容前台用户与后台管理员两种角色。
 * </p>
 *
 * @param id          用户/管理员 ID
 * @param username    用户名
 * @param displayName 展示名称（前台用户为昵称，管理员为 displayName）
 * @param role        角色：user / admin
 */
// 采用 record 声明：不可变响应 DTO，自动生成 accessor/equals/hashCode/toString；不返回 password 等敏感字段
public record LoginResponse(
        Long id,            // 用户或管理员主键 ID，前端用于标识当前登录主体
        String username,    // 登录账号名
        String displayName, // 展示名称：优先取昵称，缺失时回退为 username，保证前端总有可显示文本
        String role         // 角色标识：user 表示前台用户，admin 表示后台管理员，前端据此控制菜单与权限
) {
    /**
     * 由前台用户实体构造响应
     *
     * @param user 前台用户
     * @return 登录响应
     */
    // 使用静态工厂方法 from() 而非构造方法：命名更具语义化（明确数据来源），且可封装字段兜底逻辑，避免在调用方散落空值处理
    public static LoginResponse from(User user) {
        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                // 昵称为空时回退到用户名，确保 displayName 永不为 null
                user.getNickname() != null ? user.getNickname() : user.getUsername(),
                // 固定写死 "user"，标识前台用户角色
                "user"
        );
    }

    /**
     * 由后台管理员实体构造响应
     *
     * @param admin 管理员
     * @return 登录响应
     */
    // 重载 from()：与前台用户工厂方法同名，通过参数类型区分调用方，统一对外 API 命名风格
    public static LoginResponse from(AdminUser admin) {
        return new LoginResponse(
                admin.getId(),
                admin.getUsername(),
                // 管理员 displayName 为空时同样回退到用户名
                admin.getDisplayName() != null ? admin.getDisplayName() : admin.getUsername(),
                // 固定写死 "admin"，标识后台管理员角色
                "admin"
        );
    }
}
