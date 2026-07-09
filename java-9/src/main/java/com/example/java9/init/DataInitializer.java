package com.example.java9.init; // 声明 Init 层包路径，存放启动时的初始化逻辑

import com.example.java9.model.Activity; // 引入营销活动实体
import com.example.java9.model.ActivityType; // 引入活动类型枚举（COUPON/INTEREST_RATE 等）
import com.example.java9.model.AdminRole; // 引入管理员角色枚举，用于初始化运营/风控账号
import com.example.java9.model.AdminUser; // 引入管理员实体
import com.example.java9.model.FinancialProduct; // 引入理财产品实体
import com.example.java9.model.Merchant; // 引入商户实体
import com.example.java9.model.ProductType; // 引入产品类型枚举（DEPOSIT/FUND/INSURANCE）
import com.example.java9.model.SystemConfig; // 引入系统配置实体
import com.example.java9.model.User; // 引入 C 端用户实体
import com.example.java9.model.UserStatus; // 引入用户状态枚举，初始化为 NORMAL
import com.example.java9.repository.ActivityRepository; // 引入活动仓储
import com.example.java9.repository.AdminUserRepository; // 引入管理员仓储
import com.example.java9.repository.FinancialProductRepository; // 引入产品仓储
import com.example.java9.repository.MerchantRepository; // 引入商户仓储
import com.example.java9.repository.SystemConfigRepository; // 引入系统配置仓储
import com.example.java9.repository.UserRepository; // 引入用户仓储
import org.springframework.boot.CommandLineRunner; // 引入启动回调接口，容器就绪后执行 run 方法
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 引入 BCrypt 密码编码器
import org.springframework.stereotype.Component; // 引入 @Component，让 Spring 扫描注册

import java.math.BigDecimal; // 引入高精度十进制，用于金额/利率
import java.time.LocalDateTime; // 引入日期时间，用于活动起止时间

/**
 * 种子数据初始化器
 * <p>
 * 应用启动时自动初始化演示数据，便于直接体验平台功能。
 * 仅在对应表为空时执行，避免重复插入。
 * </p>
 * <p>
 * 初始化内容：
 * <ul>
 *   <li>2 个管理员（运营 admin/123456 + 风控 risk/123456）</li>
 *   <li>3 个 C 端用户（含已实名有余额的演示账户 user1/123456）</li>
 *   <li>2 个商户（一个已审核、一个待审核）</li>
 *   <li>4 个理财产品（定期/基金/保险各类）</li>
 *   <li>2 个营销活动</li>
 *   <li>3 个系统配置</li>
 * </ul>
 * </p>
 */
@Component // 注册为 Spring Bean，Spring Boot 启动完成后会自动调用其 run 方法
public class DataInitializer implements CommandLineRunner { // 实现该接口即成为启动回调

    private final AdminUserRepository adminUserRepository; // 注入管理员仓储
    private final UserRepository userRepository; // 注入用户仓储
    private final MerchantRepository merchantRepository; // 注入商户仓储
    private final FinancialProductRepository financialProductRepository; // 注入产品仓储
    private final ActivityRepository activityRepository; // 注入活动仓储
    private final SystemConfigRepository systemConfigRepository; // 注入系统配置仓储
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // 创建 BCrypt 编码器实例，用于加密明文密码

    public DataInitializer(AdminUserRepository adminUserRepository,
                           UserRepository userRepository,
                           MerchantRepository merchantRepository,
                           FinancialProductRepository financialProductRepository,
                           ActivityRepository activityRepository,
                           SystemConfigRepository systemConfigRepository) { // 构造器注入所有仓储依赖
        this.adminUserRepository = adminUserRepository; // 保存管理员仓储
        this.userRepository = userRepository; // 保存用户仓储
        this.merchantRepository = merchantRepository; // 保存商户仓储
        this.financialProductRepository = financialProductRepository; // 保存产品仓储
        this.activityRepository = activityRepository; // 保存活动仓储
        this.systemConfigRepository = systemConfigRepository; // 保存系统配置仓储
    }

    @Override // 重写 CommandLineRunner 的回调方法
    public void run(String... args) { // 应用启动后自动执行
        initAdmins(); // 初始化管理员
        initUsers(); // 初始化 C 端用户
        initMerchants(); // 初始化商户
        initProducts(); // 初始化理财产品
        initActivities(); // 初始化营销活动
        initSystemConfigs(); // 初始化系统配置
    }

    /**
     * 初始化管理员账号
     * <p>
     * - admin/123456（运营管理员）
     * - risk/123456（风控专员）
     * </p>
     */
    private void initAdmins() {
        if (adminUserRepository.count() > 0) { // 表中已有数据则跳过，保证幂等
            return; // 直接返回，不重复插入
        }
        adminUserRepository.save(new AdminUser("admin", passwordEncoder.encode("123456"), "系统运营", AdminRole.OPERATION)); // 创建运营管理员，密码经 BCrypt 加密
        adminUserRepository.save(new AdminUser("risk", passwordEncoder.encode("123456"), "风控专员", AdminRole.RISK)); // 创建风控专员
        System.out.println("[DataInitializer] 初始化 2 个管理员账号"); // 控制台打印日志，便于启动时确认
    }

    /**
     * 初始化 C 端用户
     * <p>
     * - user1/123456（已实名，余额 100000）
     * - user2/123456（已实名，余额 50000）
     * - user3/123456（未实名，余额 0）
     * </p>
     */
    private void initUsers() {
        if (userRepository.count() > 0) { // 用户表已有数据则跳过
            return; // 幂等返回
        }
        User user1 = new User("user1", passwordEncoder.encode("123456"), "13800000001"); // 创建用户1，含用户名/密码/手机号
        user1.setRealName("张三"); // 设置真实姓名
        user1.setIdCard("110101199001011234"); // 设置身份证号
        user1.setVerified(true); // 标记为已实名
        user1.setBalance(new BigDecimal("100000")); // 设置账户余额 10 万元
        user1.setStatus(UserStatus.NORMAL); // 状态设为正常
        userRepository.save(user1); // 持久化

        User user2 = new User("user2", passwordEncoder.encode("123456"), "13800000002"); // 创建用户2
        user2.setRealName("李四"); // 设置真实姓名
        user2.setIdCard("110101199002021234"); // 设置身份证号
        user2.setVerified(true); // 已实名
        user2.setBalance(new BigDecimal("50000")); // 余额 5 万
        user2.setStatus(UserStatus.NORMAL); // 正常状态
        userRepository.save(user2); // 持久化

        User user3 = new User("user3", passwordEncoder.encode("123456"), "13800000003"); // 创建用户3，未实名
        user3.setStatus(UserStatus.NORMAL); // 仅设置状态，余额默认 0
        userRepository.save(user3); // 持久化

        System.out.println("[DataInitializer] 初始化 3 个 C 端用户"); // 日志输出
    }

    /**
     * 初始化商户
     * <p>
     * - shop1/123456（已审核通过，余额 20000）
     * - shop2/123456（待审核）
     * </p>
     */
    private void initMerchants() {
        if (merchantRepository.count() > 0) { // 商户表已有数据则跳过
            return; // 幂等返回
        }
        Merchant m1 = new Merchant("shop1", passwordEncoder.encode("123456"), "优品数码", "13900000001", "91110000MA01ABCD1X"); // 创建商户1，含统一社会信用代码
        m1.setStatus(com.example.java9.model.MerchantStatus.APPROVED); // 设置为已审核通过
        m1.setDescription("专营3C数码产品"); // 设置经营范围描述
        m1.setBalance(new BigDecimal("20000")); // 商户余额 2 万
        merchantRepository.save(m1); // 持久化

        Merchant m2 = new Merchant("shop2", passwordEncoder.encode("123456"), "美食天地", "13900000002", "91110000MA02EFGH2X"); // 创建商户2，待审核
        merchantRepository.save(m2); // 持久化（默认 PENDING 状态）

        System.out.println("[DataInitializer] 初始化 2 个商户"); // 日志输出
    }

    /**
     * 初始化理财产品
     * <p>
     * 覆盖存款、基金、保险三类，风险等级从 1（低）到 4（中高）。
     * </p>
     */
    private void initProducts() {
        if (financialProductRepository.count() > 0) { // 产品表已有数据则跳过
            return; // 幂等返回
        }
        financialProductRepository.save(new FinancialProduct(
                "安心存90天", ProductType.DEPOSIT, new BigDecimal("0.035"),
                new BigDecimal("1000"), 90, 1, new BigDecimal("5000000"),
                "低风险定期存款产品，90天到期还本付息")); // 存款类，年化 3.5%，起投 1000，期限 90 天，风险等级 1

        financialProductRepository.save(new FinancialProduct(
                "稳健增利180天", ProductType.DEPOSIT, new BigDecimal("0.042"),
                new BigDecimal("5000"), 180, 2, new BigDecimal("10000000"),
                "中低风险定期存款，180天期限，收益稳定")); // 存款类，年化 4.2%，起投 5000，期限 180 天，风险等级 2

        financialProductRepository.save(new FinancialProduct(
                "成长优选基金", ProductType.FUND, new BigDecimal("0.065"),
                new BigDecimal("10000"), 365, 3, new BigDecimal("20000000"),
                "混合型基金产品，预期年化6.5%，历史业绩优良")); // 基金类，年化 6.5%，起投 1 万，期限 365 天，风险等级 3

        financialProductRepository.save(new FinancialProduct(
                "守护万能险", ProductType.INSURANCE, new BigDecimal("0.038"),
                new BigDecimal("20000"), 730, 4, new BigDecimal("8000000"),
                "万能型保险理财，兼顾保障与收益，2年期限")); // 保险类，年化 3.8%，起投 2 万，期限 730 天，风险等级 4

        System.out.println("[DataInitializer] 初始化 4 个理财产品"); // 日志输出
    }

    /**
     * 初始化营销活动
     */
    private void initActivities() {
        if (activityRepository.count() > 0) { // 活动表已有数据则跳过
            return; // 幂等返回
        }
        activityRepository.save(new Activity(
                "新手理财优惠券", ActivityType.COUPON, "新注册用户专享，投资满1000减50",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(30))); // 优惠券活动，昨日开始，30 天后结束

        activityRepository.save(new Activity(
                "暑期加息季", ActivityType.INTEREST_RATE, "限时加息1%，最高加息额度10万",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(60))); // 加息活动，昨日开始，60 天后结束

        System.out.println("[DataInitializer] 初始化 2 个营销活动"); // 日志输出
    }

    /**
     * 初始化系统配置
     * <p>
     * 风控阈值等关键参数，运营可在后台动态调整。
     * </p>
     */
    private void initSystemConfigs() {
        if (systemConfigRepository.count() > 0) { // 系统配置表已有数据则跳过
            return; // 幂等返回
        }
        systemConfigRepository.save(new SystemConfig("risk.large_amount_threshold", "50000", "单笔大额交易阈值（元）")); // 大额阈值 5 万
        systemConfigRepository.save(new SystemConfig("risk.super_large_threshold", "200000", "超大额自动冻结阈值（元）")); // 超大额冻结阈值 20 万
        systemConfigRepository.save(new SystemConfig("investment.min_amount", "1000", "最低投资金额（元）")); // 最低投资额 1000 元

        System.out.println("[DataInitializer] 初始化 3 个系统配置"); // 日志输出
    }
}
