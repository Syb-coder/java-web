package com.example.java3.controller;

import com.example.java3.dto.AppointmentRequest;
import com.example.java3.dto.ReviewRequest;
import com.example.java3.model.Appointment;
import com.example.java3.model.Review;
import com.example.java3.model.User;
import com.example.java3.service.AppointmentService;
import com.example.java3.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 预约与评价控制器
 */
@RestController
@RequestMapping("/api")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ReviewService reviewService;

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
    @PostMapping("/appointments")
    public ResponseEntity<?> create(@Valid @RequestBody AppointmentRequest req, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            Appointment a = appointmentService.create(u.getId(), req);
            return ResponseEntity.ok(a);
        } catch (IllegalArgumentException e) {
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
    @GetMapping("/appointments/mine")
    public ResponseEntity<?> mine(HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        List<Appointment> list = appointmentService.findByUser(u.getId());
        return ResponseEntity.ok(list);
    }

    /**
     * 按订单查询预约
     *
     * @param orderId 订单 ID
     * @return 预约 Optional
     */
    @GetMapping("/orders/{orderId}/appointment")
    public ResponseEntity<?> byOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(appointmentService.findByOrder(orderId));
    }

    /**
     * 发表交易评价
     *
     * @param req     评价请求
     * @param session 会话
     * @return 评价实体
     */
    @PostMapping("/reviews")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequest req, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            Review r = reviewService.create(u.getId(), req);
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
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
    @GetMapping("/users/{sellerId}/reviews")
    public ResponseEntity<?> sellerReviews(@PathVariable Long sellerId) {
        return ResponseEntity.ok(reviewService.findBySeller(sellerId));
    }
}
