package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.dto.NotificationResponse;
import com.example.java11.model.User;
import com.example.java11.service.AuthService;
import com.example.java11.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知控制器
 * <p>
 * 提供站内通知的列表、未读数查询与已读标记接口。
 * 所有接口均需登录，从 session 获取当前用户 ID。
 * </p>
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    /** 通知服务 */
    private final NotificationService notificationService;

    /** 认证服务，用于获取当前登录用户 */
    private final AuthService authService;

    /**
     * 构造器注入依赖
     *
     * @param notificationService 通知服务
     * @param authService         认证服务
     */
    public NotificationController(NotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;
    }

    /**
     * 获取当前用户通知列表
     *
     * @param session HTTP 会话
     * @return 通知响应列表，未登录返回 error
     */
    @GetMapping
    public ApiResponse getNotifications(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        List<NotificationResponse> list = notificationService.getNotifications(currentUser.getId());
        return ApiResponse.success(list);
    }

    /**
     * 获取未读通知数
     *
     * @param session HTTP 会话
     * @return 未读通知数量，未登录返回 error
     */
    @GetMapping("/unread-count")
    public ApiResponse getUnreadCount(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        long count = notificationService.getUnreadCount(currentUser.getId());
        return ApiResponse.success(count);
    }

    /**
     * 标记单条通知为已读
     *
     * @param id 通知 ID
     * @return 统一包装结果
     */
    @PutMapping("/{id}/read")
    public ApiResponse markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.success("已标记为已读", null);
    }

    /**
     * 将当前用户所有未读通知标记为已读
     *
     * @param session HTTP 会话
     * @return 统一包装结果，未登录返回 error
     */
    @PutMapping("/read-all")
    public ApiResponse markAllAsRead(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        notificationService.markAllAsRead(currentUser.getId());
        return ApiResponse.success("全部已标记为已读", null);
    }
}
