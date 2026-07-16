package com.example.java12.controller;  // 控制器层包

import com.example.java12.config.AuthInterceptor;  // 拦截器
import com.example.java12.dto.ApiResponse;  // 统一响应
import com.example.java12.dto.CommentRequest;  // 评论请求
import com.example.java12.dto.CommentResponse;  // 评论响应
import com.example.java12.model.User;  // 用户实体
import com.example.java12.service.CommentService;  // 评论服务
import jakarta.servlet.http.HttpServletRequest;  // HTTP 请求
import jakarta.validation.Valid;  // 参数校验
import org.springframework.web.bind.annotation.*;  // Web 注解

import java.util.List;  // 列表

/**
 * 评论控制器
 * <p>
 * 提供评论查询、发表、删除接口。
 * </p>
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    /** 评论服务 */
    private final CommentService commentService;

    /**
     * 构造器注入
     */
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 查询帖子的评论列表（公开接口）
     *
     * @param postId 帖子 ID
     * @return 评论列表
     */
    @GetMapping
    public ApiResponse<List<CommentResponse>> findByPostId(@RequestParam Long postId) {
        return ApiResponse.success(commentService.findByPostId(postId));
    }

    /**
     * 发表评论（需登录）
     *
     * @param req     评论请求
     * @param request HTTP 请求
     * @return 新发表的评论
     */
    @PostMapping
    public ApiResponse<CommentResponse> create(@Valid @RequestBody CommentRequest req,
                                                HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        return ApiResponse.success("评论成功", commentService.create(req, currentUser));
    }

    /**
     * 删除评论（需登录，仅作者或管理员可删除）
     *
     * @param id      评论 ID
     * @param request HTTP 请求
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        commentService.delete(id, currentUser);
        return ApiResponse.success("评论已删除", null);
    }
}
