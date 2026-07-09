package com.example.java9.repository; // 声明 Repository 层包路径

import com.example.java9.model.PaymentOrder; // 引入支付订单实体，对应 payment_orders 表
import com.example.java9.model.PaymentStatus; // 引入支付状态枚举（PENDING/PAID/REFUNDED 等）
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JPA 仓储基础接口
import org.springframework.stereotype.Repository; // 引入 @Repository 注解

import java.util.List; // 引入 List 容器
import java.util.Optional; // 引入 Optional 包装类

/**
 * 支付订单 Repository
 */
@Repository // 标识为持久层 Bean
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> { // 继承 JPA，主键 Long

    /** 根据订单号查询 */
    Optional<PaymentOrder> findByOrderNo(String orderNo); // 支付回调、对账、退款时凭订单号定位支付单

    /** 根据用户 ID 查询 */
    List<PaymentOrder> findByUserId(Long userId); // C 端用户查看自身支付记录

    /** 根据商户 ID 查询 */
    List<PaymentOrder> findByMerchantId(Long merchantId); // 商户后台对账查询自己收到的所有支付单

    /** 根据状态查询 */
    List<PaymentOrder> findByStatus(PaymentStatus status); // 运营/风控查看待支付或异常订单清单
}
