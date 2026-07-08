package com.example.java3.service;

import com.example.java3.dto.ChangePasswordRequest;
import com.example.java3.dto.LoginRequest;
import com.example.java3.dto.LoginResponse;
import com.example.java3.dto.RegisterRequest;
import com.example.java3.model.AdminUser;
import com.example.java3.model.User;
import com.example.java3.model.UserStatus;
import com.example.java3.repository.AdminUserRepository;
import com.example.java3.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 认证服务
 * <p>
 * 负责学生注册、学生/管理员登录、密码修改等核心认证逻辑。
 * 密码统一采用 BCrypt 加密；修改密码后需使原会话失效。
 * </p>
 */
@Service
public class AuthService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** BCrypt 加密器（单例，线程安全） */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;

    public AuthService(UserRepository userRepository, AdminUserRepository adminUserRepository) {
        this.userRepository = userRepository;
        this.adminUserRepository = adminUserRepository;
    }

    /**
     * 学生注册
     *
     * @param req 注册请求
     * @throws IllegalArgumentException 学号或用户名已存在
     */
    public void register(RegisterRequest req) {
        // 校验学号唯一（实名认证约束）
        if (userRepository.existsByStudentId(req.getStudentId())) {
            throw new IllegalArgumentException("该学号已注册");
        }
        // 校验用户名唯一
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // BCrypt 加密密码，不保存明文
        String encoded = passwordEncoder.encode(req.getPassword());
        User user = new User(req.getStudentId(), req.getUsername(), encoded, req.getNickname());
        user.setPhone(req.getPhone());
        userRepository.save(user);
    }

    /**
     * 学生登录
     *
     * @param req 登录请求
     * @return 登录响应
     * @throws IllegalArgumentException 凭证无效或账号已封禁
     */
    public LoginResponse userLogin(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
        // BCrypt 校验：内部使用盐值比对，防止彩虹表攻击
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        // 封禁账号禁止登录
        if (user.getStatus() == UserStatus.BANNED) {
            throw new IllegalArgumentException("账号已被封禁，请联系管理员");
        }
        return new LoginResponse(user.getId(), user.getUsername(), user.getNickname(),
                "USER", user.getStudentId());
    }

    /**
     * 管理员登录
     *
     * @param req 登录请求
     * @return 登录响应
     * @throws IllegalArgumentException 凭证无效
     */
    public LoginResponse adminLogin(LoginRequest req) {
        AdminUser admin = adminUserRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
        if (!passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return new LoginResponse(admin.getId(), admin.getUsername(),
                admin.getDisplayName(), "ADMIN", null);
    }

    /**
     * 学生修改密码
     *
     * @param userId 用户 ID
     * @param req    修改密码请求
     * @throws IllegalArgumentException 旧密码错误或新旧密码相同
     */
    public void changeUserPassword(Long userId, ChangePasswordRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        // 校验旧密码
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }
        // 新旧密码不能相同
        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * 管理员修改密码
     *
     * @param adminId 管理员 ID
     * @param req     修改密码请求
     */
    public void changeAdminPassword(Long adminId, ChangePasswordRequest req) {
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("管理员不存在"));
        if (!passwordEncoder.matches(req.getOldPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }
        if (passwordEncoder.matches(req.getNewPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }
        admin.setPassword(passwordEncoder.encode(req.getNewPassword()));
        adminUserRepository.save(admin);
    }

    /**
     * 提供加密器供 DataInitializer 初始化密码使用
     *
     * @return BCryptPasswordEncoder
     */
    public BCryptPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
