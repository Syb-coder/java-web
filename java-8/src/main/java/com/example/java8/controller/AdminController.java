package com.example.java8.controller;

import com.example.java8.dto.OrderResponse;
import com.example.java8.dto.StatsResponse;
import com.example.java8.dto.UserResponse;
import com.example.java8.service.FabricService;
import com.example.java8.service.OrderService;
import com.example.java8.service.StyleService;
import com.example.java8.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台管理控制器
 * <p>
 * 所有接口均需管理员登录（GET 由拦截器放行，PUT 推进订单状态需登录）。
 * </p>
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final OrderService orderService;
    private final FabricService fabricService;
    private final StyleService styleService;
    private final UserService userService;

    public AdminController(OrderService orderService, FabricService fabricService,
                           StyleService styleService, UserService userService) {
        this.orderService = orderService;
        this.fabricService = fabricService;
        this.styleService = styleService;
        this.userService = userService;
    }

    /**
     * 仪表盘统计
     *
     * @return 各资源总数
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> stats() {
        return ResponseEntity.ok(new StatsResponse(
                fabricService.count(),
                styleService.count(),
                orderService.count(),
                userService.count()
        ));
    }

    /**
     * 查询全部订单（管理员视角）
     *
     * @return 全部订单列表
     */
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> allOrders() {
        return ResponseEntity.ok(orderService.listAll());
    }

    /**
     * 推进指定订单的状态
     *
     * @param id 订单 ID
     * @return 200（成功） / 404（不存在或已完成）
     */
    @PutMapping("/orders/{id}/advance")
    public ResponseEntity<OrderResponse> advanceOrder(@PathVariable Long id) {
        OrderResponse resp = orderService.advanceStatus(id);
        if (resp == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resp);
    }

    /**
     * 查询全部用户
     *
     * @return 用户响应列表
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> allUsers() {
        return ResponseEntity.ok(userService.listAll().stream()
                .map(userService::toResponse)
                .toList());
    }
}
