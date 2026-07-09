// 声明包路径，存放业务服务层
package com.example.java4.service;

// 导入 DTO 类
import com.example.java4.dto.ChangePasswordRequest;
import com.example.java4.dto.LoginRequest;
import com.example.java4.dto.LoginResponse;
import com.example.java4.dto.RegisterRequest;

// 导入实体类
import com.example.java4.model.AdminUser;
import com.example.java4.model.Reader;
import com.example.java4.model.ReaderType;

// 导入 Repository
import com.example.java4.repository.AdminUserRepository;
import com.example.java4.repository.ReaderRepository;

// 导入 Spring 与安全工具
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证业务服务
 * <p>
 * 统一处理读者端与管理端的注册、登录、修改密码等认证逻辑。
 * 密码使用 BCrypt 加密存储与比对，避免明文落库。
 * </p>
 * <p>
 * 设计说明：
 * - Service 层不直接操作 HttpSession，仅返回业务结果，由 Controller 层管理会话；
 * - 读者与管理员分别存储在不同表，通过 loginType 字段区分登录类型；
 * - BCryptPasswordEncoder 是线程安全的无状态对象，可作为单例共享。
 * </p>
 */
@Service // 声明为 Spring 服务组件，由 IoC 容器管理为单例 Bean
public class AuthService {

    /** 管理员仓储 */
    private final AdminUserRepository adminUserRepository;

    /** 读者仓储 */
    private final ReaderRepository readerRepository;

    /** BCrypt 编码器（线程安全，无状态，可共享） */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 构造方法注入依赖
     *
     * @param adminUserRepository 管理员仓储
     * @param readerRepository    读者仓储
     */
    public AuthService(AdminUserRepository adminUserRepository, ReaderRepository readerRepository) {
        this.adminUserRepository = adminUserRepository;
        this.readerRepository = readerRepository;
    }

    /**
     * 编码明文密码（供 DataInitializer 初始化数据时调用）
     *
     * @param rawPassword 明文密码
     * @return BCrypt 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 统一登录入口：根据 loginType 分发到读者登录或管理员登录
     *
     * @param req 登录请求（含 username、password、loginType）
     * @return 登录响应（成功）或 null（失败）
     */
    public LoginResponse login(LoginRequest req) {
        // 根据登录类型分发，"admin" 走管理员登录，其余默认走读者登录
        if ("admin".equals(req.getLoginType())) {
            return adminLogin(req.getUsername(), req.getPassword());
        } else {
            return readerLogin(req.getUsername(), req.getPassword());
        }
    }

    /**
     * 读者登录校验
     *
     * @param readerNo 学号/工号
     * @param password 明文密码
     * @return 登录响应（成功）或 null（失败）
     */
    private LoginResponse readerLogin(String readerNo, String password) {
        // 按学号/工号查询读者，不存在则返回 null
        Reader reader = readerRepository.findByReaderNo(readerNo).orElse(null);
        // 用户不存在或密码不匹配，统一返回 null 避免泄露用户是否存在
        if (reader == null || !passwordEncoder.matches(password, reader.getPassword())) {
            return null;
        }
        // 更新最近登录时间，用于安全审计
        reader.setLastLoginAt(LocalDateTime.now());
        readerRepository.save(reader);
        // 组装登录响应，角色为 reader
        return new LoginResponse(
                reader.getId(),
                reader.getReaderNo(),
                reader.getName(),
                "reader",
                reader.getType().name(),
                reader.getDepartment()
        );
    }

    /**
     * 管理员登录校验
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 登录响应（成功）或 null（失败）
     */
    private LoginResponse adminLogin(String username, String password) {
        // 按用户名查询管理员
        AdminUser admin = adminUserRepository.findByUsername(username).orElse(null);
        // 用户不存在或密码不匹配
        if (admin == null || !passwordEncoder.matches(password, admin.getPassword())) {
            return null;
        }
        // 更新最近登录时间
        admin.setLastLoginAt(LocalDateTime.now());
        adminUserRepository.save(admin);
        // 组装登录响应，角色为 admin
        return new LoginResponse(
                admin.getId(),
                admin.getUsername(),
                admin.getRealName(),
                "admin",
                null,
                null
        );
    }

    /**
     * 读者注册
     *
     * @param req 注册请求
     * @return 新创建的读者实体
     * @throws IllegalArgumentException 学号/工号已注册或参数不合法
     */
    public Reader register(RegisterRequest req) {
        // 检查学号/工号是否已注册
        if (readerRepository.existsByReaderNo(req.getReaderNo())) {
            throw new IllegalArgumentException("该学号/工号已注册");
        }
        // 解析读者类型
        ReaderType type = ReaderType.valueOf(req.getReaderType());
        // 创建读者实体，密码 BCrypt 加密后存储
        Reader reader = new Reader(
                req.getReaderNo(),
                passwordEncoder.encode(req.getPassword()),
                req.getName(),
                type,
                req.getDepartment(),
                req.getPhone()
        );
        return readerRepository.save(reader);
    }

    /**
     * 读者修改密码
     *
     * @param readerId 读者 ID
     * @param req      修改密码请求
     * @return true 修改成功，false 旧密码错误
     * @throws IllegalArgumentException 新密码不合规
     */
    public boolean changeReaderPassword(Long readerId, ChangePasswordRequest req) {
        // 校验新密码与旧密码不同
        if (req.getNewPassword().equals(req.getOldPassword())) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }
        // 加载读者实体
        Reader reader = readerRepository.findById(readerId).orElse(null);
        if (reader == null) {
            return false;
        }
        // 校验旧密码
        if (!passwordEncoder.matches(req.getOldPassword(), reader.getPassword())) {
            return false;
        }
        // 设置新密码并持久化
        reader.setPassword(passwordEncoder.encode(req.getNewPassword()));
        readerRepository.save(reader);
        return true;
    }

    /**
     * 管理员修改密码
     *
     * @param adminId 管理员 ID
     * @param req     修改密码请求
     * @return true 修改成功，false 旧密码错误
     * @throws IllegalArgumentException 新密码不合规
     */
    public boolean changeAdminPassword(Long adminId, ChangePasswordRequest req) {
        // 校验新密码与旧密码不同
        if (req.getNewPassword().equals(req.getOldPassword())) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }
        // 加载管理员实体
        AdminUser admin = adminUserRepository.findById(adminId).orElse(null);
        if (admin == null) {
            return false;
        }
        // 校验旧密码
        if (!passwordEncoder.matches(req.getOldPassword(), admin.getPassword())) {
            return false;
        }
        // 设置新密码并持久化
        admin.setPassword(passwordEncoder.encode(req.getNewPassword()));
        adminUserRepository.save(admin);
        return true;
    }

    /**
     * 根据读者 ID 获取读者实体
     *
     * @param id 读者 ID
     * @return 读者实体（可能为空）
     */
    public Reader getReaderById(Long id) {
        return readerRepository.findById(id).orElse(null);
    }

    /**
     * 根据管理员 ID 获取管理员实体
     *
     * @param id 管理员 ID
     * @return 管理员实体（可能为空）
     */
    public AdminUser getAdminById(Long id) {
        return adminUserRepository.findById(id).orElse(null);
    }
}
