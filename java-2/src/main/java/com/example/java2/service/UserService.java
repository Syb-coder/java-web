// 声明包路径
package com.example.java2.service;

// 导入 DTO 与实体类
import com.example.java2.dto.ChangePasswordRequest;
import com.example.java2.dto.LoginRequest;
import com.example.java2.dto.LoginResponse;
import com.example.java2.dto.RegisterRequest;
import com.example.java2.dto.UserResponse;
import com.example.java2.model.User;
import com.example.java2.repository.UserRepository;

// 导入 BCrypt 编码器
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 前台用户业务服务
 * <p>
 * 负责用户注册、登录、修改密码、查询个人信息、查询用户列表（后台用）、禁用/启用用户。
 * 密码使用 BCrypt 加密存储。
 * </p>
 */
@Service
public class UserService {

    // final 修饰：依赖在构造完成后不可变，满足 JMM 安全发布要求，多线程下不会读到半初始化状态
    private final UserRepository repository;
    /**
     * BCrypt 编码器（线程安全）
     * 为何作为单例共享：BCryptPasswordEncoder 无内部可变状态，重复实例化无意义开销
     * 为何 final：构造后不再变更，确保跨线程可见性
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 构造方法注入（Spring 4.3+ 单构造器自动注入，无需 @Autowired）
     * 为何不用字段注入：字段注入无法声明 final、不利于脱离容器做单元测试、隐藏循环依赖到运行期
     */
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * 用户注册
     *
     * @param req 注册请求
     * @return 注册成功的用户响应
     * @throws IllegalArgumentException 用户名已存在
     */
    public UserResponse register(RegisterRequest req) {
        // 用户名唯一约束前置校验：先查再插，避免落到 DB 唯一索引抛 SQL 异常导致流程不可控
        // 抛 IllegalArgumentException：参数语义错误（重复注册），由全局异常处理器转 400
        if (repository.existsByUsername(req.username())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 密码加密：明文绝不能入库，BCrypt 抗彩虹表与暴力破解
        // encode 内部会随机生成 salt 并嵌入密文，同一明文每次加密结果都不同
        String encoded = passwordEncoder.encode(req.password());
        // 昵称为空时回退为用户名，保证展示层永远有值，避免出现空白昵称
        User user = new User(
                req.username(),
                encoded,
                req.nickname() != null && !req.nickname().isBlank() ? req.nickname() : req.username()
        );
        repository.save(user);
        return UserResponse.from(user);
    }

    /**
     * 用户登录校验
     *
     * @param req 登录请求
     * @return 登录响应（成功）或 null（失败/账号已禁用）
     */
    public LoginResponse login(LoginRequest req) {
        User user = repository.findByUsername(req.username()).orElse(null);
        // 用户不存在与密码错误统一返回 null，避免账号是否存在的信息泄露（防枚举）
        // matches 内部机制：BCrypt 密文自包含 salt（前 22 位），matches 自动解析盐值重算哈希比对
        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            return null;
        }
        // 禁用账号拒绝登录：即使密码正确也不放行，配合后台管理实现封禁
        if (!user.isEnabled()) {
            return null;
        }
        // 刷新最近登录时间，用于风控与个人中心展示
        user.setLastLoginAt(LocalDateTime.now());
        repository.save(user);
        return LoginResponse.from(user);
    }

    /**
     * 根据用户名获取用户实体（用于拦截器校验登录态）
     */
    public User getByUsername(String username) {
        // 拦截器高频调用，orElse(null) 避免异常开销并便于调用方判空
        return repository.findByUsername(username).orElse(null);
    }

    /**
     * 根据 ID 获取用户实体
     */
    public User getById(Long id) {
        // orElse(null) 用于"存在则填充，不存在则跳过"的关联查询场景
        return repository.findById(id).orElse(null);
    }

    /**
     * 修改用户密码
     *
     * @param userId 用户 ID
     * @param req    修改密码请求
     * @return true 修改成功，false 旧密码错误或用户不存在
     * @throws IllegalArgumentException 新密码不合规
     */
    public boolean changePassword(Long userId, ChangePasswordRequest req) {
        // 双重校验：Controller 的 @Valid 可能被绕过（如内部直接调用），Service 层兜底
        // 抛 IllegalArgumentException：参数层面不合规，与下方"业务状态不存在"语义区分
        if (req.newPassword() == null || req.newPassword().length() < 6) {
            throw new IllegalArgumentException("新密码至少 6 位");
        }
        // 防止用户"改了个一样的密码"，避免无效写入
        if (req.newPassword().equals(req.oldPassword())) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }
        User user = repository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        // 旧密码校验：必须验证身份，否则 token 泄露即可改密
        // matches 会从存储的 BCrypt 密文中提取 salt 重算哈希后比对，无需额外 salt 字段
        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            return false;
        }
        // 新密码重新加密存储，BCrypt 每次加密生成不同 salt
        user.setPassword(passwordEncoder.encode(req.newPassword()));
        repository.save(user);
        return true;
    }

    /**
     * 修改用户启用状态（后台管理）
     *
     * @param userId  用户 ID
     * @param enabled 是否启用
     * @return true 操作成功，false 用户不存在
     */
    public boolean setEnabled(Long userId, boolean enabled) {
        User user = repository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        // 仅更新启用标志位，JPA 脏检查会自动 flush 到 DB
        // 即便显式 save，JPA 也会基于 @Id 比对生成 UPDATE，无性能损耗
        user.setEnabled(enabled);
        repository.save(user);
        return true;
    }

    /**
     * 查询全部用户列表（后台管理用）
     */
    public java.util.List<UserResponse> listAll() {
        // 全量加载仅适合后台小数据量场景；用 stream 转 DTO 避免实体直接序列化泄露密码字段
        // UserResponse.from 是显式映射，相比 Jackson 注解忽略字段更可控、可审计
        return repository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    /**
     * 统计注册用户数（仪表板用）
     */
    public long count() {
        // 走 DB count 聚合，避免全表加载到内存再统计
        return repository.count();
    }
}
