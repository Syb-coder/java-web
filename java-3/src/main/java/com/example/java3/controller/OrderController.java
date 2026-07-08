// 声明当前类所在的包路径
package com.example.java3.controller;

// 导入下单请求 DTO
import com.example.java3.dto.OrderRequest;
// 导入订单响应 DTO
import com.example.java3.dto.OrderResponse;
// 导入学生用户实体
import com.example.java3.model.User;
// 导入订单服务
import com.example.java3.service.OrderService;
// 导入 HttpSession
import jakarta.servlet.http.HttpSession;
// 导入 @Valid
import jakarta.validation.Valid;
// 导入 HttpStatus
import org.springframework.http.HttpStatus;
// 导入 ResponseEntity
import org.springframework.http.ResponseEntity;
// 导入 Spring MVC 注解
import org.springframework.web.bind.annotation.*;

// 导入 Map，构建 JSON 响应体
import java.util.Map;

/**
 * 订单控制器
 */
// @RestController：REST 控制器，返回 JSON
@RestController
// @RequestMapping("/api/orders")：基础路径 /api/orders
@RequestMapping("/api/orders")
public class OrderController {

    // 注入的订单服务
    private final OrderService orderService;

    // 构造器注入订单服务
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
    // @PostMapping：处理 POST /api/orders 请求
    @PostMapping
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequest req, HttpSession session) {
        // 从会话获取当前学生
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            // 调用服务下单，关联买家 ID
            return ResponseEntity.ok(orderService.placeOrder(u.getId(), req));
        } catch (IllegalArgumentException e) {
            // 商品已售出、不能购买自己商品等，返回 422
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
    // @GetMapping("/mine")：处理 GET /api/orders/mine 请求
    @GetMapping("/mine")
    public ResponseEntity<?> myOrders(HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 返回 200 OK 和当前用户的订单列表（含买入与卖出）
        return ResponseEntity.ok(orderService.myOrders(u.getId()));
    }

    /**
     * 订单详情
     *
     * @param id 订单 ID
     * @return 订单响应
     */
    // @GetMapping("/{id}")：处理 GET /api/orders/{id} 请求
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        try {
            // 返回 200 OK 和订单详情
            return ResponseEntity.ok(orderService.detail(id));
        } catch (IllegalArgumentException e) {
            // 订单不存在，返回 404
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
    // @PostMapping("/{id}/pay")：处理 POST /api/orders/{id}/pay 请求
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            // 调用服务确认付款，校验买家身份
            return ResponseEntity.ok(orderService.pay(id, u.getId()));
        } catch (IllegalArgumentException e) {
            // 订单状态非法或非买家，返回 422
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
    // @PostMapping("/{id}/complete")：处理 POST /api/orders/{id}/complete 请求
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            // 调用服务确认完成交易
            return ResponseEntity.ok(orderService.complete(id, u.getId()));
        } catch (IllegalArgumentException e) {
            // 订单状态非法，返回 422
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
    // @PostMapping("/{id}/cancel")：处理 POST /api/orders/{id}/cancel 请求
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            // 调用服务取消订单
            return ResponseEntity.ok(orderService.cancel(id, u.getId()));
        } catch (IllegalArgumentException e) {
            // 订单状态非法（如已完成不可取消），返回 422
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
