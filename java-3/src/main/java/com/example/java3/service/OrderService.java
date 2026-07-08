package com.example.java3.service;

import com.example.java3.dto.OrderRequest;
import com.example.java3.dto.OrderResponse;
import com.example.java3.model.*;
import com.example.java3.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务
 * <p>
 * 负责下单、状态流转、纠纷处理等。
 * 订单状态机：PENDING -> PAID -> COMPLETED，或任意阶段 CANCELLED。
 * </p>
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * 下单
     *
     * @param buyerId 买家 ID
     * @param req     下单请求
     * @return 订单响应
     */
    public OrderResponse placeOrder(Long buyerId, OrderRequest req) {
        Product p = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        // 校验商品已审核通过
        if (p.getAuditStatus() != ProductAuditStatus.APPROVED) {
            throw new IllegalArgumentException("商品未通过审核，无法下单");
        }
        // 校验商品未售出
        if (Boolean.TRUE.equals(p.getSold())) {
            throw new IllegalArgumentException("商品已售出");
        }
        // 校验不能购买自己的商品
        if (p.getSellerId().equals(buyerId)) {
            throw new IllegalArgumentException("不能购买自己发布的商品");
        }
        // 生成订单号：时间戳 + UUID 片段
        String orderNo = "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Order order = new Order(orderNo, p.getId(), buyerId, p.getSellerId(),
                p.getPrice(), req.getBuyerRemark());
        orderRepository.save(order);
        return toResponse(order);
    }

    /**
     * 买家确认付款（线下自提场景下表示达成交易意向）
     *
     * @param orderId 订单 ID
     * @param userId  当前用户 ID
     * @return 订单响应
     */
    public OrderResponse pay(Long orderId, Long userId) {
        Order o = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        if (!o.getBuyerId().equals(userId)) {
            throw new IllegalArgumentException("仅买家可确认付款");
        }
        if (o.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("订单状态不允许付款");
        }
        o.setStatus(OrderStatus.PAID);
        orderRepository.save(o);
        return toResponse(o);
    }

    /**
     * 确认完成（买卖双方均可触发）
     *
     * @param orderId 订单 ID
     * @param userId  当前用户 ID
     * @return 订单响应
     */
    public OrderResponse complete(Long orderId, Long userId) {
        Order o = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        // 买家或卖家均可确认完成
        if (!o.getBuyerId().equals(userId) && !o.getSellerId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此订单");
        }
        if (o.getStatus() != OrderStatus.PAID) {
            throw new IllegalArgumentException("订单状态不允许完成");
        }
        o.setStatus(OrderStatus.COMPLETED);
        o.setCompletedAt(LocalDateTime.now());
        orderRepository.save(o);
        // 标记商品已售出
        productRepository.findById(o.getProductId()).ifPresent(p -> {
            p.setSold(true);
            productRepository.save(p);
        });
        return toResponse(o);
    }

    /**
     * 取消订单
     *
     * @param orderId 订单 ID
     * @param userId  当前用户 ID
     * @return 订单响应
     */
    public OrderResponse cancel(Long orderId, Long userId) {
        Order o = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        if (!o.getBuyerId().equals(userId) && !o.getSellerId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此订单");
        }
        if (o.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("已完成订单不可取消");
        }
        o.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(o);
        return toResponse(o);
    }

    /**
     * 管理员介入纠纷处理
     *
     * @param orderId 订单 ID
     * @param remark  处理备注
     * @return 订单响应
     */
    public OrderResponse handleDispute(Long orderId, String remark) {
        Order o = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        o.setDisputeRemark(remark);
        orderRepository.save(o);
        return toResponse(o);
    }

    /**
     * 查询用户相关订单
     *
     * @param userId 用户 ID
     * @return 订单列表
     */
    public List<OrderResponse> myOrders(Long userId) {
        return orderRepository.findByBuyerIdOrSellerIdOrderByCreatedAtDesc(userId, userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 查询全部订单（后台）
     *
     * @return 订单列表
     */
    public List<OrderResponse> all() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 按 ID 查询订单
     *
     * @param id 订单 ID
     * @return 订单响应
     */
    public OrderResponse detail(Long id) {
        return toResponse(orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在")));
    }

    /**
     * 实体转响应
     *
     * @param o 订单实体
     * @return 响应
     */
    private OrderResponse toResponse(Order o) {
        String productTitle = productRepository.findById(o.getProductId())
                .map(Product::getTitle).orElse("未知商品");
        String buyerName = userRepository.findById(o.getBuyerId())
                .map(User::getNickname).orElse("未知用户");
        String sellerName = userRepository.findById(o.getSellerId())
                .map(User::getNickname).orElse("未知用户");
        return new OrderResponse(o, productTitle, buyerName, sellerName);
    }
}
