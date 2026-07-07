// 声明包路径
package com.example.java2.service;

// 导入 DTO 与实体类
import com.example.java2.dto.ChangePasswordRequest;
import com.example.java2.dto.LoginRequest;
import com.example.java2.dto.LoginResponse;
import com.example.java2.model.AdminUser;
import com.example.java2.repository.AdminUserRepository;

// 导入 BCrypt 编码器
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 管理员认证业务服务
 * <p>
 * 负责后台管理员登录校验与密码修改。密码使用 BCrypt 加密存储与比对。
 * </p>
 */
@Service
public class AuthService {

    // final 修饰：保证依赖在构造完成后不可变，避免被误改写；同时满足 JMM 对安全发布的要求，跨线程可见
    private final AdminUserRepository repository;
    /**
     * BCrypt 编码器（线程安全，可作为单例共享）
     * 为何单例：BCryptPasswordEncoder 内部无可变状态，重复 new 会浪费对象创建开销
     * 为何 final：与 repository 同理，构造后不再变更，确保多线程环境下的安全发布
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 构造方法注入：Spring 4.3+ 单构造器可省略 @Autowired 自动注入
     * 为何不用字段注入（@Autowired 在字段上）：
     *   1) 字段注入会破坏 final 约束，依赖无法声明为不可变；
     *   2) 字段注入使组件无法脱离 Spring 容器测试（无法手动 new 并 set 依赖）；
     *   3) 构造方法注入能在启动期暴露循环依赖，比字段注入更早失败、更易定位。
     */
    public AuthService(AdminUserRepository repository) {
        this.repository = repository;
    }

    /**
     * 编码明文密码（用于初始化管理员）
     *
     * @param rawPassword 明文密码
     * @return BCrypt 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        // BCrypt 内部自动生成随机 salt 并嵌入密文，无需单独维护 salt 字段
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 管理员登录校验
     *
     * @param req 登录请求
     * @return 登录响应（成功）或 null（失败）
     */
    public LoginResponse login(LoginRequest req) {
        AdminUser user = repository.findByUsername(req.username()).orElse(null);
        // 合并"用户不存在"与"密码错误"两种情况统一返回 null，避免暴露账号是否存在，防御用户枚举攻击
        // matches 内部机制：BCrypt 密文自包含 salt（前 22 位为盐），matches 会自动从中解析盐值并重新哈希明文比对
        // 因此无需在 DB 单独存 salt 字段，也无法通过相同明文反推密文一致性
        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            return null;
        }
        // 刷新最近登录时间，用于后台审计与登录态展示
        user.setLastLoginAt(LocalDateTime.now());
        repository.save(user);
        return LoginResponse.from(user);
    }

    /**
     * 根据用户名获取管理员（用于拦截器校验登录态）
     *
     * @param username 用户名
     * @return 管理员（可选）
     */
    public AdminUser getByUsername(String username) {
        // 拦截器每请求都会调用，orElse(null) 避免抛异常带来的栈开销与统一异常处理干扰
        return repository.findByUsername(username).orElse(null);
    }

    /**
     * 修改密码
     *
     * <p>校验旧密码后更新为新密码。新密码通过 BCrypt 重新加密存储。
     * 业务规则：新密码不少于 6 位，且不得与旧密码相同。</p>
     *
     * @param userId 管理员 ID
     * @param req    修改密码请求
     * @return true 修改成功，false 旧密码错误或用户不存在
     * @throws IllegalArgumentException 新密码不合规（为空、长度不足、与旧密码相同）
     */
    public boolean changePassword(Long userId, ChangePasswordRequest req) {
        // 校验新密码长度（@Valid 已在 Controller 层校验，此处双重保险防御直接调用）
        // 抛 IllegalArgumentException：参数不合规属于"调用方传错"的语义，与下方"业务状态"区分
        if (req.newPassword() == null || req.newPassword().length() < 6) {
            throw new IllegalArgumentException("新密码至少 6 位");
        }
        // 校验新旧密码不同
        if (req.newPassword().equals(req.oldPassword())) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }
        AdminUser user = repository.findById(userId).orElse(null);
        if (user == null) {
            // 返回 false 而非抛异常，调用方据返回值区分"用户不存在"与"旧密码错误"
            // 这是业务可恢复状态而非参数错误，故用返回值而非异常
            return false;
        }
        // 校验旧密码：matches 自动从已存的 BCrypt 密文中提取 salt 重算哈希再比对
        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            return false;
        }
        // 重新 BCrypt 编码：每次加密生成新 salt，确保新旧密文不同
        // 即使新密码与历史密码相同，密文也不同，防止密文比对泄露密码历史
        user.setPassword(passwordEncoder.encode(req.newPassword()));
        repository.save(user);
        return true;
    }
}
