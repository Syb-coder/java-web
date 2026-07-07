package com.example.java8.service;

import com.example.java8.dto.LoginRequest;
import com.example.java8.dto.LoginResponse;
import com.example.java8.dto.RegisterRequest;
import com.example.java8.dto.UserResponse;
import com.example.java8.model.User;
import com.example.java8.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 前台用户业务服务
 * <p>
 * 负责用户注册、登录、查询与密码加密。与后台管理员 {@link AuthService} 分离，
 * 但密码同样使用 BCrypt 加密。
 * </p>
 */
@Service
public class UserService {

    private final UserRepository repository;
    /** BCrypt 编码器（线程安全） */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * 注册新用户
     *
     * @param req 注册请求
     * @return 注册成功的用户实体
     * @throws IllegalArgumentException 用户名已存在或密码不合规
     */
    public User register(RegisterRequest req) {
        // 用户名唯一性校验
        if (repository.findByUsername(req.username()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 密码长度校验
        if (req.password().length() < 6) {
            throw new IllegalArgumentException("密码至少 6 位");
        }
        User user = new User();
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setNickname(req.nickname());
        user.setPhone(req.phone());
        user.setCreateTime(LocalDateTime.now());
        return repository.save(user);
    }

    /**
     * 用户登录校验
     *
     * @param req 登录请求
     * @return 登录响应（成功）或 null（失败）
     */
    public LoginResponse login(LoginRequest req) {
        User user = repository.findByUsername(req.username()).orElse(null);
        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            return null;
        }
        user.setLastLoginAt(LocalDateTime.now());
        repository.save(user);
        return LoginResponse.from(user, false);
    }

    /**
     * 根据 ID 获取用户
     *
     * @param id 用户 ID
     * @return 用户实体（不存在返回 null）
     */
    public User getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * 查询所有用户（后台用户管理使用）
     *
     * @return 全部用户列表
     */
    public List<User> listAll() {
        return repository.findAll();
    }

    /**
     * 统计用户总数
     *
     * @return 用户数量
     */
    public long count() {
        return repository.count();
    }

    /**
     * 将用户实体转换为响应 DTO
     *
     * @param u 用户实体
     * @return 响应 DTO
     */
    public UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getNickname(),
                u.getPhone(), u.getCreateTime(), u.getLastLoginAt());
    }
}
