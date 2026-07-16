package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.dto.PostRequest;
import com.example.java11.dto.PostResponse;
import com.example.java11.model.User;
import com.example.java11.service.AuthService;
import com.example.java11.service.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 帖子控制器
 * <p>
 * 提供发帖、帖子详情查询、按板块/热门/关键词/用户查询等接口。
 * 发帖需登录，查询接口允许匿名访问（currentUserId 可为 null）。
 * </p>
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    /** 帖子服务 */
    private final PostService postService;

    /** 认证服务，用于获取当前登录用户 */
    private final AuthService authService;

    /**
     * 构造器注入依赖
     *
     * @param postService 帖子服务
     * @param authService 认证服务
     */
    public PostController(PostService postService, AuthService authService) {
        this.postService = postService;
        this.authService = authService;
    }

    /**
     * 发帖
     *
     * @param req     发帖请求 DTO
     * @param session HTTP 会话
     * @return 包含帖子响应的统一包装结果，未登录返回 error
     */
    @PostMapping
    public ApiResponse createPost(@Valid @RequestBody PostRequest req, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        PostResponse response = postService.createPost(currentUser.getId(), req);
        return ApiResponse.success("发帖成功", response);
    }

    /**
     * 获取帖子详情（匿名可访问）
     *
     * @param id      帖子 ID
     * @param session HTTP 会话
     * @return 帖子详情响应
     */
    @GetMapping("/{id}")
    public ApiResponse getPost(@PathVariable Long id, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        PostResponse response = postService.getPost(id, currentUserId);
        return ApiResponse.success(response);
    }

    /**
     * 按板块获取帖子列表
     *
     * @param sectionId 板块 ID
     * @param session   HTTP 会话
     * @return 帖子响应列表
     */
    @GetMapping("/section/{sectionId}")
    public ApiResponse getPostsBySection(@PathVariable Long sectionId, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        List<PostResponse> list = postService.getPostsBySection(sectionId, currentUserId);
        return ApiResponse.success(list);
    }

    /**
     * 获取热门帖子
     *
     * @param session HTTP 会话
     * @return 热门帖子响应列表
     */
    @GetMapping("/hot")
    public ApiResponse getHotPosts(HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        List<PostResponse> list = postService.getHotPosts(currentUserId);
        return ApiResponse.success(list);
    }

    /**
     * 搜索帖子
     *
     * @param keyword 搜索关键词
     * @param session HTTP 会话
     * @return 匹配的帖子响应列表
     */
    @GetMapping("/search")
    public ApiResponse searchPosts(@RequestParam String keyword, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        List<PostResponse> list = postService.searchPosts(keyword, currentUserId);
        return ApiResponse.success(list);
    }

    /**
     * 获取用户发帖历史
     *
     * @param userId  用户 ID
     * @param session HTTP 会话
     * @return 用户发帖响应列表
     */
    @GetMapping("/user/{userId}")
    public ApiResponse getPostsByUser(@PathVariable Long userId, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        List<PostResponse> list = postService.getPostsByUser(userId, currentUserId);
        return ApiResponse.success(list);
    }

    /**
     * 从 session 获取当前登录用户 ID，未登录返回 null
     *
     * @param session HTTP 会话
     * @return 当前用户 ID，未登录时为 null
     */
    private Long getCurrentUserId(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        return currentUser == null ? null : currentUser.getId();
    }
}
