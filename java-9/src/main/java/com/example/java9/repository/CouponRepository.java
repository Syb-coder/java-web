package com.example.java9.repository; // 声明 Repository 层包路径

import com.example.java9.model.Coupon; // 引入优惠券实体，对应 coupons 表
import com.example.java9.model.CouponStatus; // 引入优惠券状态枚举（UNUSED/USED/EXPIRED 等）
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JPA 仓储基础接口
import org.springframework.stereotype.Repository; // 引入 @Repository 注解

import java.util.List; // 引入 List 容器

/**
 * 优惠券 Repository
 */
@Repository // 标识为持久层 Bean
public interface CouponRepository extends JpaRepository<Coupon, Long> { // 继承 JPA，主键 Long

    /** 根据用户 ID 查询优惠券 */
    List<Coupon> findByUserId(Long userId); // C 端"我的优惠券"页面展示用户全部券（含已用/过期）

    /** 根据用户 ID 和状态查询 */
    List<Coupon> findByUserIdAndStatus(Long userId, CouponStatus status); // 用户支付前筛选可用(UNUSED)优惠券用于抵扣
}
