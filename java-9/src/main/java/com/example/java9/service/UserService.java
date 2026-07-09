package com.example.java9.service;  // 声明服务层包路径

import com.example.java9.model.Transaction;  // 导入交易流水实体
import com.example.java9.model.TransactionType;  // 导入交易类型枚举
import com.example.java9.model.User;  // 导入用户实体
import com.example.java9.model.UserStatus;  // 导入用户状态枚举（NORMAL/FROZEN）
import com.example.java9.repository.UserRepository;  // 导入用户 Repository
import org.springframework.stereotype.Service;  // 导入 Spring Service 注解
import org.springframework.transaction.annotation.Transactional;  // 导入事务注解

import java.math.BigDecimal;  // 导入高精度十进制类，金额计算必须用 BigDecimal 避免 double 精度丢失
import java.util.List;  // 导入集合 List

/**
 * C端用户服务
 * <p>
 * 负责用户资产管理，包括实名认证、充值、提现、流水查询及账户状态管理。
 * 资金变动均通过 {@link TransactionService} 记录流水，提现操作触发风控检查。
 * </p>
 */
@Service  // 标记为 Spring Service Bean
public class UserService {

    private final UserRepository userRepository;  // 用户 Repository，final 保证不可变
    private final TransactionService transactionService;  // 交易流水服务，final 保证不可变
    private final RiskService riskService;  // 风控服务，final 保证不可变

    // 构造器注入：保证依赖不可变、显式暴露依赖、便于单元测试 mock，Spring 启动时即可发现循环依赖
    public UserService(UserRepository userRepository, TransactionService transactionService, RiskService riskService) {
        this.userRepository = userRepository;  // 注入用户 Repository
        this.transactionService = transactionService;  // 注入交易流水服务
        this.riskService = riskService;  // 注入风控服务
    }

    /**
     * 实名认证
     * <p>
     * 校验身份证号唯一性后写入实名信息，重复身份证号直接拒绝。
     * </p>
     *
     * @param userId   用户 ID
     * @param realName 真实姓名
     * @param idCard   身份证号
     * @return 已实名的用户实体
     * @throws IllegalArgumentException 用户不存在或身份证号已被使用
     */
    @Transactional  // 声明事务边界：身份证唯一性校验与实名信息写入原子化，避免并发认证产生重复绑定
    public User verify(Long userId, String realName, String idCard) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));  // 查询用户，不存在则抛异常
        // 身份证号全局唯一，已存在则拒绝认证
        if (userRepository.findByIdCard(idCard).isPresent()) {  // 查询身份证号是否已被其他用户绑定
            throw new IllegalArgumentException("身份证号已存在");  // 重复绑定快速失败
        }
        user.setRealName(realName);  // 写入真实姓名
        user.setIdCard(idCard);  // 写入身份证号
        user.setVerified(true);  // 标记为已实名，后续投资/提现等操作依赖此标志
        return userRepository.save(user);  // 持久化并返回
    }

    /**
     * 充值
     * <p>
     * 增加用户余额并记录充值流水（正数入账）。
     * </p>
     *
     * @param userId 用户 ID
     * @param amount 充值金额（正数）
     * @param remark 备注
     * @return 更新后的用户实体
     * @throws IllegalArgumentException 用户不存在
     */
    @Transactional  // 声明事务边界：余额更新与流水记录在同一事务，要么同时成功要么同时回滚，保证资金与流水一致
    public User recharge(Long userId, BigDecimal amount, String remark) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));  // 查询用户
        user.setBalance(user.getBalance().add(amount));  // 余额增加，使用 BigDecimal.add 避免 double 浮点精度丢失
        User saved = userRepository.save(user);  // 持久化更新后的余额
        // 交易后余额需传入流水，保证审计链路完整
        transactionService.recordTransaction(userId, "USER", TransactionType.RECHARGE,
                amount, saved.getBalance(), null, remark);  // 记录充值流水，金额为正表示入账
        return saved;  // 返回更新后的用户
    }

    /**
     * 提现
     * <p>
     * 校验余额充足后扣减余额，记录提现流水（负数出账），
     * 并在末尾触发风控检查（大额提现可能生成风控记录或自动冻结）。
     * </p>
     *
     * @param userId 用户 ID
     * @param amount 提现金额（正数）
     * @param remark 备注
     * @return 更新后的用户实体
     * @throws IllegalArgumentException 用户不存在或余额不足
     */
    @Transactional  // 声明事务边界：余额扣减、流水记录、风控检查在同一事务，任一失败则整体回滚，保证资金安全
    public User withdraw(Long userId, BigDecimal amount, String remark) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));  // 查询用户
        // 余额校验：不足则拒绝提现
        // 使用 compareTo 而非 equals：BigDecimal equals 比较 scale，compareTo 只比较数值大小更可靠
        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("余额不足");  // 余额不足快速失败
        }
        user.setBalance(user.getBalance().subtract(amount));  // 余额扣减，使用 BigDecimal.subtract 避免浮点精度丢失
        User saved = userRepository.save(user);  // 持久化更新后的余额
        // 提现为出账，流水金额取负
        transactionService.recordTransaction(userId, "USER", TransactionType.WITHDRAW,
                amount.negate(), saved.getBalance(), null, remark);  // negate 将正数转为负数，表示出账
        // 提现风控检查：大额或未实名会生成风控记录
        riskService.checkTransactionRisk(userId, amount, null);  // 触发风控规则引擎
        return saved;  // 返回更新后的用户
    }

    /**
     * 查询用户流水
     *
     * @param userId 用户 ID
     * @return 该用户的交易流水列表（按时间倒序）
     */
    public List<Transaction> getUserTransactions(Long userId) {
        return transactionService.findByAccountIdAndType(userId, "USER");  // 按账户 ID 与类型 USER 查询流水
    }

    /**
     * 查询所有用户（管理后台用）
     *
     * @return 全部用户列表
     */
    public List<User> findAll() {
        return userRepository.findAll();  // 查询全部用户
    }

    /**
     * 根据 ID 查询用户
     *
     * @param userId 用户 ID
     * @return 用户实体
     * @throws IllegalArgumentException 用户不存在
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));  // 查询用户，不存在则抛异常
    }

    /**
     * 根据状态查询用户
     *
     * @param status 用户状态
     * @return 符合状态的用户列表
     */
    public List<User> findByStatus(UserStatus status) {
        return userRepository.findByStatus(status);  // 按状态查询用户
    }

    /**
     * 冻结/解冻用户
     *
     * @param userId 用户 ID
     * @param status 目标状态（NORMAL/FROZEN）
     * @return 更新后的用户实体
     * @throws IllegalArgumentException 用户不存在
     */
    @Transactional  // 声明事务边界：用户状态更新原子化
    public User updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));  // 查询用户
        user.setStatus(status);  // 更新用户状态
        return userRepository.save(user);  // 持久化并返回
    }
}
