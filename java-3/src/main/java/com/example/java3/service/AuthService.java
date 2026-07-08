// 声明当前类所在的包路径，便于 Spring 组件扫描与依赖管理
package com.example.java3.service;

// 导入修改密码请求 DTO
import com.example.java3.dto.ChangePasswordRequest;
// 导入登录请求 DTO
import com.example.java3.dto.LoginRequest;
// 导入登录响应 DTO
import com.example.java3.dto.LoginResponse;
// 导入注册请求 DTO
import com.example.java3.dto.RegisterRequest;
// 导入管理员实体模型
import com.example.java3.model.AdminUser;
// 导入学生用户实体模型
import com.example.java3.model.User;
// 导入用户状态枚举（ACTIVE / BANNED）
import com.example.java3.model.UserStatus;
// 导入管理员仓储接口（Spring Data JPA 自动生成实现）
import com.example.java3.repository.AdminUserRepository;
// 导入用户仓储接口
import com.example.java3.repository.UserRepository;
// 导入 BCrypt 加密器，Spring Security 提供的强哈希算法
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// 导入 @Service 注解，标记为业务层 Bean
import org.springframework.stereotype.Service;

// 导入日期时间格式化器
import java.time.format.DateTimeFormatter;

/**
 * 认证服务
 * <p>
 * 负责学生注册、学生/管理员登录、密码修改等核心认证逻辑。
 * 密码统一采用 BCrypt 加密；修改密码后需使原会话失效。
 * </p>
 */
// 标识为业务层组件，由 Spring 容器管理为单例
@Service
public class AuthService {

    // 日期格式化常量，用于统一时间字符串格式（线程安全可共享）
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** BCrypt 加密器（单例，线程安全） */
    // BCrypt 加密器实例，每次 encode 会自动生成随机盐，无需手动管理盐值
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 用户仓储，通过构造方法注入，便于对学生表 CRUD
    private final UserRepository userRepository;
    // 管理员仓储，通过构造方法注入，便于对管理员表 CRUD
    private final AdminUserRepository adminUserRepository;

    // 构造方法注入：Spring 自动将两个仓储 Bean 注入，便于单元测试 mock
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
        // 校验学号唯一（实名认证约束），同一学号不可重复注册
        if (userRepository.existsByStudentId(req.getStudentId())) {
            // 学号已存在则抛异常，由 Controller 转为 422 响应
            throw new IllegalArgumentException("该学号已注册");
        }
        // 校验用户名唯一，避免登录时出现同名歧义
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 使用 BCrypt 加密密码，不保存明文，防止数据库泄露后密码暴露
        String encoded = passwordEncoder.encode(req.getPassword());
        // 构造用户实体并填入学号、用户名、加密密码、昵称
        User user = new User(req.getStudentId(), req.getUsername(), encoded, req.getNickname());
        // 设置联系电话（可选字段）
        user.setPhone(req.getPhone());
        // 持久化到数据库，JPA 自动生成 INSERT 语句
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
        // 根据用户名查询用户，不存在则返回 Optional 空
        User user = userRepository.findByUsername(req.getUsername())
                // 用户不存在时抛异常，统一返回"用户名或密码错误"避免泄露用户是否存在
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
        // BCrypt 校验：内部使用盐值比对，防止彩虹表攻击
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            // 密码不匹配则抛异常
            throw new IllegalArgumentException("用户名或密码错误");
        }
        // 封禁账号禁止登录，阻断违规用户继续交易
        if (user.getStatus() == UserStatus.BANNED) {
            throw new IllegalArgumentException("账号已被封禁，请联系管理员");
        }
        // 构造登录响应，包含用户 ID、用户名、昵称、角色标识、学号
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
        // 根据用户名查询管理员
        AdminUser admin = adminUserRepository.findByUsername(req.getUsername())
                // 不存在则统一返回"用户名或密码错误"
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
        // BCrypt 校验管理员密码
        if (!passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        // 构造管理员登录响应，角色为 ADMIN，学号字段为 null
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
        // 根据 ID 查询用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        // 校验旧密码是否正确，防止他人冒用会话改密
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }
        // 新旧密码不能相同，避免用户图省事导致密码强度无变化
        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }
        // 加密新密码并写回实体
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        // 持久化更新
        userRepository.save(user);
    }

    /**
     * 管理员修改密码
     *
     * @param adminId 管理员 ID
     * @param req     修改密码请求
     */
    public void changeAdminPassword(Long adminId, ChangePasswordRequest req) {
        // 根据 ID 查询管理员
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("管理员不存在"));
        // 校验旧密码
        if (!passwordEncoder.matches(req.getOldPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }
        // 校验新旧密码不同
        if (passwordEncoder.matches(req.getNewPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }
        // 加密新密码并写回
        admin.setPassword(passwordEncoder.encode(req.getNewPassword()));
        // 持久化
        adminUserRepository.save(admin);
    }

    /**
     * 提供加密器供 DataInitializer 初始化密码使用
     *
     * @return BCryptPasswordEncoder
     */
    // 暴露加密器供外部初始化数据使用，避免重复实例化
    public BCryptPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
