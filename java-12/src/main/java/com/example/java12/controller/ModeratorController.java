package com.example.java12.controller;  // 控制器层包

import com.example.java12.config.AuthInterceptor;  // 拦截器
import com.example.java12.dto.ApiResponse;  // 统一响应
import com.example.java12.model.User;  // 用户实体
import com.example.java12.service.CommentService;  // 评论服务
import com.example.java12.service.PostService;  // 帖子服务
import jakarta.servlet.http.HttpServletRequest;  // HTTP 请求
import org.springframework.web.bind.annotation.*;  // Web 注解

/**
 * 版主管理控制器
 * <p>
 * 提供版主专属接口：删除所管板块的帖子、屏蔽评论、置顶/取消置顶帖子。
 * 所有接口需版主权限或 ADMIN 角色（拦截器已校验 /api/moderator/** 路径权限）。
 * </p>
 */
@RestController
@RequestMapping("/api/moderator")
public class ModeratorController {

    /** 帖子服务 */
    private final PostService postService;
    /** 评论服务 */
    private final CommentService commentService;

    /**
     * 构造器注入
     */
    public ModeratorController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    /**
     * 删除帖子（版主仅能删除所管板块的帖子）
     * <p>
     * 软删除，帖子不再在前台展示但数据库记录保留。
     * </p>
     *
     * @param id      帖子 ID
     * @param request HTTP 请求
     * @return 操作结果
     */
    @DeleteMapping("/posts/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id, HttpServletRequest request) {
        User moderator = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        postService.delete(id, moderator, request.getRemoteAddr());
        return ApiResponse.success("帖子已删除", null);
    }

    /**
     * 屏蔽评论（版主仅能屏蔽所管板块下帖子的评论）
     * <p>
     * 屏蔽后评论内容显示为"[该评论已被屏蔽]"，但评论记录保留。
     * </p>
     *
     * @param id      评论 ID
     * @param request HTTP 请求
     * @return 操作结果
     */
    @PutMapping("/comments/{id}/hide")
    public ApiResponse<Void> hideComment(@PathVariable Long id, HttpServletRequest request) {
        User moderator = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        commentService.hide(id, moderator, request.getRemoteAddr());
        return ApiResponse.success("评论已屏蔽", null);
    }

    /**
     * 置顶/取消置顶帖子（版主仅能操作所管板块的帖子）
     *
     * @param id      帖子 ID
     * @param request HTTP 请求
     * @return 操作结果
     */
    @PutMapping("/posts/{id}/top")
    public ApiResponse<Void> toggleTop(@PathVariable Long id, HttpServletRequest request) {
        User moderator = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        postService.toggleTop(id, moderator, request.getRemoteAddr());
        return ApiResponse.success("操作成功", null);
    }
}
