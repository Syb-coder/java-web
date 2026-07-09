package com.example.java9.service;  // 声明服务层包路径

import com.example.java9.model.Activity;  // 导入活动实体
import com.example.java9.model.ActivityType;  // 导入活动类型枚举（COUPON/INTEREST_RATE/SIGN_IN）
import com.example.java9.model.Coupon;  // 导入优惠券实体
import com.example.java9.model.CouponStatus;  // 导入优惠券状态枚举（UNUSED/USED/EXPIRED）
import com.example.java9.repository.ActivityRepository;  // 导入活动 Repository
import com.example.java9.repository.CouponRepository;  // 导入优惠券 Repository
import org.springframework.stereotype.Service;  // 导入 Spring Service 注解
import org.springframework.transaction.annotation.Transactional;  // 导入事务注解

import java.math.BigDecimal;  // 导入高精度十进制类，优惠券面额必须用 BigDecimal 避免 double 精度丢失
import java.time.LocalDateTime;  // 导入时间类
import java.util.List;  // 导入集合 List

/**
 * 营销活动与优惠券服务
 * <p>
 * 负责营销活动的创建、查询、结束，以及用户优惠券领取与查询。
 * 优惠券面额与门槛根据活动类型差异化配置：
 * <ul>
 *   <li>COUPON：满 1000 减 50，有效期至活动结束</li>
 *   <li>INTEREST_RATE：满 500 减 10，有效期至活动结束</li>
 *   <li>SIGN_IN：满 100 减 5，有效期 7 天</li>
 * </ul>
 * </p>
 */
@Service  // 标记为 Spring Service Bean
public class ActivityService {

    /** COUPON 活动优惠券面额 */
    // 使用 BigDecimal 而非 double：面额是金额，double 存在二进制浮点精度问题，BigDecimal 可精确表示
    private static final BigDecimal COUPON_AMOUNT = new BigDecimal("50");

    /** COUPON 活动使用门槛 */
    private static final BigDecimal COUPON_MIN_AMOUNT = new BigDecimal("1000");

    /** INTEREST_RATE 活动优惠券面额 */
    private static final BigDecimal INTEREST_RATE_AMOUNT = new BigDecimal("10");

    /** INTEREST_RATE 活动使用门槛 */
    private static final BigDecimal INTEREST_RATE_MIN_AMOUNT = new BigDecimal("500");

    /** SIGN_IN 活动优惠券面额 */
    private static final BigDecimal SIGN_IN_AMOUNT = new BigDecimal("5");

    /** SIGN_IN 活动使用门槛 */
    private static final BigDecimal SIGN_IN_MIN_AMOUNT = new BigDecimal("100");

    /** SIGN_IN 优惠券有效期天数 */
    private static final long SIGN_IN_COUPON_VALID_DAYS = 7L;

    private final ActivityRepository activityRepository;  // 活动 Repository，final 保证不可变
    private final CouponRepository couponRepository;  // 优惠券 Repository，final 保证不可变

    // 构造器注入：保证依赖不可变、显式暴露依赖、便于单元测试，Spring 启动时即可发现循环依赖
    public ActivityService(ActivityRepository activityRepository, CouponRepository couponRepository) {
        this.activityRepository = activityRepository;  // 注入活动 Repository
        this.couponRepository = couponRepository;  // 注入优惠券 Repository
    }

    /**
     * 创建活动
     * <p>
     * 新建活动默认 active=true，开始与结束时间由调用方指定。
     * </p>
     *
     * @param title       活动标题
     * @param type        活动类型
     * @param description 活动描述
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @return 已保存的活动实体
     */
    @Transactional  // 声明事务边界：活动创建原子化
    public Activity create(String title, ActivityType type, String description,
                           LocalDateTime startTime, LocalDateTime endTime) {
        Activity activity = new Activity(title, type, description, startTime, endTime);  // 构造活动实体，构造器内默认 active=true
        return activityRepository.save(activity);  // 持久化并返回带 ID 的实体
    }

    /**
     * 查询进行中的活动
     *
     * @return active=true 的活动列表
     */
    public List<Activity> findActive() {
        return activityRepository.findByActiveTrue();  // 查询 active=true 的活动供 C 端展示
    }

    /**
     * 查询所有活动（管理后台用）
     *
     * @return 全部活动列表
     */
    public List<Activity> findAll() {
        return activityRepository.findAll();  // 查询全部活动
    }

    /**
     * 结束活动
     * <p>
     * 将活动 active 置为 false，使其不再对 C 端可见。
     * </p>
     *
     * @param id 活动 ID
     * @return 更新后的活动实体
     * @throws IllegalArgumentException 活动不存在
     */
    @Transactional  // 声明事务边界：活动状态更新原子化
    public Activity endActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在"));  // 查询活动，不存在则抛异常
        activity.setActive(false);  // 置为非活跃，C 端不再可见但仍保留数据
        return activityRepository.save(activity);  // 持久化并返回
    }

    /**
     * 用户领取优惠券
     * <p>
     * 校验活动有效且在有效期内后，根据活动类型生成对应面额与门槛的优惠券。
     * </p>
     *
     * @param userId     用户 ID
     * @param activityId 活动 ID
     * @return 已领取的优惠券
     * @throws IllegalArgumentException 活动不存在
     * @throws IllegalStateException    活动已结束或不在有效期内
     */
    @Transactional  // 声明事务边界：活动校验与优惠券生成原子化
    public Coupon claimCoupon(Long userId, Long activityId) {
        // 1. 查找活动
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在"));  // 查询活动，不存在则抛异常

        // 2. 校验活动 active=true 且在有效期内
        LocalDateTime now = LocalDateTime.now();  // 获取当前时间
        // 使用 Boolean.TRUE.equals 避免 active 为 null 时 NPE
        if (!Boolean.TRUE.equals(activity.getActive())) {
            throw new IllegalStateException("活动已结束");  // 活动已结束快速失败
        }
        if (now.isBefore(activity.getStartTime()) || now.isAfter(activity.getEndTime())) {
            throw new IllegalStateException("活动不在有效期内");  // 未开始或已过期快速失败
        }

        // 3. 根据活动类型差异化生成优惠券
        BigDecimal amount;  // 优惠券面额
        BigDecimal minAmount;  // 使用门槛
        LocalDateTime expiredAt;  // 过期时间
        switch (activity.getType()) {  // 根据活动类型分支
            case COUPON:
                // 理财优惠券：满 1000 减 50，有效期至活动结束
                amount = COUPON_AMOUNT;  // 面额 50
                minAmount = COUPON_MIN_AMOUNT;  // 门槛 1000
                expiredAt = activity.getEndTime();  // 过期时间与活动结束时间一致
                break;
            case INTEREST_RATE:
                // 加息活动：满 500 减 10，有效期至活动结束
                amount = INTEREST_RATE_AMOUNT;  // 面额 10
                minAmount = INTEREST_RATE_MIN_AMOUNT;  // 门槛 500
                expiredAt = activity.getEndTime();  // 过期时间与活动结束时间一致
                break;
            case SIGN_IN:
                // 签到福利：满 100 减 5，有效期 7 天
                amount = SIGN_IN_AMOUNT;  // 面额 5
                minAmount = SIGN_IN_MIN_AMOUNT;  // 门槛 100
                expiredAt = now.plusDays(SIGN_IN_COUPON_VALID_DAYS);  // 领取后 7 天过期
                break;
            default:
                // 前置防御：新增活动类型未适配时快速失败
                throw new IllegalStateException("不支持的活动类型：" + activity.getType());
        }

        Coupon coupon = new Coupon(userId, activityId, activity.getTitle(), amount, minAmount, expiredAt);  // 构造优惠券实体
        return couponRepository.save(coupon);  // 持久化并返回带 ID 的优惠券
    }

    /**
     * 查询用户优惠券
     *
     * @param userId 用户 ID
     * @return 该用户的全部优惠券列表
     */
    public List<Coupon> findUserCoupons(Long userId) {
        return couponRepository.findByUserId(userId);  // 查询用户全部优惠券
    }

    /**
     * 查询用户未使用优惠券
     *
     * @param userId 用户 ID
     * @return 该用户状态为 UNUSED 的优惠券列表
     */
    public List<Coupon> findUserUnusedCoupons(Long userId) {
        return couponRepository.findByUserIdAndStatus(userId, CouponStatus.UNUSED);  // 按用户 ID 与状态 UNUSED 查询可用优惠券
    }
}
