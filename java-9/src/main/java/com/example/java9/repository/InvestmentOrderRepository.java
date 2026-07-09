package com.example.java9.repository; // 声明 Repository 层包路径

import com.example.java9.model.InvestmentOrder; // 引入投资订单实体，对应 investment_orders 表
import com.example.java9.model.InvestmentStatus; // 引入订单状态枚举（PENDING/CONFIRMED/SETTLED 等）
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JPA 仓储基础接口
import org.springframework.stereotype.Repository; // 引入 @Repository 注解

import java.util.List; // 引入 List 容器
import java.util.Optional; // 引入 Optional 包装类

/**
 * 投资订单 Repository
 */
@Repository // 标识为持久层 Bean
public interface InvestmentOrderRepository extends JpaRepository<InvestmentOrder, Long> { // 继承 JPA，主键 Long

    /** 根据订单号查询 */
    Optional<InvestmentOrder> findByOrderNo(String orderNo); // 凭唯一订单号定位订单，用于支付回调与用户查询详情

    /** 根据用户 ID 查询订单列表 */
    List<InvestmentOrder> findByUserId(Long userId); // C 端"我的投资"页面展示该用户全部投资记录

    /** 根据用户 ID 和状态查询 */
    List<InvestmentOrder> findByUserIdAndStatus(Long userId, InvestmentStatus status); // 用户筛选某一状态订单（如查持有中/已结算）
}
