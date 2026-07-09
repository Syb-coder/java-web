package com.example.java9.service;  // 声明服务层包路径

import com.example.java9.dto.LoginRequest;  // 导入登录请求 DTO
import com.example.java9.dto.LoginResponse;  // 导入登录响应 DTO
import com.example.java9.dto.MerchantRegisterRequest;  // 导入商户注册请求 DTO
import com.example.java9.dto.RegisterRequest;  // 导入用户注册请求 DTO
import com.example.java9.model.AdminUser;  // 导入管理员实体
import com.example.java9.model.AdminRole;  // 导入管理员角色枚举
import com.example.java9.model.Merchant;  // 导入商户实体
import com.example.java9.model.User;  // 导入用户实体
import com.example.java9.repository.AdminUserRepository;  // 导入管理员 Repository
import com.example.java9.repository.MerchantRepository;  // 导入商户 Repository
import com.example.java9.repository.UserRepository;  // 导入用户 Repository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;  // 导入 BCrypt 密码编码器
import org.springframework.stereotype.Service;  // 导入 Spring Service 注解
import org.springframework.transaction.annotation.Transactional;  // 导入事务注解

/**
 * 认证服务
 * <p>
 * 统一处理管理员、C端用户、商户三类账户的注册、登录、密码修改。
 * 密码使用 BCrypt 加密存储与校验。
 * </p>
 */
@Service  // 标记为 Spring Service Bean
public class AuthService {

    private final AdminUserRepository adminUserRepository;  // 管理员 Repository，final 保证不可变
    private final UserRepository userRepository;  // 用户 Repository，final 保证不可变
    private final MerchantRepository merchantRepository;  // 商户 Repository，final 保证不可变
    // BCryptPasswordEncoder：每次编码使用随机 salt，即使相同密码密文也不同，可有效防御彩虹表攻击；密文内嵌 salt 与 cost factor，校验时自动提取
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 构造器注入：保证依赖不可变、显式暴露依赖、便于单元测试，避免字段注入的循环依赖隐患
    public AuthService(AdminUserRepository adminUserRepository,
                       UserRepository userRepository,
                       MerchantRepository merchantRepository) {
        this.adminUserRepository = adminUserRepository;  // 注入管理员 Repository
        this.userRepository = userRepository;  // 注入用户 Repository
        this.merchantRepository = merchantRepository;  // 注入商户 Repository
    }

    // ==================== C端用户 ====================

    /**
     * C端用户注册
     *
     * @param req 注册信息
     * @return 新建用户实体
     * @throws IllegalArgumentException 用户名已存在
     */
    @Transactional  // 声明事务边界：用户名唯一性校验与用户写入在同一事务，避免并发注册产生重复用户名
    public User registerUser(RegisterRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {  // 查询用户名是否已存在
            throw new IllegalArgumentException("用户名已存在");  // 重复则快速失败
        }
        User user = new User(
                req.getUsername(),  // 用户名
                passwordEncoder.encode(req.getPassword()),  // 密码 BCrypt 加密存储，encode 内部自动生成随机 salt
                req.getPhone()  // 手机号
        );
        return userRepository.save(user);  // 持久化新用户并返回带 ID 的实体
    }

    /**
     * C端用户登录校验
     *
     * @param req 登录请求
     * @return 用户实体（校验通过）
     * @throws IllegalArgumentException 用户名或密码错误
     */
    public User loginUserService(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));  // 用户不存在，统一返回"用户名或密码错误"避免泄露用户是否存在
        // matches 安全原理：BCrypt 密文内嵌 salt 与 cost factor，matches 会从密文中提取 salt 对明文重新编码后比对，无需单独存储 salt
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");  // 密码不匹配，错误信息与用户不存在时一致以防枚举攻击
        }
        if (user.getStatus() == com.example.java9.model.UserStatus.FROZEN) {
            throw new IllegalStateException("账户已被冻结，请联系客服");  // 冻结账户拒绝登录
        }
        return user;  // 校验通过返回用户实体
    }

    /**
     * 修改C端用户密码
     *
     * @param userId      用户 ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Transactional  // 声明事务边界：旧密码校验与新密码写入原子化，避免校验通过但写入失败导致密码状态不一致
    public void changeUserPassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));  // 查询用户，不存在则抛异常
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码不正确");  // 旧密码不匹配则拒绝修改
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");  // 新旧密码相同则拒绝，强制提升密码强度
        }
        user.setPassword(passwordEncoder.encode(newPassword));  // 新密码 BCrypt 加密后写入
        userRepository.save(user);  // 持久化密码变更
    }

    // ==================== 管理员 ====================

    /**
     * 管理员登录校验
     *
     * @param req 登录请求
     * @return 管理员实体
     */
    public AdminUser loginAdmin(LoginRequest req) {
        AdminUser admin = adminUserRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));  // 管理员不存在，统一错误信息防枚举
        if (!passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");  // 密码不匹配
        }
        return admin;  // 校验通过返回管理员实体
    }

    /**
     * 修改管理员密码
     */
    @Transactional  // 声明事务边界：旧密码校验与新密码写入原子化
    public void changeAdminPassword(Long adminId, String oldPassword, String newPassword) {
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("管理员不存在"));  // 查询管理员
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new IllegalArgumentException("旧密码不正确");  // 旧密码校验
        }
        admin.setPassword(passwordEncoder.encode(newPassword));  // 新密码 BCrypt 加密
        adminUserRepository.save(admin);  // 持久化
    }

    // ==================== 商户 ====================

    /**
     * 商户入驻注册
     *
     * @param req 入驻信息
     * @return 新建商户实体（状态为 PENDING 待审核）
     */
    @Transactional  // 声明事务边界：账号唯一性校验与商户写入原子化
    public Merchant registerMerchant(MerchantRegisterRequest req) {
        if (merchantRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("商户账号已存在");  // 账号重复快速失败
        }
        Merchant merchant = new Merchant(
                req.getUsername(),  // 商户登录账号
                passwordEncoder.encode(req.getPassword()),  // 密码 BCrypt 加密
                req.getMerchantName(),  // 商户名称
                req.getContactPhone(),  // 联系电话
                req.getLicenseNo()  // 营业执照号
        );
        merchant.setDescription(req.getDescription());  // 设置商户描述
        return merchantRepository.save(merchant);  // 持久化，初始状态为 PENDING 待审核
    }

    /**
     * 商户登录校验
     *
     * @param req 登录请求
     * @return 商户实体
     */
    public Merchant loginMerchant(LoginRequest req) {
        Merchant merchant = merchantRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));  // 商户不存在
        if (!passwordEncoder.matches(req.getPassword(), merchant.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");  // 密码不匹配
        }
        if (merchant.getStatus() != com.example.java9.model.MerchantStatus.APPROVED) {
            throw new IllegalStateException("商户账号尚未通过审核");  // 未审核通过的商户拒绝登录
        }
        return merchant;  // 校验通过返回商户实体
    }

    /**
     * 修改商户密码
     */
    @Transactional  // 声明事务边界：旧密码校验与新密码写入原子化
    public void changeMerchantPassword(Long merchantId, String oldPassword, String newPassword) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在"));  // 查询商户
        if (!passwordEncoder.matches(oldPassword, merchant.getPassword())) {
            throw new IllegalArgumentException("旧密码不正确");  // 旧密码校验
        }
        merchant.setPassword(passwordEncoder.encode(newPassword));  // 新密码 BCrypt 加密
        merchantRepository.save(merchant);  // 持久化
    }

    /**
     * 根据角色构建登录响应
     *
     * @param admin 管理员实体
     * @return 登录响应
     */
    public LoginResponse buildAdminLoginResponse(AdminUser admin) {
        String displayName = admin.getRealName() != null ? admin.getRealName() : admin.getUsername();  // 优先使用真实姓名，无则回退到用户名
        return new LoginResponse(admin.getId(), admin.getUsername(), displayName, admin.getRole().name());  // 构建登录响应，角色取枚举 name
    }

    /**
     * 构建用户登录响应
     */
    public LoginResponse buildUserLoginResponse(User user) {
        String displayName = user.getRealName() != null ? user.getRealName() : user.getUsername();  // 优先真实姓名
        return new LoginResponse(user.getId(), user.getUsername(), displayName, "USER");  // 角色固定为 USER
    }

    /**
     * 构建商户登录响应
     */
    public LoginResponse buildMerchantLoginResponse(Merchant merchant) {
        return new LoginResponse(merchant.getId(), merchant.getUsername(), merchant.getMerchantName(), "MERCHANT");  // 展示名使用商户名称，角色 MERCHANT
    }
}
