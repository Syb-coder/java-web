package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.dto.CommentRequest;
import com.example.java11.dto.CommentResponse;
import com.example.java11.model.User;
import com.example.java11.service.AuthService;
import com.example.java11.service.CommentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 * <p>
 * 提供评论发表、查询与删除接口。
 * 发表评论需登录，查询接口允许匿名访问。
 * </p>
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    /** 评论服务 */
    private final CommentService commentService;

    /** 认证服务，用于获取当前登录用户 */
    private final AuthService authService;

    /**
     * 构造器注入依赖
     *
     * @param commentService 评论服务
     * @param authService    认证服务
     */
    public CommentController(CommentService commentService, AuthService authService) {
        this.commentService = commentService;
        this.authService = authService;
    }

    /**
     * 发表评论
     *
     * @param req     评论请求 DTO
     * @param session HTTP 会话
     * @return 包含评论响应的统一包装结果，未登录返回 error
     */
    @PostMapping
    public ApiResponse createComment(@Valid @RequestBody CommentRequest req, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        CommentResponse response = commentService.createComment(currentUser.getId(), req);
        return ApiResponse.success("评论成功", response);
    }

    /**
     * 获取帖子评论列表
     *
     * @param postId  帖子 ID
     * @param session HTTP 会话
     * @return 评论响应列表
     */
    @GetMapping("/post/{postId}")
    public ApiResponse getCommentsByPost(@PathVariable Long postId, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        Long currentUserId = currentUser == null ? null : currentUser.getId();
        List<CommentResponse> list = commentService.getCommentsByPost(postId, currentUserId);
        return ApiResponse.success(list);
    }

    /**
     * 获取用户评论历史
     *
     * @param userId 用户 ID
     * @return 评论响应列表
     */
    @GetMapping("/user/{userId}")
    public ApiResponse getCommentsByUser(@PathVariable Long userId) {
        List<CommentResponse> list = commentService.getCommentsByUser(userId);
        return ApiResponse.success(list);
    }

    /**
     * 删除评论（逻辑删除）
     *
     * @param id 评论 ID
     * @return 统一包装结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ApiResponse.success("评论已删除", null);
    }
}
