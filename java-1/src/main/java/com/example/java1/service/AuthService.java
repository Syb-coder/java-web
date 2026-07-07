// 声明当前类所在的包路径，归类为 service 业务服务层
package com.example.java1.service;

import com.example.java1.dto.ChangePasswordRequest; // 引入修改密码请求 DTO，封装旧密码与新密码
import com.example.java1.dto.LoginRequest; // 引入登录请求 DTO，封装用户名与密码
import com.example.java1.dto.LoginResponse; // 引入登录响应 DTO，用于返回登录成功信息
import com.example.java1.model.AdminUser; // 引入管理员实体类，对应数据库 admin_user 表
import com.example.java1.repository.AdminUserRepository; // 引入管理员 JPA 仓储
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 引入 BCrypt 编码器，用于密码加密与比对
import org.springframework.stereotype.Service; // 引入 @Service 注解，标记服务层组件

import java.time.LocalDateTime; // 引入时间类，用于记录最近登录时间

/**
 * 管理员认证业务服务
 * <p>
 * 负责后台管理员登录校验与密码修改。密码使用 BCrypt 加密存储与比对，
 * 避免明文落库导致的安全风险。
 * </p>
 */
@Service // 声明为 Spring 服务组件，由 IoC 容器管理为单例 Bean
public class AuthService {

    /** 管理员仓储 */
    private final AdminUserRepository repository; // 管理员仓储，final 保证不可变
    /** BCrypt 编码器（线程安全，可作为单例共享） */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // BCrypt 编码器实例化，无状态可共享

    /**
     * 构造方法注入依赖
     *
     * @param repository 管理员仓储
     */
    public AuthService(AdminUserRepository repository) { // 构造方法注入仓储依赖
        this.repository = repository; // 赋值管理员仓储
    }

    /**
     * 编码明文密码（用于初始化管理员）
     *
     * @param rawPassword 明文密码
     * @return BCrypt 加密后的密码
     */
    public String encodePassword(String rawPassword) { // 密码编码方法，供初始化数据调用
        return passwordEncoder.encode(rawPassword); // 调用 BCrypt 编码，每次生成随机盐值
    }

    /**
     * 管理员登录校验
     *
     * @param req 登录请求
     * @return 登录响应（成功）或 null（失败）
     */
    public LoginResponse login(LoginRequest req) { // 登录校验业务入口方法
        AdminUser user = repository.findByUsername(req.username()).orElse(null); // 按用户名查询管理员，不存在返回 null
        // 用户不存在或密码不匹配
        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) { // 用户不存在或密码比对失败，统一返回 null 避免泄露用户是否存在
            return null; // 返回 null 表示登录失败，由上层转换为 401 响应
        }
        // 更新最近登录时间
        user.setLastLoginAt(LocalDateTime.now()); // 记录本次登录时间，用于安全审计
        repository.save(user); // 持久化登录时间更新
        return LoginResponse.from(user); // 转换为登录响应 DTO 返回
    }

    /**
     * 根据用户名获取管理员（用于拦截器校验登录态）
     *
     * @param username 用户名
     * @return 管理员（可能为空）
     */
    public AdminUser getByUsername(String username) { // 按用户名查询管理员实体方法
        return repository.findByUsername(username).orElse(null); // 不存在时返回 null，由调用方判空处理
    }

    /**
     * 修改密码
     * <p>校验旧密码后更新为新密码。业务规则：新密码不少于 6 位，且不得与旧密码相同。</p>
     *
     * @param userId 管理员 ID
     * @param req    修改密码请求
     * @return true 修改成功，false 旧密码错误或用户不存在
     * @throws IllegalArgumentException 新密码不合规（为空、长度不足、与旧密码相同）
     */
    public boolean changePassword(Long userId, ChangePasswordRequest req) { // 修改密码业务入口方法
        // 校验新密码长度
        if (req.newPassword() == null || req.newPassword().length() < 6) { // 新密码为空或不足 6 位，防止弱密码
            throw new IllegalArgumentException("新密码至少 6 位"); // 抛出参数异常，触发 400 响应
        }
        // 校验新旧密码不同
        if (req.newPassword().equals(req.oldPassword())) { // 新旧密码相同，防止用户未实际修改密码
            throw new IllegalArgumentException("新密码不能与旧密码相同"); // 抛出参数异常，触发 400 响应
        }
        AdminUser user = repository.findById(userId).orElse(null); // 按用户 ID 加载管理员实体
        if (user == null) { // 用户不存在
            return false; // 返回失败，由上层处理
        }
        // 校验旧密码
        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) { // 旧密码比对失败，验证操作者身份
            return false; // 旧密码错误返回失败
        }
        user.setPassword(passwordEncoder.encode(req.newPassword())); // 设置新密码（BCrypt 加密后存储）
        repository.save(user); // 持久化密码更新
        return true; // 返回修改成功
    }
}
