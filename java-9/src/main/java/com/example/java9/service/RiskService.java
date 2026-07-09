package com.example.java9.service;  // 声明服务层包路径

import com.example.java9.model.RiskLevel;  // 导入风险等级枚举（LOW/MEDIUM/HIGH）
import com.example.java9.model.RiskRecord;  // 导入风控记录实体
import com.example.java9.model.RiskStatus;  // 导入风控状态枚举（PENDING/HANDLED/IGNORED）
import com.example.java9.model.User;  // 导入用户实体
import com.example.java9.model.UserStatus;  // 导入用户状态枚举（NORMAL/FROZEN）
import com.example.java9.repository.RiskRecordRepository;  // 导入风控记录 Repository
import com.example.java9.repository.UserRepository;  // 导入用户 Repository
import org.springframework.stereotype.Service;  // 导入 Spring Service 注解
import org.springframework.transaction.annotation.Transactional;  // 导入事务注解

import java.math.BigDecimal;  // 导入高精度十进制类，金额比较必须用 BigDecimal 避免 double 精度问题
import java.time.LocalDateTime;  // 导入时间类
import java.util.List;  // 导入集合 List

/**
 * 风控服务（规则引擎模拟）
 * <p>
 * 基于阈值规则自动识别风险交易，生成风控记录。
 * 规则示例：
 * <ul>
 *   <li>单笔交易 >= 50000 元 → MEDIUM 风险（LARGE_AMOUNT）</li>
 *   <li>单笔交易 >= 200000 元 → HIGH 风险（LARGE_AMOUNT），自动冻结用户</li>
 *   <li>用户未实名但发起交易 → HIGH 风险（UNVERIFIED）</li>
 * </ul>
 * 风控专员可在后台处理或忽略风险记录。
 * </p>
 */
@Service  // 标记为 Spring Service Bean
public class RiskService {

    /** 单笔大额阈值（5万） */
    // 使用 BigDecimal 而非 double：阈值是金额，double 存在二进制浮点精度问题（如 0.1 无法精确表示），BigDecimal 可精确表示十进制
    private static final BigDecimal LARGE_AMOUNT_THRESHOLD = new BigDecimal("50000");

    /** 单笔超大额阈值（20万），触发自动冻结 */
    // 超大额阈值独立配置：高于此值直接冻结账户，防止资金外逃，阈值设计参考反洗钱大额交易上报标准
    private static final BigDecimal SUPER_LARGE_AMOUNT_THRESHOLD = new BigDecimal("200000");

    private final RiskRecordRepository riskRecordRepository;  // 风控记录 Repository，final 保证不可变
    private final UserRepository userRepository;  // 用户 Repository，final 保证不可变

    // 构造器注入：保证依赖不可变、显式声明依赖、便于单元测试，且 Spring 启动时即可发现循环依赖
    public RiskService(RiskRecordRepository riskRecordRepository, UserRepository userRepository) {
        this.riskRecordRepository = riskRecordRepository;  // 注入风控记录 Repository
        this.userRepository = userRepository;  // 注入用户 Repository
    }

    /**
     * 检查交易风险（规则引擎入口）
     * <p>
     * 在支付/投资/提现场景调用，根据金额与用户状态判断是否生成风控记录。
     * </p>
     *
     * @param userId    用户 ID
     * @param amount    交易金额
     * @param orderNo   关联订单号
     */
    @Transactional  // 声明事务边界：风控记录写入与用户冻结操作在同一事务内，要么同时成功要么同时回滚，避免出现"记录生成但未冻结"的不一致
    public void checkTransactionRisk(Long userId, BigDecimal amount, String orderNo) {
        // 规则1：未实名用户发起交易 → HIGH 风险
        User user = userRepository.findById(userId).orElse(null);  // 查询用户，不存在则返回 null（不抛异常，避免阻断主流程）
        if (user != null && !Boolean.TRUE.equals(user.getVerified())) {  // 用户存在且未实名（用 Boolean.TRUE.equals 避免 NPE，因 verified 可能为 null）
            createRiskRecord("USER", userId, "UNVERIFIED", RiskLevel.HIGH,
                    "未实名用户发起交易，金额：" + amount + "元", orderNo);  // 生成 HIGH 风险记录，未实名即高风险因无法追溯资金来源
            return;  // 命中高风险规则后直接返回，不再继续检查金额规则
        }

        // 规则2：单笔 >= 20万 → HIGH 风险，自动冻结用户
        // 使用 compareTo 而非 equals：BigDecimal equals 会比较 scale，"200000" 与 "200000.00" 不相等，compareTo 只比较数值大小
        if (amount.compareTo(SUPER_LARGE_AMOUNT_THRESHOLD) >= 0) {
            createRiskRecord("USER", userId, "LARGE_AMOUNT", RiskLevel.HIGH,
                    "单笔交易金额超20万：" + amount + "元，已自动冻结账户", orderNo);  // 生成 HIGH 风险记录
            // 自动冻结用户账户，防止资金外逃
            user.setStatus(UserStatus.FROZEN);  // 置为冻结状态，冻结后用户无法再进行资金操作
            userRepository.save(user);  // 持久化冻结状态
            return;  // 已冻结，无需再检查中等风险规则
        }

        // 规则3：单笔 >= 5万 → MEDIUM 风险
        // 5万阈值设计：低于反洗钱上报线但属大额，需人工复核确认交易合理性
        if (amount.compareTo(LARGE_AMOUNT_THRESHOLD) >= 0) {
            createRiskRecord("USER", userId, "LARGE_AMOUNT", RiskLevel.MEDIUM,
                    "单笔交易金额超5万：" + amount + "元，需人工复核", orderNo);  // 生成 MEDIUM 风险记录，等待风控专员复核
        }
    }

    /**
     * 创建风控记录
     *
     * @param targetType     目标类型（USER/MERCHANT/ORDER）
     * @param targetId       目标 ID
     * @param riskType       风险类型标识
     * @param riskLevel      风险等级
     * @param description    风险描述
     * @param relatedOrderNo 关联订单号
     */
    @Transactional  // 声明事务边界：风控记录写入原子化，失败则回滚
    public void createRiskRecord(String targetType, Long targetId, String riskType,
                                 RiskLevel riskLevel, String description, String relatedOrderNo) {
        RiskRecord record = new RiskRecord(targetType, targetId, riskType, riskLevel, description, relatedOrderNo);  // 构造风控记录实体
        riskRecordRepository.save(record);  // 持久化风控记录
    }

    /**
     * 处理风控记录（风控专员操作）
     *
     * @param id          风控记录 ID
     * @param action      处理动作：HANDLE（已处理）/ IGNORE（忽略误报）
     * @param handleRemark 处理意见
     * @param handler     处理人（管理员用户名）
     * @return 更新后的风控记录
     */
    @Transactional  // 声明事务边界：风控记录状态更新原子化
    public RiskRecord handleRisk(Long id, String action, String handleRemark, String handler) {
        RiskRecord record = riskRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("风控记录不存在"));  // 查询风控记录，不存在则抛异常
        record.setHandledBy(handler);  // 记录处理人
        record.setHandleRemark(handleRemark);  // 记录处理意见
        record.setHandledAt(LocalDateTime.now());  // 记录处理时间
        if ("HANDLE".equalsIgnoreCase(action)) {  // 使用 equalsIgnoreCase 容错大小写
            record.setStatus(RiskStatus.HANDLED);  // 标记为已处理
        } else if ("IGNORE".equalsIgnoreCase(action)) {
            record.setStatus(RiskStatus.IGNORED);  // 标记为已忽略（误报）
        } else {
            throw new IllegalArgumentException("无效的处理动作：" + action);  // 非法动作快速失败
        }
        return riskRecordRepository.save(record);  // 持久化更新后的风控记录
    }

    /**
     * 查询所有风控记录
     */
    public List<RiskRecord> findAll() {
        return riskRecordRepository.findAll();  // 查询全部风控记录
    }

    /**
     * 查询待处理风控记录
     */
    public List<RiskRecord> findPending() {
        return riskRecordRepository.findByStatus(RiskStatus.PENDING);  // 按状态 PENDING 查询待处理记录
    }

    /**
     * 根据状态查询
     */
    public List<RiskRecord> findByStatus(RiskStatus status) {
        return riskRecordRepository.findByStatus(status);  // 按指定状态查询风控记录
    }
}
