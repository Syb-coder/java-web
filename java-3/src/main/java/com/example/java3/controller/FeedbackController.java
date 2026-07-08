// 声明当前类所在的包路径
package com.example.java3.controller;

// 导入反馈请求 DTO
import com.example.java3.dto.FeedbackRequest;
// 导入反馈实体
import com.example.java3.model.Feedback;
// 导入学生用户实体
import com.example.java3.model.User;
// 导入反馈服务
import com.example.java3.service.FeedbackService;
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

// 导入 Map
import java.util.Map;

/**
 * 意见反馈控制器（前台）
 */
// @RestController：REST 控制器，返回 JSON
@RestController
// @RequestMapping("/api/feedbacks")：基础路径 /api/feedbacks
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    // 注入的反馈服务
    private final FeedbackService feedbackService;

    // 构造器注入反馈服务
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * 提交反馈
     *
     * @param req     反馈请求
     * @param session 会话
     * @return 反馈实体
     */
    // @PostMapping：处理 POST /api/feedbacks 请求
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody FeedbackRequest req, HttpSession session) {
        // 从会话获取当前学生
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 调用服务提交反馈，关联用户 ID
        Feedback f = feedbackService.create(u.getId(), req);
        // 返回 200 OK 和反馈实体
        return ResponseEntity.ok(f);
    }

    /**
     * 查询我的反馈
     *
     * @param session 会话
     * @return 反馈列表
     */
    // @GetMapping("/mine")：处理 GET /api/feedbacks/mine 请求
    @GetMapping("/mine")
    public ResponseEntity<?> mine(HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 返回 200 OK 和当前用户提交的反馈列表
        return ResponseEntity.ok(feedbackService.findByUser(u.getId()));
    }
}
