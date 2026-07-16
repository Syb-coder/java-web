package com.example.java11.service;  // 服务层包，存放业务逻辑

import com.example.java11.dto.ChangePasswordRequest;
import com.example.java11.dto.UserProfileRequest;
import com.example.java11.dto.UserResponse;
import com.example.java11.model.User;
import com.example.java11.model.UserStatus;
import com.example.java11.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务
 * <p>
 * 负责用户资料管理、密码修改、用户搜索与封禁/解封等操作。
 * </p>
 */
@Service
public class UserService {

    /** 用户数据访问层 */
    private final UserRepository userRepository;

    /** BCrypt 密码编码器，用于密码验证与加密 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 构造器注入依赖
     *
     * @param userRepository 用户数据访问层
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 获取用户资料
     *
     * @param userId 用户 ID
     * @return 用户响应 DTO
     */
    public UserResponse getUserProfile(Long userId) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        return toResponse(optional.get());
    }

    /**
     * 获取当前登录用户资料
     *
     * @param session HTTP 会话
     * @return 用户响应 DTO，未登录时抛出异常
     */
    public UserResponse getCurrentUserProfile(HttpSession session) {
        Object attribute = session.getAttribute(AuthService.SESSION_USER_KEY);
        if (!(attribute instanceof User user)) {
            throw new RuntimeException("请先登录");
        }
        // 从数据库重新加载最新数据，避免 session 中数据过期
        Optional<User> optional = userRepository.findById(user.getId());
        if (optional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        return toResponse(optional.get());
    }

    /**
     * 更新个人资料
     *
     * @param userId 用户 ID
     * @param req    资料更新请求 DTO
     * @return 更新后的用户响应 DTO
     */
    @Transactional
    public UserResponse updateProfile(Long userId, UserProfileRequest req) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User user = optional.get();
        // 仅更新非 null 字段
        if (req.getNickname() != null) {
            user.setNickname(req.getNickname());
        }
        if (req.getAvatar() != null) {
            user.setAvatar(req.getAvatar());
        }
        if (req.getSignature() != null) {
            user.setSignature(req.getSignature());
        }
        if (req.getBio() != null) {
            user.setBio(req.getBio());
        }
        if (req.getEmail() != null) {
            user.setEmail(req.getEmail());
        }
        userRepository.save(user);
        return toResponse(user);
    }

    /**
     * 修改密码
     * <p>
     * 验证旧密码是否正确，加密新密码后更新。
     * </p>
     *
     * @param userId 用户 ID
     * @param req    修改密码请求 DTO
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User user = optional.get();
        // 验证旧密码
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }
        // 加密新密码并更新
        String encodedNewPassword = passwordEncoder.encode(req.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    /**
     * 获取所有用户列表
     *
     * @return 用户响应列表
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 搜索用户
     *
     * @param keyword 搜索关键词（匹配用户名）
     * @return 匹配的用户响应列表
     */
    public List<UserResponse> searchUsers(String keyword) {
        return userRepository.findByUsernameContaining(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 封禁用户
     *
     * @param userId 用户 ID
     */
    @Transactional
    public void banUser(Long userId) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User user = optional.get();
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);
    }

    /**
     * 解封用户
     *
     * @param userId 用户 ID
     */
    @Transactional
    public void unbanUser(Long userId) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User user = optional.get();
        user.setStatus(UserStatus.NORMAL);
        userRepository.save(user);
    }

    /**
     * 更新用户发帖数
     *
     * @param userId 用户 ID
     * @param delta  增量（+1 或 -1）
     */
    @Transactional
    public void updatePostCount(Long userId, int delta) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User user = optional.get();
        user.setPostCount(user.getPostCount() + delta);
        userRepository.save(user);
    }

    /**
     * 实体转 DTO
     *
     * @param user 用户实体
     * @return 用户响应 DTO，实体为 null 时返回 null
     */
    private UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setSignature(user.getSignature());
        response.setBio(user.getBio());
        response.setPostCount(user.getPostCount());
        response.setFollowerCount(user.getFollowerCount());
        response.setFollowingCount(user.getFollowingCount());
        response.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdateTime(user.getUpdateTime());
        return response;
    }
}
