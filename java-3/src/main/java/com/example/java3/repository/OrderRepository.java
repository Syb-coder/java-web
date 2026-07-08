package com.example.java3.repository;

import com.example.java3.model.Order;
import com.example.java3.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 订单仓储
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 按订单号查询
     *
     * @param orderNo 订单号
     * @return 订单 Optional
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 查询买家全部订单（按时间倒序）
     *
     * @param buyerId 买家 ID
     * @return 订单列表
     */
    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    /**
     * 查询卖家全部订单（按时间倒序）
     *
     * @param sellerId 卖家 ID
     * @return 订单列表
     */
    List<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId);

    /**
     * 查询用户相关订单（买家或卖家）
     *
     * @param buyerId  买家 ID
     * @param sellerId 卖家 ID
     * @return 订单列表
     */
    List<Order> findByBuyerIdOrSellerIdOrderByCreatedAtDesc(Long buyerId, Long sellerId);

    /**
     * 按状态统计订单数量
     *
     * @param status 订单状态
     * @return 数量
     */
    long countByStatus(OrderStatus status);

    /**
     * 查询全部订单（按时间倒序）
     *
     * @return 订单列表
     */
    List<Order> findAllByOrderByCreatedAtDesc();

    /**
     * 统计订单总数
     *
     * @return 数量
     */
    long count();
}
