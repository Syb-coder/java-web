package com.example.java8.service;

import com.example.java8.dto.ChangePasswordRequest;
import com.example.java8.dto.LoginRequest;
import com.example.java8.dto.LoginResponse;
import com.example.java8.model.AdminUser;
import com.example.java8.repository.AdminUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 管理员认证业务服务
 * <p>
 * 负责后台管理员登录校验与密码修改。密码使用 BCrypt 加密存储与比对。
 * </p>
 */
@Service
public class AuthService {

    private final AdminUserRepository repository;
    /** BCrypt 编码器（线程安全，可作为单例共享） */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(AdminUserRepository repository) {
        this.repository = repository;
    }

    /**
     * 编码明文密码（用于初始化管理员）
     *
     * @param rawPassword 明文密码
     * @return BCrypt 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 管理员登录校验
     *
     * @param req 登录请求
     * @return 登录响应（成功）或 null（失败）
     */
    public LoginResponse login(LoginRequest req) {
        AdminUser user = repository.findByUsername(req.username()).orElse(null);
        // 用户不存在或密码不匹配
        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            return null;
        }
        // 更新最近登录时间
        user.setLastLoginAt(LocalDateTime.now());
        repository.save(user);
        return LoginResponse.from(user);
    }

    /**
     * 根据用户名获取管理员（用于拦截器校验登录态）
     *
     * @param username 用户名
     * @return 管理员（可选）
     */
    public AdminUser getByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

    /**
     * 修改密码
     *
     * <p>校验旧密码后更新为新密码。新密码通过 BCrypt 重新加密存储。
     * 业务规则：新密码不少于 6 位，且不得与旧密码相同。</p>
     *
     * @param userId 管理员 ID
     * @param req    修改密码请求
     * @return true 修改成功，false 旧密码错误或用户不存在
     * @throws IllegalArgumentException 新密码不合规（为空、长度不足、与旧密码相同）
     */
    public boolean changePassword(Long userId, ChangePasswordRequest req) {
        // 校验新密码长度
        if (req.newPassword() == null || req.newPassword().length() < 6) {
            throw new IllegalArgumentException("新密码至少 6 位");
        }
        // 校验新旧密码不同
        if (req.newPassword().equals(req.oldPassword())) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }
        AdminUser user = repository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        // 校验旧密码
        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(req.newPassword()));
        repository.save(user);
        return true;
    }
}
