package com.example.java3.controller;

import com.example.java3.dto.CommentRequest;
import com.example.java3.dto.CommentResponse;
import com.example.java3.model.User;
import com.example.java3.service.CommentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/api/products/{productId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 查询商品评论列表
     *
     * @param productId 商品 ID
     * @return 评论列表
     */
    @GetMapping
    public ResponseEntity<?> list(@PathVariable Long productId) {
        List<CommentResponse> list = commentService.listByProduct(productId);
        return ResponseEntity.ok(list);
    }

    /**
     * 发表评论
     *
     * @param productId 商品 ID
     * @param req       评论请求
     * @param session   会话
     * @return 评论响应
     */
    @PostMapping
    public ResponseEntity<?> create(@PathVariable Long productId,
                                    @Valid @RequestBody CommentRequest req,
                                    HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            return ResponseEntity.ok(commentService.create(productId, u.getId(), req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
