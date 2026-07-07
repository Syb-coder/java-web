// 声明包路径，归类为 dto 层，存放数据传输对象（DTO）
package com.example.java1.dto;

// 导入管理员实体类，用于 from 转换方法
import com.example.java1.model.AdminUser;

/**
 * 登录响应 DTO
 * <p>
 * 登录成功后返回用户基本信息，供前端展示与登录态校验。
 * </p>
 *
 * @param id       用户 ID
 * @param username 用户名
 * @param nickname 昵称
 */
// 登录响应不含 token，本系统采用 Session 会话保持登录态，简化个人项目实现
public record LoginResponse(
        Long id, // 用户主键 ID，前端用于后续请求定位用户
        String username, // 用户名，前端展示登录账号
        String nickname // 昵称，前端展示问候语
) {
    /**
     * 从管理员实体构造响应
     *
     * @param admin 管理员实体
     * @return 登录响应
     */
    public static LoginResponse from(AdminUser admin) {
        return new LoginResponse(admin.getId(), admin.getUsername(), admin.getNickname()); // 透传管理员 ID、用户名、昵称
    }
}
