// 声明当前类所在包路径
package com.example.java3.service;

// 导入订单请求 DTO
import com.example.java3.dto.OrderRequest;
// 导入订单响应 DTO
import com.example.java3.dto.OrderResponse;
// 导入 model 包下全部实体类
import com.example.java3.model.*;
// 导入 repository 包下全部仓储接口
import com.example.java3.repository.*;
// 导入 @Service 注解
import org.springframework.stereotype.Service;

// 导入本地日期时间类
import java.time.LocalDateTime;
// 导入日期时间格式化器
import java.time.format.DateTimeFormatter;
// 导入 List 集合
import java.util.List;
// 导入 UUID 工具类，用于生成订单号
import java.util.UUID;

/**
 * 订单服务
 * <p>
 * 负责下单、状态流转、纠纷处理等。
 * 订单状态机：PENDING -> PAID -> COMPLETED，或任意阶段 CANCELLED。
 * </p>
 */
// 标识为业务层组件，由 Spring 容器管理为单例
@Service
public class OrderService {

    // 订单仓储，处理订单表 CRUD
    private final OrderRepository orderRepository;
    // 商品仓储，用于校验商品状态和补充商品标题
    private final ProductRepository productRepository;
    // 用户仓储，用于补充买家、卖家昵称
    private final UserRepository userRepository;

    // 构造方法注入三个仓储 Bean
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
        // 根据 ID 查询商品
        Product p = productRepository.findById(req.getProductId())
                // 商品不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        // 校验商品已审核通过，未审核商品不可下单
        if (p.getAuditStatus() != ProductAuditStatus.APPROVED) {
            throw new IllegalArgumentException("商品未通过审核，无法下单");
        }
        // 校验商品未售出，避免重复交易
        if (Boolean.TRUE.equals(p.getSold())) {
            throw new IllegalArgumentException("商品已售出");
        }
        // 校验不能购买自己的商品，避免刷单
        if (p.getSellerId().equals(buyerId)) {
            throw new IllegalArgumentException("不能购买自己发布的商品");
        }
        // 生成订单号：时间戳 + UUID 片段，保证唯一性
        String orderNo = "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        // 构造订单实体，包含订单号、商品 ID、买家 ID、卖家 ID、价格、买家备注
        Order order = new Order(orderNo, p.getId(), buyerId, p.getSellerId(),
                p.getPrice(), req.getBuyerRemark());
        // 持久化订单
        orderRepository.save(order);
        // 转换为响应 DTO
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
        // 根据 ID 查询订单
        Order o = orderRepository.findById(orderId)
                // 订单不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        // 校验当前用户为买家，仅买家可确认付款
        if (!o.getBuyerId().equals(userId)) {
            throw new IllegalArgumentException("仅买家可确认付款");
        }
        // 校验订单状态为 PENDING，非 PENDING 状态不可付款
        if (o.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("订单状态不允许付款");
        }
        // 状态流转：PENDING -> PAID
        o.setStatus(OrderStatus.PAID);
        // 持久化
        orderRepository.save(o);
        // 返回响应 DTO
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
        // 根据 ID 查询订单
        Order o = orderRepository.findById(orderId)
                // 订单不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        // 买家或卖家均可确认完成
        if (!o.getBuyerId().equals(userId) && !o.getSellerId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此订单");
        }
        // 校验订单状态为 PAID，仅已付款订单可完成
        if (o.getStatus() != OrderStatus.PAID) {
            throw new IllegalArgumentException("订单状态不允许完成");
        }
        // 状态流转：PAID -> COMPLETED
        o.setStatus(OrderStatus.COMPLETED);
        // 记录完成时间
        o.setCompletedAt(LocalDateTime.now());
        // 持久化
        orderRepository.save(o);
        // 标记商品已售出，前台不再展示
        productRepository.findById(o.getProductId()).ifPresent(p -> {
            // 设置 sold 标志
            p.setSold(true);
            // 持久化商品
            productRepository.save(p);
        });
        // 返回响应 DTO
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
        // 根据 ID 查询订单
        Order o = orderRepository.findById(orderId)
                // 订单不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        // 买家或卖家均可取消
        if (!o.getBuyerId().equals(userId) && !o.getSellerId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此订单");
        }
        // 已完成订单不可取消，避免交易回滚
        if (o.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("已完成订单不可取消");
        }
        // 状态流转：任意阶段 -> CANCELLED
        o.setStatus(OrderStatus.CANCELLED);
        // 持久化
        orderRepository.save(o);
        // 返回响应 DTO
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
        // 根据 ID 查询订单
        Order o = orderRepository.findById(orderId)
                // 订单不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        // 设置纠纷处理备注
        o.setDisputeRemark(remark);
        // 持久化
        orderRepository.save(o);
        // 返回响应 DTO
        return toResponse(o);
    }

    /**
     * 查询用户相关订单
     *
     * @param userId 用户 ID
     * @return 订单列表
     */
    public List<OrderResponse> myOrders(Long userId) {
        // 查询用户作为买家或卖家的所有订单，按创建时间降序
        return orderRepository.findByBuyerIdOrSellerIdOrderByCreatedAtDesc(userId, userId)
                // 转 Stream
                .stream()
                // 映射为响应 DTO
                .map(this::toResponse)
                // 收集为 List
                .toList();
    }

    /**
     * 查询全部订单（后台）
     *
     * @return 订单列表
     */
    public List<OrderResponse> all() {
        // 查询全部订单，按创建时间降序
        return orderRepository.findAllByOrderByCreatedAtDesc()
                // 转 Stream
                .stream()
                // 映射为响应 DTO
                .map(this::toResponse)
                // 收集为 List
                .toList();
    }

    /**
     * 按 ID 查询订单
     *
     * @param id 订单 ID
     * @return 订单响应
     */
    public OrderResponse detail(Long id) {
        // 根据 ID 查询订单并转换为响应 DTO
        return toResponse(orderRepository.findById(id)
                // 订单不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("订单不存在")));
    }

    /**
     * 实体转响应
     *
     * @param o 订单实体
     * @return 响应
     */
    private OrderResponse toResponse(Order o) {
        // 查询商品标题，不存在时使用占位文本
        String productTitle = productRepository.findById(o.getProductId())
                // 取商品标题
                .map(Product::getTitle).orElse("未知商品");
        // 查询买家昵称，不存在时使用占位文本
        String buyerName = userRepository.findById(o.getBuyerId())
                // 取昵称
                .map(User::getNickname).orElse("未知用户");
        // 查询卖家昵称，不存在时使用占位文本
        String sellerName = userRepository.findById(o.getSellerId())
                // 取昵称
                .map(User::getNickname).orElse("未知用户");
        // 构造响应 DTO
        return new OrderResponse(o, productTitle, buyerName, sellerName);
    }
}
