// 声明当前类所在的包路径
package com.example.java3.controller;

// 导入 dto 包下所有 DTO
import com.example.java3.dto.*;
// 导入管理员实体
import com.example.java3.model.AdminUser;
// 导入商品审核状态枚举（PENDING/APPROVED/REJECTED）
import com.example.java3.model.ProductAuditStatus;
// 导入 service 包下所有服务
import com.example.java3.service.*;
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
 * 管理员后台控制器
 * <p>
 * 集中处理管理员相关写操作：用户管理、商品审核、订单纠纷、公告管理、
 * 分类管理、反馈处理、数据可视化。
 * 拦截器已对 /api/admin/** 写操作要求管理员登录态。
 * </p>
 */
// @RestController：REST 控制器，返回 JSON
@RestController
// @RequestMapping("/api/admin")：基础路径 /api/admin，拦截器对该路径写操作要求管理员登录
@RequestMapping("/api/admin")
public class AdminController {

    // 注入的用户服务
    private final UserService userService;
    // 注入的商品服务
    private final ProductService productService;
    // 注入的分类服务
    private final CategoryService categoryService;
    // 注入的订单服务
    private final OrderService orderService;
    // 注入的公告服务
    private final AnnouncementService announcementService;
    // 注入的反馈服务
    private final FeedbackService feedbackService;
    // 注入的统计服务
    private final StatsService statsService;

    // 构造器注入所有服务依赖
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
    // @GetMapping("/users")：处理 GET /api/admin/users 请求
    // @RequestParam(required = false)：可选查询参数 keyword
    @GetMapping("/users")
    public ResponseEntity<?> users(@RequestParam(required = false) String keyword) {
        // 有关键词时按关键词搜索
        if (keyword != null && !keyword.isBlank()) {
            // 返回 200 OK 和搜索结果（转为 UserResponse）
            return ResponseEntity.ok(userService.search(keyword).stream()
                    .map(userService::toResponse)
                    .toList());
        }
        // 无关键词时返回全部学生
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
    // @PostMapping("/users/{id}/ban")：处理 POST /api/admin/users/{id}/ban 请求
    // @PathVariable：从路径提取用户 ID
    @PostMapping("/users/{id}/ban")
    public ResponseEntity<?> ban(@PathVariable Long id) {
        try {
            // 调用服务封禁用户
            userService.ban(id);
            // 返回 200 OK
            return ResponseEntity.ok(Map.of("message", "已封禁"));
        } catch (IllegalArgumentException e) {
            // 用户不存在，返回 404 Not Found
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
    // @PostMapping("/users/{id}/unban")：处理 POST /api/admin/users/{id}/unban 请求
    @PostMapping("/users/{id}/unban")
    public ResponseEntity<?> unban(@PathVariable Long id) {
        try {
            // 调用服务解封用户
            userService.unban(id);
            // 返回 200 OK
            return ResponseEntity.ok(Map.of("message", "已解封"));
        } catch (IllegalArgumentException e) {
            // 用户不存在，返回 404
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
    // @GetMapping("/products")：处理 GET /api/admin/products 请求
    @GetMapping("/products")
    public ResponseEntity<?> products(@RequestParam(required = false) String status) {
        // 当 status=PENDING 时仅返回待审核商品
        if ("PENDING".equalsIgnoreCase(status)) {
            // 返回 200 OK 和待审核列表
            return ResponseEntity.ok(productService.pendingList());
        }
        // 默认返回全部商品
        return ResponseEntity.ok(productService.all());
    }

    /**
     * 审核商品
     *
     * @param id  商品 ID
     * @param req 审核请求
     * @return 空响应
     */
    // @PostMapping("/products/{id}/audit")：处理 POST /api/admin/products/{id}/audit 请求
    @PostMapping("/products/{id}/audit")
    // @RequestBody：反序列化为 AuditRequest
    public ResponseEntity<?> audit(@PathVariable Long id, @RequestBody AuditRequest req) {
        try {
            // 将字符串状态转为枚举，大写化以容错
            ProductAuditStatus status = ProductAuditStatus.valueOf(req.getStatus().toUpperCase());
            // 驳回时必须填写原因
            if (status == ProductAuditStatus.REJECTED && (req.getRemark() == null || req.getRemark().isBlank())) {
                // 缺少驳回原因，返回 422
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(Map.of("error", "驳回需填写原因"));
            }
            // 调用服务执行审核
            productService.audit(id, status, req.getRemark());
            // 返回 200 OK
            return ResponseEntity.ok(Map.of("message", "审核完成"));
        } catch (IllegalArgumentException e) {
            // 状态非法或商品不存在，返回 422
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
    // @PostMapping("/products/{id}/takeDown")：处理 POST /api/admin/products/{id}/takeDown 请求
    @PostMapping("/products/{id}/takeDown")
    public ResponseEntity<?> takeDown(@PathVariable Long id) {
        try {
            // 调用服务下架商品
            productService.takeDown(id);
            // 返回 200 OK
            return ResponseEntity.ok(Map.of("message", "已下架"));
        } catch (IllegalArgumentException e) {
            // 商品不存在，返回 404
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
    // @PostMapping("/categories")：处理 POST /api/admin/categories 请求
    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest req) {
        try {
            // 调用服务创建分类，返回 200 OK 和分类响应
            return ResponseEntity.ok(categoryService.create(req));
        } catch (IllegalArgumentException e) {
            // 分类名重复等，返回 422
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
    // @PutMapping("/categories/{id}")：处理 PUT /api/admin/categories/{id} 请求
    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) {
        try {
            // 调用服务更新分类
            return ResponseEntity.ok(categoryService.update(id, req));
        } catch (IllegalArgumentException e) {
            // 分类不存在或名称冲突，返回 422
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
    // @DeleteMapping("/categories/{id}")：处理 DELETE /api/admin/categories/{id} 请求
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        // 调用服务删除分类
        categoryService.delete(id);
        // 返回 200 OK
        return ResponseEntity.ok(Map.of("message", "已删除"));
    }

    // ===== 订单管理 =====

    /**
     * 查询全部订单
     *
     * @return 订单列表
     */
    // @GetMapping("/orders")：处理 GET /api/admin/orders 请求
    @GetMapping("/orders")
    public ResponseEntity<?> orders() {
        // 返回 200 OK 和全部订单
        return ResponseEntity.ok(orderService.all());
    }

    /**
     * 介入纠纷处理
     *
     * @param id  订单 ID
     * @param req 纠纷请求
     * @return 订单响应
     */
    // @PostMapping("/orders/{id}/dispute")：处理 POST /api/admin/orders/{id}/dispute 请求
    @PostMapping("/orders/{id}/dispute")
    public ResponseEntity<?> dispute(@PathVariable Long id, @RequestBody DisputeRequest req) {
        try {
            // 调用服务处理纠纷，返回更新后的订单
            return ResponseEntity.ok(orderService.handleDispute(id, req.getRemark()));
        } catch (IllegalArgumentException e) {
            // 订单不存在，返回 404
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
    // @PostMapping("/announcements")：处理 POST /api/admin/announcements 请求
    @PostMapping("/announcements")
    public ResponseEntity<?> createAnnouncement(@Valid @RequestBody AnnouncementRequest req,
                                                HttpSession session) {
        // 从会话获取当前管理员
        AdminUser admin = currentAdmin(session);
        if (admin == null) {
            // 未登录管理员，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 调用服务创建公告，关联管理员 ID
        return ResponseEntity.ok(announcementService.create(admin.getId(), req));
    }

    /**
     * 更新公告
     *
     * @param id  公告 ID
     * @param req 公告请求
     * @return 公告响应
     */
    // @PutMapping("/announcements/{id}")：处理 PUT /api/admin/announcements/{id} 请求
    @PutMapping("/announcements/{id}")
    public ResponseEntity<?> updateAnnouncement(@PathVariable Long id,
                                                @Valid @RequestBody AnnouncementRequest req) {
        try {
            // 调用服务更新公告
            return ResponseEntity.ok(announcementService.update(id, req));
        } catch (IllegalArgumentException e) {
            // 公告不存在，返回 404
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
    // @DeleteMapping("/announcements/{id}")：处理 DELETE /api/admin/announcements/{id} 请求
    @DeleteMapping("/announcements/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long id) {
        // 调用服务删除公告
        announcementService.delete(id);
        // 返回 200 OK
        return ResponseEntity.ok(Map.of("message", "已删除"));
    }

    // ===== 反馈管理 =====

    /**
     * 查询全部反馈
     *
     * @return 反馈列表
     */
    // @GetMapping("/feedbacks")：处理 GET /api/admin/feedbacks 请求
    @GetMapping("/feedbacks")
    public ResponseEntity<?> feedbacks() {
        // 返回 200 OK 和全部反馈
        return ResponseEntity.ok(feedbackService.findAll());
    }

    /**
     * 回复反馈
     *
     * @param id  反馈 ID
     * @param req 回复请求
     * @return 空响应
     */
    // @PostMapping("/feedbacks/{id}/reply")：处理 POST /api/admin/feedbacks/{id}/reply 请求
    @PostMapping("/feedbacks/{id}/reply")
    public ResponseEntity<?> replyFeedback(@PathVariable Long id,
                                           @Valid @RequestBody FeedbackReplyRequest req) {
        try {
            // 调用服务回复反馈
            feedbackService.reply(id, req);
            // 返回 200 OK
            return ResponseEntity.ok(Map.of("message", "已回复"));
        } catch (IllegalArgumentException e) {
            // 反馈不存在，返回 404
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
    // @GetMapping("/stats")：处理 GET /api/admin/stats 请求
    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        // 返回 200 OK 和平台总览统计
        return ResponseEntity.ok(statsService.overview());
    }

    /**
     * 热门品类统计
     *
     * @return 品类统计列表
     */
    // @GetMapping("/stats/hot-categories")：处理 GET /api/admin/stats/hot-categories 请求
    @GetMapping("/stats/hot-categories")
    public ResponseEntity<?> hotCategories() {
        // 返回 200 OK 和热门品类统计
        return ResponseEntity.ok(statsService.hotCategories());
    }

    /**
     * 月度交易量统计
     *
     * @return 月度统计列表
     */
    // @GetMapping("/stats/monthly-orders")：处理 GET /api/admin/stats/monthly-orders 请求
    @GetMapping("/stats/monthly-orders")
    public ResponseEntity<?> monthlyOrders() {
        // 返回 200 OK 和月度交易量统计
        return ResponseEntity.ok(statsService.monthlyOrders());
    }

    /**
     * 获取当前会话中的管理员
     *
     * @param session 会话
     * @return 管理员，未登录返回 null
     */
    // 私有工具方法，从会话读取管理员
    private AdminUser currentAdmin(HttpSession session) {
        // 从会话读取管理员对象（key 为 AuthController.SESSION_ADMIN_KEY）
        Object obj = session.getAttribute(AuthController.SESSION_ADMIN_KEY);
        // instanceof 校验后返回，否则返回 null
        return obj instanceof AdminUser ? (AdminUser) obj : null;
    }
}
