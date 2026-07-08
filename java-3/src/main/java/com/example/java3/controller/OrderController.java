package com.example.java3.controller;

import com.example.java3.dto.OrderRequest;
import com.example.java3.dto.OrderResponse;
import com.example.java3.model.User;
import com.example.java3.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 下单
     *
     * @param req     下单请求
     * @param session 会话
     * @return 订单响应
     */
    @PostMapping
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequest req, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            return ResponseEntity.ok(orderService.placeOrder(u.getId(), req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 我的订单
     *
     * @param session 会话
     * @return 订单列表
     */
    @GetMapping("/mine")
    public ResponseEntity<?> myOrders(HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        return ResponseEntity.ok(orderService.myOrders(u.getId()));
    }

    /**
     * 订单详情
     *
     * @param id 订单 ID
     * @return 订单响应
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.detail(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 买家确认付款
     *
     * @param id      订单 ID
     * @param session 会话
     * @return 订单响应
     */
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            return ResponseEntity.ok(orderService.pay(id, u.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 确认完成（买卖双方均可）
     *
     * @param id      订单 ID
     * @param session 会话
     * @return 订单响应
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            return ResponseEntity.ok(orderService.complete(id, u.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 取消订单
     *
     * @param id      订单 ID
     * @param session 会话
     * @return 订单响应
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            return ResponseEntity.ok(orderService.cancel(id, u.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
