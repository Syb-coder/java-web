package com.example.java12.service;  // 服务层包

import com.example.java12.dto.ChangePasswordRequest;  // 修改密码请求
import com.example.java12.dto.LoginRequest;  // 登录请求
import com.example.java12.dto.LoginResponse;  // 登录响应
import com.example.java12.dto.RegisterRequest;  // 注册请求
import com.example.java12.model.Role;  // 角色枚举
import com.example.java12.model.User;  // 用户实体
import com.example.java12.model.UserStatus;  // 用户状态枚举
import com.example.java12.repository.UserRepository;  // 用户数据访问层
import com.example.java12.util.TokenUtil;  // Token 工具类
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;  // BCrypt 编码器
import org.springframework.stereotype.Service;  // Service 注解
import org.springframework.transaction.annotation.Transactional;  // 事务注解

import java.util.Optional;  // Optional 包装类

/**
 * 认证服务
 * <p>
 * 负责用户注册、登录、登出及密码修改。
 * 密码采用 BCrypt 加密存储，登录态通过 TokenUtil 管理的 UUID Token 维护。
 * </p>
 */
@Service
public class AuthService {

    /** 用户数据访问层 */
    private final UserRepository userRepository;

    /** Token 工具类 */
    private final TokenUtil tokenUtil;

    /** BCrypt 密码编码器 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 构造器注入
     *
     * @param userRepository 用户数据访问层
     * @param tokenUtil      Token 工具类
     */
    public AuthService(UserRepository userRepository, TokenUtil tokenUtil) {
        this.userRepository = userRepository;
        this.tokenUtil = tokenUtil;
    }

    /**
     * 用户注册
     * <p>
     * 校验账号/昵称唯一性、密码确认一致性，BCrypt 加密密码后创建用户。
     * 注册成功后自动登录，返回 Token。
     * </p>
     *
     * @param req 注册请求
     * @return 登录响应（含 Token）
     * @throws RuntimeException 校验失败时抛出
     */
    @Transactional
    public LoginResponse register(RegisterRequest req) {
        // 校验两次密码一致
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        // 校验账号唯一性
        if (userRepository.existsByAccount(req.getAccount())) {
            throw new RuntimeException("账号已被注册");
        }
        // 校验昵称唯一性
        if (userRepository.existsByNickname(req.getNickname())) {
            throw new RuntimeException("昵称已被使用");
        }
        // BCrypt 加密密码
        String encodedPassword = passwordEncoder.encode(req.getPassword());
        // 创建用户
        User user = new User(req.getAccount(), req.getNickname(), encodedPassword);
        userRepository.save(user);
        // 生成 Token
        String token = tokenUtil.generateToken(user.getId());
        return new LoginResponse(user.getId(), user.getAccount(), user.getNickname(),
                user.getAvatar(), user.getRole().name(), token);
    }

    /**
     * 用户登录
     * <p>
     * 校验账号密码，检查封禁状态，生成 Token。
     * </p>
     *
     * @param req 登录请求
     * @return 登录响应（含 Token）
     * @throws RuntimeException 校验失败时抛出
     */
    public LoginResponse login(LoginRequest req) {
        Optional<User> optional = userRepository.findByAccount(req.getAccount());
        if (optional.isEmpty()) {
            throw new RuntimeException("账号不存在");
        }
        User user = optional.get();
        // 校验密码
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 检查封禁状态
        if (user.getStatus() == UserStatus.BANNED) {
            throw new RuntimeException("账号已被封禁，请联系管理员");
        }
        // 生成 Token
        String token = tokenUtil.generateToken(user.getId());
        return new LoginResponse(user.getId(), user.getAccount(), user.getNickname(),
                user.getAvatar(), user.getRole().name(), token);
    }

    /**
     * 登出：移除 Token
     *
     * @param token 当前 Token
     */
    public void logout(String token) {
        tokenUtil.removeToken(token);
    }

    /**
     * 修改密码
     * <p>
     * 验证旧密码，更新新密码，修改后自动失效当前 Token（强制重新登录）。
     * </p>
     *
     * @param userId 用户 ID
     * @param req    修改密码请求
     * @param token  当前 Token（用于修改后失效）
     * @throws RuntimeException 校验失败时抛出
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req, String token) {
        // 校验两次密码一致
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("两次输入的新密码不一致");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        // 验证旧密码
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        // 更新密码
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        // 失效当前 Token，强制重新登录
        tokenUtil.removeToken(token);
    }

    /**
     * 管理员登录（管理员使用普通用户相同的登录接口，此处保留单独方法供扩展）
     * <p>
     * 校验账号密码，检查是否为管理员角色，生成 Token。
     * </p>
     *
     * @param req 登录请求
     * @return 登录响应
     * @throwsRuntimeException 校验失败时抛出
     */
    public LoginResponse adminLogin(LoginRequest req) {
        LoginResponse response = login(req);
        // 检查是否为管理员
        User user = userRepository.findById(response.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("无管理员权限");
        }
        return response;
    }
}
