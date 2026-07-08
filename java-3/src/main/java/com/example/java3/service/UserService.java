package com.example.java3.service;

import com.example.java3.dto.UserProfileRequest;
import com.example.java3.dto.UserResponse;
import com.example.java3.model.User;
import com.example.java3.model.UserStatus;
import com.example.java3.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 学生用户服务
 * <p>
 * 负责用户信息查询、资料修改、违规封禁等。
 * </p>
 */
@Service
public class UserService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 按 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户 Optional
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 获取当前登录用户信息
     *
     * @param id 用户 ID
     * @return 用户响应
     */
    public UserResponse getProfile(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return toResponse(u);
    }

    /**
     * 更新用户资料（昵称、电话、头像）
     *
     * @param id  用户 ID
     * @param req 资料请求
     * @return 更新后的用户响应
     */
    public UserResponse updateProfile(Long id, UserProfileRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (req.getNickname() != null) {
            u.setNickname(req.getNickname());
        }
        if (req.getPhone() != null) {
            u.setPhone(req.getPhone());
        }
        if (req.getAvatar() != null) {
            u.setAvatar(req.getAvatar());
        }
        userRepository.save(u);
        return toResponse(u);
    }

    /**
     * 管理员封禁用户
     *
     * @param id 用户 ID
     */
    public void ban(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        u.setStatus(UserStatus.BANNED);
        userRepository.save(u);
    }

    /**
     * 管理员解封用户
     *
     * @param id 用户 ID
     */
    public void unban(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        u.setStatus(UserStatus.ACTIVE);
        userRepository.save(u);
    }

    /**
     * 查询全部学生（后台用）
     *
     * @return 用户列表
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * 按用户名/学号模糊查询
     *
     * @param keyword 关键词
     * @return 用户列表
     */
    public List<User> search(String keyword) {
        return userRepository.findAll().stream()
                .filter(u -> (u.getUsername() != null && u.getUsername().contains(keyword))
                        || (u.getStudentId() != null && u.getStudentId().contains(keyword))
                        || (u.getNickname() != null && u.getNickname().contains(keyword)))
                .toList();
    }

    /**
     * 实体转响应 DTO
     *
     * @param u 用户实体
     * @return 响应 DTO
     */
    public UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getStudentId(), u.getUsername(),
                u.getNickname(), u.getPhone(), u.getAvatar(),
                u.getStatus() != null ? u.getStatus().name() : null,
                u.getCreatedAt() != null ? u.getCreatedAt().format(FMT) : null);
    }
}
