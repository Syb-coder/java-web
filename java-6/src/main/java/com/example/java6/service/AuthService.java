package com.example.java6.service; // 声明本类所在的包，位于 service 业务层

import com.example.java6.dto.ChangePasswordRequest; // 导入修改密码请求 DTO，封装旧密码与新密码字段
import com.example.java6.dto.LoginRequest; // 导入登录请求 DTO，封装用户名与密码字段
import com.example.java6.dto.LoginResponse; // 导入登录响应 DTO，用于向前端返回登录结果
import com.example.java6.model.AdminUser; // 导入管理员实体类，对应数据库 admin_user 表
import com.example.java6.repository.AdminUserRepository; // 导入管理员仓库接口，提供数据库访问能力
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 导入 BCrypt 编码器，用于密码加密与校验
import org.springframework.stereotype.Service; // 导入 @Service 注解，标记为 Spring Service 组件

import java.time.LocalDateTime; // 导入时间类型，用于记录最近登录时间

/**
 * 认证业务服务
 *
 * <p>负责管理员登录校验与登录态维护。密码使用 BCrypt 加密存储与比对。</p>
 */
@Service // 标记为 Spring Service 组件，由容器管理生命周期，业务层的核心注解
public class AuthService {

    private final AdminUserRepository repository; // 注入管理员仓库，用于数据库 CRUD 操作
    /** BCrypt 编码器（线程安全，可作为单例共享） */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // BCrypt 编码器实例，线程安全，用于密码加密与校验

    public AuthService(AdminUserRepository repository) { // 构造函数注入，Spring 自动注入 repository 依赖
        this.repository = repository; // 完成依赖赋值
    }

    /**
     * 编码明文密码（用于初始化管理员）
     *
     * @param rawPassword 明文密码
     * @return BCrypt 加密后的密码
     */
    public String encodePassword(String rawPassword) { // 密码编码方法，将明文密码加密为 BCrypt 密文
        return passwordEncoder.encode(rawPassword); // 调用 BCryptPasswordEncoder 编码明文，每次加密会生成随机盐值
    }

    /**
     * 登录校验
     *
     * @param req 登录请求
     * @return 登录响应（成功）或 null（失败）
     */
    public LoginResponse login(LoginRequest req) { // 登录校验方法，校验用户名与密码
        AdminUser user = repository.findByUsername(req.username()).orElse(null); // 按用户名查找管理员，不存在返回 null
        // 用户不存在或密码不匹配
        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) { // 用户不存在或 BCrypt 密码校验失败
            return null; // 返回 null 表示登录失败，由 Controller 转换为 401 响应
        }
        // 更新最近登录时间
        user.setLastLoginAt(LocalDateTime.now()); // 设置管理员最近登录时间为当前时刻
        repository.save(user); // 持久化更新后的管理员信息到数据库
        return LoginResponse.from(user); // 转换为 LoginResponse 响应对象返回，包含用户基本信息
    }

    /**
     * 根据用户名获取管理员信息（用于拦截器校验登录态）
     *
     * @param username 用户名
     * @return 管理员（可选）
     */
    public AdminUser getByUsername(String username) { // 根据用户名查询管理员方法，用于登录态校验
        return repository.findByUsername(username).orElse(null); // 调用仓库查询，不存在返回 null
    }

    /**
     * 修改密码
     *
     * <p>校验旧密码后更新为新密码。新密码通过 BCrypt 重新加密存储。</p>
     *
     * @param userId    管理员 ID
     * @param req       修改密码请求（含旧密码、新密码）
     * @return true 修改成功，false 旧密码错误或用户不存在
     * @throws IllegalArgumentException 新密码不合规（为空或与旧密码相同）
     */
    public boolean changePassword(Long userId, ChangePasswordRequest req) { // 修改密码方法，校验旧密码后更新新密码
        // 校验新密码非空
        if (req.newPassword() == null || req.newPassword().isBlank()) { // 新密码为 null 或空白字符串则不合规
            throw new IllegalArgumentException("新密码不能为空"); // 抛出非法参数异常，由全局异常处理器转换为 400 响应
        }
        // 校验新旧密码不同
        if (req.newPassword().equals(req.oldPassword())) { // 新旧密码相同则不合规
            throw new IllegalArgumentException("新密码不能与旧密码相同"); // 抛出非法参数异常，避免用户使用相同密码
        }
        AdminUser user = repository.findById(userId).orElse(null); // 根据用户 ID 查询管理员，不存在返回 null
        if (user == null) { // 用户不存在
            return false; // 返回 false 表示修改失败
        }
        // 校验旧密码
        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) { // BCrypt 校验旧密码失败
            return false; // 旧密码错误，返回 false
        }
        // 更新密码
        user.setPassword(passwordEncoder.encode(req.newPassword())); // 将新密码 BCrypt 加密后设置到实体
        repository.save(user); // 持久化更新后的密码到数据库
        return true; // 返回 true 表示修改成功
    }
}
