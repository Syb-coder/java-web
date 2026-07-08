package com.example.java3.controller;

import com.example.java3.dto.*;
import com.example.java3.model.AdminUser;
import com.example.java3.model.ProductAuditStatus;
import com.example.java3.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员后台控制器
 * <p>
 * 集中处理管理员相关写操作：用户管理、商品审核、订单纠纷、公告管理、
 * 分类管理、反馈处理、数据可视化。
 * 拦截器已对 /api/admin/** 写操作要求管理员登录态。
 * </p>
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final AnnouncementService announcementService;
    private final FeedbackService feedbackService;
    private final StatsService statsService;

    public AdminController(UserService userService,
                           ProductService productService,
                           CategoryService categoryService,
                           OrderService orderService,
                           AnnouncementService announcementService,
                           FeedbackService feedbackService,
                           StatsService statsService) {
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.announcementService = announcementService;
        this.feedbackService = feedbackService;
        this.statsService = statsService;
    }

    // ===== 用户管理 =====

    /**
     * 查询全部学生
     *
     * @param keyword 关键词（可选）
     * @return 用户列表
     */
    @GetMapping("/users")
    public ResponseEntity<?> users(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return ResponseEntity.ok(userService.search(keyword).stream()
                    .map(userService::toResponse)
                    .toList());
        }
        return ResponseEntity.ok(userService.findAll().stream()
                .map(userService::toResponse)
                .toList());
    }

    /**
     * 封禁用户
     *
     * @param id 用户 ID
     * @return 空响应
     */
    @PostMapping("/users/{id}/ban")
    public ResponseEntity<?> ban(@PathVariable Long id) {
        try {
            userService.ban(id);
            return ResponseEntity.ok(Map.of("message", "已封禁"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 解封用户
     *
     * @param id 用户 ID
     * @return 空响应
     */
    @PostMapping("/users/{id}/unban")
    public ResponseEntity<?> unban(@PathVariable Long id) {
        try {
            userService.unban(id);
            return ResponseEntity.ok(Map.of("message", "已解封"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ===== 商品审核 =====

    /**
     * 查询全部商品
     *
     * @param status 审核状态（可选）
     * @return 商品列表
     */
    @GetMapping("/products")
    public ResponseEntity<?> products(@RequestParam(required = false) String status) {
        if ("PENDING".equalsIgnoreCase(status)) {
            return ResponseEntity.ok(productService.pendingList());
        }
        return ResponseEntity.ok(productService.all());
    }

    /**
     * 审核商品
     *
     * @param id  商品 ID
     * @param req 审核请求
     * @return 空响应
     */
    @PostMapping("/products/{id}/audit")
    public ResponseEntity<?> audit(@PathVariable Long id, @RequestBody AuditRequest req) {
        try {
            ProductAuditStatus status = ProductAuditStatus.valueOf(req.getStatus().toUpperCase());
            if (status == ProductAuditStatus.REJECTED && (req.getRemark() == null || req.getRemark().isBlank())) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(Map.of("error", "驳回需填写原因"));
            }
            productService.audit(id, status, req.getRemark());
            return ResponseEntity.ok(Map.of("message", "审核完成"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 强制下架商品
     *
     * @param id 商品 ID
     * @return 空响应
     */
    @PostMapping("/products/{id}/takeDown")
    public ResponseEntity<?> takeDown(@PathVariable Long id) {
        try {
            productService.takeDown(id);
            return ResponseEntity.ok(Map.of("message", "已下架"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ===== 分类管理 =====

    /**
     * 新增分类
     *
     * @param req 分类请求
     * @return 分类响应
     */
    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest req) {
        try {
            return ResponseEntity.ok(categoryService.create(req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 更新分类
     *
     * @param id  分类 ID
     * @param req 分类请求
     * @return 分类响应
     */
    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) {
        try {
            return ResponseEntity.ok(categoryService.update(id, req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 删除分类
     *
     * @param id 分类 ID
     * @return 空响应
     */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(Map.of("message", "已删除"));
    }

    // ===== 订单管理 =====

    /**
     * 查询全部订单
     *
     * @return 订单列表
     */
    @GetMapping("/orders")
    public ResponseEntity<?> orders() {
        return ResponseEntity.ok(orderService.all());
    }

    /**
     * 介入纠纷处理
     *
     * @param id  订单 ID
     * @param req 纠纷请求
     * @return 订单响应
     */
    @PostMapping("/orders/{id}/dispute")
    public ResponseEntity<?> dispute(@PathVariable Long id, @RequestBody DisputeRequest req) {
        try {
            return ResponseEntity.ok(orderService.handleDispute(id, req.getRemark()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ===== 公告管理 =====

    /**
     * 发布公告
     *
     * @param req     公告请求
     * @param session 会话
     * @return 公告响应
     */
    @PostMapping("/announcements")
    public ResponseEntity<?> createAnnouncement(@Valid @RequestBody AnnouncementRequest req,
                                                HttpSession session) {
        AdminUser admin = currentAdmin(session);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        return ResponseEntity.ok(announcementService.create(admin.getId(), req));
    }

    /**
     * 更新公告
     *
     * @param id  公告 ID
     * @param req 公告请求
     * @return 公告响应
     */
    @PutMapping("/announcements/{id}")
    public ResponseEntity<?> updateAnnouncement(@PathVariable Long id,
                                                @Valid @RequestBody AnnouncementRequest req) {
        try {
            return ResponseEntity.ok(announcementService.update(id, req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 删除公告
     *
     * @param id 公告 ID
     * @return 空响应
     */
    @DeleteMapping("/announcements/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.ok(Map.of("message", "已删除"));
    }

    // ===== 反馈管理 =====

    /**
     * 查询全部反馈
     *
     * @return 反馈列表
     */
    @GetMapping("/feedbacks")
    public ResponseEntity<?> feedbacks() {
        return ResponseEntity.ok(feedbackService.findAll());
    }

    /**
     * 回复反馈
     *
     * @param id  反馈 ID
     * @param req 回复请求
     * @return 空响应
     */
    @PostMapping("/feedbacks/{id}/reply")
    public ResponseEntity<?> replyFeedback(@PathVariable Long id,
                                           @Valid @RequestBody FeedbackReplyRequest req) {
        try {
            feedbackService.reply(id, req);
            return ResponseEntity.ok(Map.of("message", "已回复"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ===== 数据可视化 =====

    /**
     * 平台整体统计
     *
     * @return 统计响应
     */
    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        return ResponseEntity.ok(statsService.overview());
    }

    /**
     * 热门品类统计
     *
     * @return 品类统计列表
     */
    @GetMapping("/stats/hot-categories")
    public ResponseEntity<?> hotCategories() {
        return ResponseEntity.ok(statsService.hotCategories());
    }

    /**
     * 月度交易量统计
     *
     * @return 月度统计列表
     */
    @GetMapping("/stats/monthly-orders")
    public ResponseEntity<?> monthlyOrders() {
        return ResponseEntity.ok(statsService.monthlyOrders());
    }

    /**
     * 获取当前会话中的管理员
     *
     * @param session 会话
     * @return 管理员，未登录返回 null
     */
    private AdminUser currentAdmin(HttpSession session) {
        Object obj = session.getAttribute(AuthController.SESSION_ADMIN_KEY);
        return obj instanceof AdminUser ? (AdminUser) obj : null;
    }
}
