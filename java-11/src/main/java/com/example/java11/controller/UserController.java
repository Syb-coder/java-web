package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.dto.ChangePasswordRequest;
import com.example.java11.dto.UserProfileRequest;
import com.example.java11.dto.UserResponse;
import com.example.java11.model.User;
import com.example.java11.service.AuthService;
import com.example.java11.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * <p>
 * 提供用户资料查询与更新、密码修改等接口。
 * 涉及当前用户的写操作需从 session 获取登录用户 ID。
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /** 用户服务 */
    private final UserService userService;

    /** 认证服务，用于获取当前登录用户 */
    private final AuthService authService;

    /**
     * 构造器注入依赖
     *
     * @param userService 用户服务
     * @param authService 认证服务
     */
    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * 获取指定用户资料
     *
     * @param id 用户 ID
     * @return 用户资料响应
     */
    @GetMapping("/{id}")
    public ApiResponse getUserProfile(@PathVariable Long id) {
        UserResponse response = userService.getUserProfile(id);
        return ApiResponse.success(response);
    }

    /**
     * 获取当前登录用户资料
     *
     * @param session HTTP 会话
     * @return 当前用户资料响应，未登录返回 error
     */
    @GetMapping("/profile")
    public ApiResponse getProfile(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        UserResponse response = userService.getUserProfile(currentUser.getId());
        return ApiResponse.success(response);
    }

    /**
     * 更新个人资料
     *
     * @param req     资料更新请求 DTO
     * @param session HTTP 会话
     * @return 更新后的用户资料响应，未登录返回 error
     */
    @PutMapping("/profile")
    public ApiResponse updateProfile(@Valid @RequestBody UserProfileRequest req, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        UserResponse response = userService.updateProfile(currentUser.getId(), req);
        return ApiResponse.success("资料更新成功", response);
    }

    /**
     * 修改密码
     *
     * @param req     修改密码请求 DTO
     * @param session HTTP 会话
     * @return 统一包装结果，未登录返回 error
     */
    @PutMapping("/password")
    public ApiResponse changePassword(@Valid @RequestBody ChangePasswordRequest req, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        userService.changePassword(currentUser.getId(), req);
        return ApiResponse.success("密码修改成功", null);
    }
}
