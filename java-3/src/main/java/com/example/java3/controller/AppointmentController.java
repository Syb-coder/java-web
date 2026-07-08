// 声明当前类所在的包路径
package com.example.java3.controller;

// 导入预约请求 DTO
import com.example.java3.dto.AppointmentRequest;
// 导入评价请求 DTO
import com.example.java3.dto.ReviewRequest;
// 导入预约实体
import com.example.java3.model.Appointment;
// 导入评价实体
import com.example.java3.model.Review;
// 导入学生用户实体
import com.example.java3.model.User;
// 导入预约服务
import com.example.java3.service.AppointmentService;
// 导入评价服务
import com.example.java3.service.ReviewService;
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

// 导入 List
import java.util.List;
// 导入 Map
import java.util.Map;

/**
 * 预约与评价控制器
 */
// @RestController：REST 控制器，返回 JSON
@RestController
// @RequestMapping("/api")：基础路径 /api，因预约与评价分属不同子路径，故基础路径仅设为 /api
@RequestMapping("/api")
public class AppointmentController {

    // 注入的预约服务
    private final AppointmentService appointmentService;
    // 注入的评价服务
    private final ReviewService reviewService;

    // 构造器注入两个服务
    public AppointmentController(AppointmentService appointmentService, ReviewService reviewService) {
        this.appointmentService = appointmentService;
        this.reviewService = reviewService;
    }

    /**
     * 创建自提预约
     *
     * @param req     预约请求
     * @param session 会话
     * @return 预约实体
     */
    // @PostMapping("/appointments")：处理 POST /api/appointments 请求
    @PostMapping("/appointments")
    public ResponseEntity<?> create(@Valid @RequestBody AppointmentRequest req, HttpSession session) {
        // 从会话获取当前学生
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            // 调用服务创建自提预约，关联买家 ID
            Appointment a = appointmentService.create(u.getId(), req);
            // 返回 200 OK 和预约实体
            return ResponseEntity.ok(a);
        } catch (IllegalArgumentException e) {
            // 订单状态非法等，返回 422
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 查询我的预约
     *
     * @param session 会话
     * @return 预约列表
     */
    // @GetMapping("/appointments/mine")：处理 GET /api/appointments/mine 请求
    @GetMapping("/appointments/mine")
    public ResponseEntity<?> mine(HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 查询当前用户的全部预约
        List<Appointment> list = appointmentService.findByUser(u.getId());
        // 返回 200 OK
        return ResponseEntity.ok(list);
    }

    /**
     * 按订单查询预约
     *
     * @param orderId 订单 ID
     * @return 预约 Optional
     */
    // @GetMapping("/orders/{orderId}/appointment")：处理 GET /api/orders/{orderId}/appointment 请求
    @GetMapping("/orders/{orderId}/appointment")
    public ResponseEntity<?> byOrder(@PathVariable Long orderId) {
        // 返回 200 OK 和该订单关联的预约（Optional，可能为空）
        return ResponseEntity.ok(appointmentService.findByOrder(orderId));
    }

    /**
     * 发表交易评价
     *
     * @param req     评价请求
     * @param session 会话
     * @return 评价实体
     */
    // @PostMapping("/reviews")：处理 POST /api/reviews 请求
    @PostMapping("/reviews")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequest req, HttpSession session) {
        // 从会话获取当前学生
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            // 调用服务发表评价，关联评价人 ID
            Review r = reviewService.create(u.getId(), req);
            // 返回 200 OK 和评价实体
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            // 订单未完成等，返回 422
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 查询卖家收到的评价
     *
     * @param sellerId 卖家 ID
     * @return 评价列表
     */
    // @GetMapping("/users/{sellerId}/reviews")：处理 GET /api/users/{sellerId}/reviews 请求
    @GetMapping("/users/{sellerId}/reviews")
    public ResponseEntity<?> sellerReviews(@PathVariable Long sellerId) {
        // 返回 200 OK 和卖家收到的全部评价
        return ResponseEntity.ok(reviewService.findBySeller(sellerId));
    }
}
