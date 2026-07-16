package com.example.java12.controller;  // 控制器层包

import com.example.java12.config.AuthInterceptor;  // 拦截器
import com.example.java12.dto.*;  // 全部 DTO
import com.example.java12.model.Message;  // 消息实体
import com.example.java12.model.User;  // 用户实体
import com.example.java12.service.CommentService;  // 评论服务
import com.example.java12.service.MessageService;  // 消息服务
import com.example.java12.service.PostService;  // 帖子服务
import com.example.java12.service.UserService;  // 用户服务
import jakarta.servlet.http.HttpServletRequest;  // HTTP 请求
import jakarta.validation.Valid;  // 参数校验
import org.springframework.data.domain.Page;  // 分页结果
import org.springframework.web.bind.annotation.*;  // Web 注解

import java.util.List;  // 列表
import java.util.Map;  // Map

/**
 * 用户个人中心控制器
 * <p>
 * 提供个人资料、我的发帖、我的收藏、我的评论、站内消息等接口。
 * 所有接口需登录（拦截器已校验）。
 * </p>
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /** 用户服务 */
    private final UserService userService;
    /** 帖子服务 */
    private final PostService postService;
    /** 评论服务 */
    private final CommentService commentService;
    /** 消息服务 */
    private final MessageService messageService;

    /**
     * 构造器注入
     */
    public UserController(UserService userService, PostService postService,
                          CommentService commentService, MessageService messageService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.messageService = messageService;
    }

    /**
     * 获取个人资料
     */
    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfile(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        return ApiResponse.success(userService.getProfile(currentUser.getId()));
    }

    /**
     * 修改个人资料
     */
    @PutMapping("/profile")
    public ApiResponse<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest req,
                                                    HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        return ApiResponse.success("修改成功", userService.updateProfile(currentUser.getId(), req));
    }

    /**
     * 我的发帖（分页）
     */
    @GetMapping("/posts")
    public ApiResponse<Page<PostResponse>> myPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        return ApiResponse.success(postService.findByUserId(currentUser.getId(), page, size));
    }

    /**
     * 我的收藏
     */
    @GetMapping("/collects")
    public ApiResponse<List<PostResponse>> myCollects(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        return ApiResponse.success(postService.findCollectsByUserId(currentUser.getId()));
    }

    /**
     * 我的评论
     */
    @GetMapping("/comments")
    public ApiResponse<List<CommentResponse>> myComments(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        return ApiResponse.success(commentService.findByUserId(currentUser.getId()));
    }

    /**
     * 站内消息列表
     */
    @GetMapping("/messages")
    public ApiResponse<List<Message>> myMessages(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        return ApiResponse.success(messageService.findByUserId(currentUser.getId()));
    }

    /**
     * 未读消息数
     */
    @GetMapping("/messages/unread-count")
    public ApiResponse<Map<String, Integer>> unreadCount(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        int count = messageService.countUnread(currentUser.getId());
        return ApiResponse.success(Map.of("count", count));
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/messages/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ApiResponse.success("已标记为已读", null);
    }
}
