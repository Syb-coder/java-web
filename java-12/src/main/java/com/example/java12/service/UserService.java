package com.example.java12.service;  // 服务层包

import com.example.java12.dto.AssignModeratorRequest;  // 分配版主请求
import com.example.java12.dto.UpdateProfileRequest;  // 修改资料请求
import com.example.java12.dto.UserResponse;  // 用户响应 DTO
import com.example.java12.model.Moderator;  // 版主关联实体
import com.example.java12.model.Role;  // 角色枚举
import com.example.java12.model.User;  // 用户实体
import com.example.java12.model.UserStatus;  // 用户状态枚举
import com.example.java12.repository.ModeratorRepository;  // 版主数据访问层
import com.example.java12.repository.PlateRepository;  // 板块数据访问层
import com.example.java12.repository.UserRepository;  // 用户数据访问层
import org.springframework.data.domain.Page;  // 分页结果
import org.springframework.data.domain.Pageable;  // 分页参数
import org.springframework.stereotype.Service;  // Service 注解
import org.springframework.transaction.annotation.Transactional;  // 事务注解

import java.time.LocalDateTime;  // 时间类型
import java.util.List;  // 列表

/**
 * 用户服务
 * <p>
 * 负责个人资料管理、用户列表查询、用户封禁/解封、版主权限分配/回收。
 * </p>
 */
@Service
public class UserService {

    /** 昵称修改冷却期（30天） */
    private static final int NICKNAME_COOLDOWN_DAYS = 30;

    /** 用户数据访问层 */
    private final UserRepository userRepository;

    /** 版主关联数据访问层 */
    private final ModeratorRepository moderatorRepository;

    /** 板块数据访问层（分配版主时校验板块存在性） */
    private final PlateRepository plateRepository;

    /** 操作日志服务 */
    private final OperateLogService operateLogService;

    /** 站内消息服务 */
    private final MessageService messageService;

    /**
     * 构造器注入
     */
    public UserService(UserRepository userRepository, ModeratorRepository moderatorRepository,
                       PlateRepository plateRepository, OperateLogService operateLogService,
                       MessageService messageService) {
        this.userRepository = userRepository;
        this.moderatorRepository = moderatorRepository;
        this.plateRepository = plateRepository;
        this.operateLogService = operateLogService;
        this.messageService = messageService;
    }

    /**
     * 获取用户资料
     *
     * @param userId 用户 ID
     * @return 用户响应
     */
    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return new UserResponse(user);
    }

    /**
     * 修改个人资料
     * <p>
     * 昵称修改受30天冷却期限制，头像和签名可随时修改。
     * </p>
     *
     * @param userId 用户 ID
     * @param req    修改资料请求
     * @return 更新后的用户响应
     */
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        // 修改昵称（受30天冷却期限制）
        if (req.getNickname() != null && !req.getNickname().equals(user.getNickname())) {
            // 检查冷却期
            if (user.getNicknameUpdateTime() != null) {
                long daysSinceLastUpdate = java.time.Duration.between(
                        user.getNicknameUpdateTime(), LocalDateTime.now()).toDays();
                if (daysSinceLastUpdate < NICKNAME_COOLDOWN_DAYS) {
                    throw new RuntimeException("昵称30天内仅可修改一次，请" + (NICKNAME_COOLDOWN_DAYS - daysSinceLastUpdate) + "天后再试");
                }
            }
            // 检查唯一性
            if (userRepository.existsByNickname(req.getNickname())) {
                throw new RuntimeException("昵称已被使用");
            }
            user.setNickname(req.getNickname());
            user.setNicknameUpdateTime(LocalDateTime.now());
        }
        // 修改头像
        if (req.getAvatar() != null) {
            user.setAvatar(req.getAvatar());
        }
        // 修改签名
        if (req.getSignature() != null) {
            user.setSignature(req.getSignature());
        }
        userRepository.save(user);
        return new UserResponse(user);
    }

    /**
     * 分页查询全部用户（管理员）
     *
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * 封禁用户（管理员操作）
     * <p>
     * 设置用户状态为 BANNED，发送站内消息通知，记录操作日志。
     * 封禁后用户 Token 在下次请求时自动失效。
     * </p>
     *
     * @param userId 被封禁用户 ID
     * @param admin  操作管理员
     * @param ip     操作 IP
     */
    @Transactional
    public void banUser(Long userId, User admin, String ip) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("不能封禁管理员账号");
        }
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);
        // 发送站内消息通知
        messageService.sendMessage(userId, "您的账号已被封禁，如有疑问请联系管理员");
        // 记录操作日志
        operateLogService.log(admin.getId(), admin.getNickname(), "封禁用户",
                "用户:" + user.getNickname(), ip);
    }

    /**
     * 解封用户（管理员操作）
     *
     * @param userId 被解封用户 ID
     * @param admin  操作管理员
     * @param ip     操作 IP
     */
    @Transactional
    public void unbanUser(Long userId, User admin, String ip) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(UserStatus.NORMAL);
        userRepository.save(user);
        messageService.sendMessage(userId, "您的账号已解封，欢迎回来");
        operateLogService.log(admin.getId(), admin.getNickname(), "解封用户",
                "用户:" + user.getNickname(), ip);
    }

    /**
     * 分配版主（管理员操作）
     *
     * @param req   分配版主请求
     * @param admin 操作管理员
     * @param ip    操作 IP
     */
    @Transactional
    public void assignModerator(AssignModeratorRequest req, User admin, String ip) {
        // 校验用户存在
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        // 校验板块存在
        plateRepository.findById(req.getPlateId())
                .orElseThrow(() -> new RuntimeException("板块不存在"));
        // 检查是否已是该板块版主
        if (moderatorRepository.existsByUserIdAndPlateId(req.getUserId(), req.getPlateId())) {
            throw new RuntimeException("该用户已是此板块的版主");
        }
        moderatorRepository.save(new Moderator(req.getUserId(), req.getPlateId()));
        messageService.sendMessage(req.getUserId(), "您已被分配为板块版主，请尽责管理");
        operateLogService.log(admin.getId(), admin.getNickname(), "分配版主",
                "用户ID:" + req.getUserId() + ",板块ID:" + req.getPlateId(), ip);
    }

    /**
     * 回收版主权限（管理员操作）
     *
     * @param userId  版主用户 ID
     * @param plateId 板块 ID
     * @param admin   操作管理员
     * @param ip      操作 IP
     */
    @Transactional
    public void removeModerator(Long userId, Long plateId, User admin, String ip) {
        moderatorRepository.deleteByUserIdAndPlateId(userId, plateId);
        messageService.sendMessage(userId, "您的版主权限已被回收");
        operateLogService.log(admin.getId(), admin.getNickname(), "回收版主",
                "用户ID:" + userId + ",板块ID:" + plateId, ip);
    }

    /**
     * 查询指定板块的版主列表
     *
     * @param plateId 板块 ID
     * @return 版主关联列表
     */
    public List<Moderator> findModeratorsByPlateId(Long plateId) {
        return moderatorRepository.findByPlateId(plateId);
    }

    /**
     * 检查用户是否是指定板块的版主
     *
     * @param userId  用户 ID
     * @param plateId 板块 ID
     * @return true 表示是版主
     */
    public boolean isModerator(Long userId, Long plateId) {
        return moderatorRepository.existsByUserIdAndPlateId(userId, plateId);
    }
}
