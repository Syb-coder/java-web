package com.example.java11.service;  // 服务层包，存放业务逻辑

import com.example.java11.dto.LoginRequest;
import com.example.java11.dto.LoginResponse;
import com.example.java11.dto.RegisterRequest;
import com.example.java11.model.AdminUser;
import com.example.java11.model.User;
import com.example.java11.model.UserStatus;
import com.example.java11.repository.AdminUserRepository;
import com.example.java11.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 认证服务
 * <p>
 * 负责用户与管理员的注册、登录、登出及会话管理。
 * 密码采用 BCrypt 加密存储，登录态通过 HttpSession 维护。
 * </p>
 */
@Service
public class AuthService {

    /** Session 中存储当前登录用户的 key */
    public static final String SESSION_USER_KEY = "currentUser";

    /** Session 中存储当前登录管理员的 key */
    public static final String SESSION_ADMIN_KEY = "adminUser";

    /** 用户数据访问层 */
    private final UserRepository userRepository;

    /** 管理员数据访问层 */
    private final AdminUserRepository adminUserRepository;

    /** BCrypt 密码编码器，用于密码加密与验证 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 构造器注入依赖
     *
     * @param userRepository     用户数据访问层
     * @param adminUserRepository 管理员数据访问层
     */
    public AuthService(UserRepository userRepository, AdminUserRepository adminUserRepository) {
        this.userRepository = userRepository;
        this.adminUserRepository = adminUserRepository;
    }

    /**
     * 用户注册
     * <p>
     * 校验用户名唯一性，BCrypt 加密密码后创建用户实体，
     * 注册成功后自动登录并返回登录响应。
     * </p>
     *
     * @param req 注册请求 DTO
     * @return 登录响应（含用户信息与 token）
     */
    @Transactional
    public LoginResponse register(RegisterRequest req) {
        // 校验用户名唯一性
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        // BCrypt 加密密码
        String encodedPassword = passwordEncoder.encode(req.getPassword());
        // 创建用户实体
        User user = new User(req.getUsername(), encodedPassword);
        user.setEmail(req.getEmail());
        user.setNickname(req.getUsername());
        userRepository.save(user);

        // 注册成功后返回登录响应
        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
                "USER",
                null
        );
    }

    /**
     * 用户登录
     * <p>
     * 校验用户名与密码，检查账户封禁状态，登录成功后写入 session。
     * </p>
     *
     * @param req     登录请求 DTO
     * @param session HTTP 会话
     * @return 登录响应（含用户信息与 token）
     */
    public LoginResponse login(LoginRequest req, HttpSession session) {
        Optional<User> optional = userRepository.findByUsername(req.getUsername());
        if (optional.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }
        User user = optional.get();
        // 校验密码
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        // 检查封禁状态
        if (user.getStatus() == UserStatus.BANNED) {
            throw new RuntimeException("账户已被封禁，请联系管理员");
        }
        // 写入 session
        session.setAttribute(SESSION_USER_KEY, user);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
                "USER",
                session.getId()
        );
    }

    /**
     * 管理员登录
     * <p>
     * 校验管理员用户名与密码，登录成功后写入 session。
     * 返回的 role 为 ADMIN 或 MODERATOR。
     * </p>
     *
     * @param req     登录请求 DTO
     * @param session HTTP 会话
     * @return 登录响应（含管理员信息与 token）
     */
    public LoginResponse adminLogin(LoginRequest req, HttpSession session) {
        Optional<AdminUser> optional = adminUserRepository.findByUsername(req.getUsername());
        if (optional.isEmpty()) {
            throw new RuntimeException("管理员用户名或密码错误");
        }
        AdminUser admin = optional.get();
        // 校验密码
        if (!passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
            throw new RuntimeException("管理员用户名或密码错误");
        }
        // 写入 session
        session.setAttribute(SESSION_ADMIN_KEY, admin);

        return new LoginResponse(
                admin.getId(),
                admin.getUsername(),
                null,
                null,
                admin.getRole().name(),
                session.getId()
        );
    }

    /**
     * 登出：失效当前 session
     *
     * @param session HTTP 会话
     */
    public void logout(HttpSession session) {
        session.invalidate();
    }

    /**
     * 获取当前登录用户
     *
     * @param session HTTP 会话
     * @return 当前登录用户实体，未登录时返回 null
     */
    public User getCurrentUser(HttpSession session) {
        Object attribute = session.getAttribute(SESSION_USER_KEY);
        if (attribute instanceof User) {
            return (User) attribute;
        }
        return null;
    }

    /**
     * 获取当前登录管理员
     *
     * @param session HTTP 会话
     * @return 当前登录管理员实体，未登录时返回 null
     */
    public AdminUser getCurrentAdmin(HttpSession session) {
        Object attribute = session.getAttribute(SESSION_ADMIN_KEY);
        if (attribute instanceof AdminUser) {
            return (AdminUser) attribute;
        }
        return null;
    }
}
