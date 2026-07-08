package com.example.java3.controller;

import com.example.java3.dto.FeedbackRequest;
import com.example.java3.model.Feedback;
import com.example.java3.model.User;
import com.example.java3.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 意见反馈控制器（前台）
 */
@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

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
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody FeedbackRequest req, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        Feedback f = feedbackService.create(u.getId(), req);
        return ResponseEntity.ok(f);
    }

    /**
     * 查询我的反馈
     *
     * @param session 会话
     * @return 反馈列表
     */
    @GetMapping("/mine")
    public ResponseEntity<?> mine(HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        return ResponseEntity.ok(feedbackService.findByUser(u.getId()));
    }
}
