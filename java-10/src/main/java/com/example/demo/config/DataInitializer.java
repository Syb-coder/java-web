package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器：应用启动时自动创建初始账号
 * <p>
 * 为什么实现 CommandLineRunner：Spring Boot 启动完成后自动执行 run 方法，
 * 保证数据库表已创建后再插入初始数据。
 * 为什么检查是否为空：避免每次重启重复插入，保证幂等性。
 * </p>
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 构造器注入：推荐方式，保证依赖不可变且便于测试
     *
     * @param userRepository 用户仓库
     * @param passwordEncoder 密码加密器
     */
    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 数据库已有数据时跳过初始化，避免重复插入
        if (userRepository.count() > 0) {
            return;
        }

        // 初始化三个角色账号：管理员、教师、学生
        createUser("admin", "admin123", "ADMIN", "系统管理员", "13800000001");
        createUser("teacher", "teacher123", "TEACHER", "刘老师", "13900139001");
        createUser("student", "student123", "STUDENT", "张三", "13800138001");
    }

    /**
     * 创建单个用户账号
     * <p>
     * 双重检查：先检查用户名是否存在，避免并发场景下重复插入。
     * 密码使用 BCrypt 加密后存储，原始明文不落库。
     * </p>
     *
     * @param username 用户名
     * @param password 明文密码（仅用于加密，不存储）
     * @param role 角色（大写枚举值）
     * @param realName 真实姓名
     * @param phone 联系电话
     */
    private void createUser(String username, String password, String role, String realName, String phone) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setRealName(realName);
        user.setPhone(phone);
        userRepository.save(user);
    }
}
