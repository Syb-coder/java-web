// 声明当前类所在包路径
package com.example.java3.service;

// 导入用户资料更新请求 DTO
import com.example.java3.dto.UserProfileRequest;
// 导入用户响应 DTO
import com.example.java3.dto.UserResponse;
// 导入用户实体模型
import com.example.java3.model.User;
// 导入用户状态枚举
import com.example.java3.model.UserStatus;
// 导入用户仓储接口（Spring Data JPA）
import com.example.java3.repository.UserRepository;
// 导入 @Service 注解
import org.springframework.stereotype.Service;

// 导入日期时间格式化器
import java.time.format.DateTimeFormatter;
// 导入 List 集合
import java.util.List;
// 导入 Optional 容器，用于优雅处理 null
import java.util.Optional;

/**
 * 学生用户服务
 * <p>
 * 负责用户信息查询、资料修改、违规封禁等。
 * </p>
 */
// 标识为业务层组件，由 Spring 容器管理为单例
@Service
public class UserService {

    // 日期格式化常量（线程安全）
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 用户仓储，通过构造方法注入
    private final UserRepository userRepository;

    // 构造方法注入：Spring 自动注入用户仓储 Bean
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
        // 调用 JPA 默认方法 findById，返回 Optional 便于调用方处理不存在的情况
        return userRepository.findById(id);
    }

    /**
     * 获取当前登录用户信息
     *
     * @param id 用户 ID
     * @return 用户响应
     */
    public UserResponse getProfile(Long id) {
        // 根据 ID 查询用户
        User u = userRepository.findById(id)
                // 用户不存在时抛异常
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        // 转换为响应 DTO 返回前端
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
        // 根据 ID 查询用户
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        // 仅当请求字段非 null 时更新，支持部分字段更新（PATCH 语义）
        if (req.getNickname() != null) {
            u.setNickname(req.getNickname());
        }
        if (req.getPhone() != null) {
            u.setPhone(req.getPhone());
        }
        if (req.getAvatar() != null) {
            u.setAvatar(req.getAvatar());
        }
        // 持久化更新后的用户实体
        userRepository.save(u);
        // 返回更新后的响应 DTO
        return toResponse(u);
    }

    /**
     * 管理员封禁用户
     *
     * @param id 用户 ID
     */
    public void ban(Long id) {
        // 根据 ID 查询用户
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        // 设置状态为 BANNED，封禁后用户无法登录
        u.setStatus(UserStatus.BANNED);
        // 持久化
        userRepository.save(u);
    }

    /**
     * 管理员解封用户
     *
     * @param id 用户 ID
     */
    public void unban(Long id) {
        // 根据 ID 查询用户
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        // 设置状态为 ACTIVE，恢复正常使用
        u.setStatus(UserStatus.ACTIVE);
        // 持久化
        userRepository.save(u);
    }

    /**
     * 查询全部学生（后台用）
     *
     * @return 用户列表
     */
    public List<User> findAll() {
        // 调用 JPA 默认方法查询全部用户
        return userRepository.findAll();
    }

    /**
     * 按用户名/学号模糊查询
     *
     * @param keyword 关键词
     * @return 用户列表
     */
    public List<User> search(String keyword) {
        // 先查询全部用户，再使用 Stream 过滤匹配项
        return userRepository.findAll().stream()
                // 任意字段（用户名/学号/昵称）包含关键词即命中
                .filter(u -> (u.getUsername() != null && u.getUsername().contains(keyword))
                        || (u.getStudentId() != null && u.getStudentId().contains(keyword))
                        || (u.getNickname() != null && u.getNickname().contains(keyword)))
                // 收集为 List
                .toList();
    }

    /**
     * 实体转响应 DTO
     *
     * @param u 用户实体
     * @return 响应 DTO
     */
    public UserResponse toResponse(User u) {
        // 构造响应 DTO，对状态和创建时间做 null 安全处理
        return new UserResponse(u.getId(), u.getStudentId(), u.getUsername(),
                u.getNickname(), u.getPhone(), u.getAvatar(),
                // 状态非 null 时取枚举名，避免 NPE
                u.getStatus() != null ? u.getStatus().name() : null,
                // 创建时间非 null 时按指定格式格式化
                u.getCreatedAt() != null ? u.getCreatedAt().format(FMT) : null);
    }
}
