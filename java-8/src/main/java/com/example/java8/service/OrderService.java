package com.example.java8.service;

import com.example.java8.dto.OrderRequest;
import com.example.java8.dto.OrderResponse;
import com.example.java8.model.Fabric;
import com.example.java8.model.Measurement;
import com.example.java8.model.Order;
import com.example.java8.model.OrderStatus;
import com.example.java8.model.Style;
import com.example.java8.model.User;
import com.example.java8.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单业务服务
 * <p>
 * 处理用户下单、订单查询、管理员状态推进等业务逻辑。
 * 总价计算规则：款式工费 + 面料单价 × 默认 3 米用料。
 * </p>
 */
@Service
public class OrderService {

    /** 默认每单面料用料（米） */
    private static final double DEFAULT_FABRIC_USAGE = 3.0;

    private final OrderRepository repository;
    private final StyleService styleService;
    private final FabricService fabricService;
    private final MeasurementService measurementService;

    public OrderService(OrderRepository repository, StyleService styleService,
                        FabricService fabricService, MeasurementService measurementService) {
        this.repository = repository;
        this.styleService = styleService;
        this.fabricService = fabricService;
        this.measurementService = measurementService;
    }

    /**
     * 用户创建订单
     *
     * @param user 当前登录用户
     * @param req  订单请求
     * @return 订单响应
     * @throws IllegalArgumentException 款式/面料/量体数据不存在或量体不属于该用户
     */
    public OrderResponse create(User user, OrderRequest req) {
        Style style = styleService.getById(req.styleId());
        Fabric fabric = fabricService.getById(req.fabricId());
        Measurement measurement = measurementService.getById(req.measurementId());
        // 关联实体校验
        if (style == null || fabric == null || measurement == null) {
            throw new IllegalArgumentException("款式/面料/量体数据不存在");
        }
        // 量体数据归属校验：防止用户提交他人量体 ID
        if (!measurement.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("量体数据不属于当前用户");
        }
        Order order = new Order();
        order.setUser(user);
        order.setStyle(style);
        order.setFabric(fabric);
        order.setMeasurement(measurement);
        // 总价 = 工费 + 面料单价 × 默认用料
        order.setTotalPrice(style.getCraftFee() + fabric.getUnitPrice() * DEFAULT_FABRIC_USAGE);
        order.setStatus(OrderStatus.PENDING);
        order.setRemark(req.remark());
        order.setCreateTime(LocalDateTime.now());
        return toResponse(repository.save(order));
    }

    /**
     * 查询指定用户的所有订单
     *
     * @param userId 用户 ID
     * @return 订单响应列表（按下单时间倒序）
     */
    public List<OrderResponse> listByUser(Long userId) {
        return repository.findByUserIdOrderByCreateTimeDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 查询所有订单（后台管理使用）
     *
     * @return 全部订单响应列表
     */
    public List<OrderResponse> listAll() {
        return repository.findAllByOrderByCreateTimeDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 推进订单状态至下一阶段
     *
     * @param id 订单 ID
     * @return 更新后的订单响应（不存在或已完成返回 null）
     */
    public OrderResponse advanceStatus(Long id) {
        Order order = repository.findById(id).orElse(null);
        if (order == null) return null;
        // 已完成订单不允许再推进
        if (order.getStatus() == OrderStatus.COMPLETED) return null;
        // 按枚举顺序前进一格
        OrderStatus[] flow = OrderStatus.values();
        int nextIdx = order.getStatus().ordinal() + 1;
        if (nextIdx >= flow.length) return null;
        order.setStatus(flow[nextIdx]);
        return toResponse(repository.save(order));
    }

    /**
     * 统计订单总数
     *
     * @return 数量
     */
    public long count() {
        return repository.count();
    }

    /**
     * 实体转响应 DTO（包含关联实体名称）
     *
     * @param o 订单实体
     * @return 响应 DTO
     */
    public OrderResponse toResponse(Order o) {
        return new OrderResponse(o.getId(),
                o.getUser().getId(), o.getUser().getNickname() != null ? o.getUser().getNickname() : o.getUser().getUsername(),
                o.getStyle().getId(), o.getStyle().getName(),
                o.getFabric().getId(), o.getFabric().getName(),
                o.getMeasurement() != null ? o.getMeasurement().getId() : null,
                o.getMeasurement() != null ? o.getMeasurement().getName() : null,
                o.getTotalPrice(), o.getStatus(), o.getRemark(), o.getCreateTime());
    }
}
