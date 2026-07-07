package com.example.java8.controller;

import com.example.java8.dto.OrderRequest;
import com.example.java8.dto.OrderResponse;
import com.example.java8.model.User;
import com.example.java8.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

/**
 * 订单控制器（前台用户）
 * <p>提供用户下单、查询本人订单接口。</p>
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 用户下单
     *
     * @param user 当前登录用户
     * @param req  订单请求
     * @return 201 + 订单响应 / 401（未登录） / 400（参数不合规）
     */
    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user,
            @Valid @RequestBody OrderRequest req) {
        if (user == null) return ResponseEntity.status(401).build();
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(orderService.create(user, req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 查询当前用户的全部订单
     *
     * @param user 当前登录用户
     * @return 订单响应列表 / 401（未登录）
     */
    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> myOrders(
            @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(orderService.listByUser(user.getId()));
    }
}
