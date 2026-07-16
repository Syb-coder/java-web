package com.example.demo.service;

import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户业务逻辑层
 * <p>
 * 职责：封装用户相关业务操作，包括登录验证、CRUD、密码加密。
 * 为什么不返回 User 实体：实体包含 password 字段，通过 DTO 转换排除敏感信息。
 * 为什么用 @Service：标记为 Spring Bean，由容器管理生命周期，便于事务控制和注入。
 * </p>
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 构造器注入依赖
     *
     * @param userRepository 用户数据访问层
     * @param passwordEncoder 密码加密器（BCrypt）
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 用户登录验证
     * <p>
     * 为什么用 matches 而非直接比较字符串：BCrypt 每次加密结果不同（自带盐值），
     * 必须用 PasswordEncoder.matches(明文, 哈希) 进行验证。
     * 返回 null 表示登录失败，由 Controller 层决定返回 401 状态码。
     * </p>
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 登录成功返回 LoginResponse，失败返回 null
     */
    public LoginResponse login(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        // 用户不存在或密码不匹配时返回 null，不区分两种情况以防止用户枚举攻击
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        return toLoginResponse(user);
    }

    /**
     * 修改密码
     * <p>
     * 验证原密码后用 BCrypt 加密新密码并更新。
     * 为什么不区分"用户不存在"和"原密码错误"：与登录逻辑一致，防止用户枚举攻击。
     * </p>
     *
     * @param username 用户名
     * @param oldPassword 原密码（明文）
     * @param newPassword 新密码（明文）
     * @return true 修改成功，false 原密码错误或用户不存在
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表（不含密码）
     */
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * 创建用户
     * <p>
     * 密码通过 BCrypt 加密后存储，明文不落库。
     * 角色统一转为大写存储，保证数据库枚举值一致性。
     * </p>
     *
     * @param dto 用户信息（含明文密码）
     * @return 创建后的用户信息（不含密码）
     */
    public UserDTO create(UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        // 统一转大写存储，与数据库约定一致
        user.setRole(dto.getRole() != null ? dto.getRole().toUpperCase() : null);
        user.setPhone(dto.getPhone());
        User saved = userRepository.save(user);
        return toUserDTO(saved);
    }

    /**
     * 更新用户信息
     * <p>
     * 仅更新非空字段，实现部分更新语义。
     * 如果传入了 password 且非空，则更新密码（重新加密）。
     * </p>
     *
     * @param id 用户 ID
     * @param dto 更新数据
     * @return 更新后的用户信息，用户不存在时返回 null
     */
    public UserDTO update(Long id, UserDTO dto) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        if (dto.getRealName() != null) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole().toUpperCase());
        }
        // 密码非空时才更新，避免误将密码设为空
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        User saved = userRepository.save(user);
        return toUserDTO(saved);
    }

    /**
     * 删除用户
     *
     * @param id 用户 ID
     * @return 删除成功返回 true，用户不存在返回 false
     */
    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    /**
     * 检查用户名是否已存在
     *
     * @param username 用户名
     * @return 存在返回 true
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 将 User 实体转换为 LoginResponse
     * <p>
     * 角色：数据库大写 → API 小写，适配前端 Role 类型定义
     * </p>
     */
    private LoginResponse toLoginResponse(User user) {
        LoginResponse resp = new LoginResponse();
        resp.setUsername(user.getUsername());
        resp.setRealName(user.getRealName());
        resp.setRole(user.getRole() != null ? user.getRole().toLowerCase() : null);
        resp.setPhone(user.getPhone());
        return resp;
    }

    /**
     * 将 User 实体转换为 UserDTO
     * <p>
     * 不设置 password 字段，配合 @JsonProperty(WRITE_ONLY) 确保密码不出现在响应中
     * </p>
     */
    private UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setRole(user.getRole() != null ? user.getRole().toLowerCase() : null);
        dto.setPhone(user.getPhone());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
