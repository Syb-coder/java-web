package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.dto.LoginRequest;
import com.example.java11.dto.LoginResponse;
import com.example.java11.dto.RegisterRequest;
import com.example.java11.model.AdminUser;
import com.example.java11.model.User;
import com.example.java11.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * <p>
 * 提供用户注册、登录、登出及管理员登录接口。
 * 登录态通过 HttpSession 维护，{@link LoginInterceptor} 通过本类的常量读取 Session。
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** Session 中存储当前登录用户的 key（供 LoginInterceptor 引用） */
    public static final String SESSION_USER_KEY = AuthService.SESSION_USER_KEY;

    /** Session 中存储当前登录管理员的 key（供 LoginInterceptor 引用） */
    public static final String SESSION_ADMIN_KEY = AuthService.SESSION_ADMIN_KEY;

    /** 认证服务 */
    private final AuthService authService;

    /**
     * 构造器注入依赖
     *
     * @param authService 认证服务
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册
     * <p>
     * 校验请求参数后创建用户，注册成功返回登录响应。
     * </p>
     *
     * @param req 注册请求 DTO
     * @return 包含登录响应的统一包装结果
     */
    @PostMapping("/register")
    public ApiResponse register(@Valid @RequestBody RegisterRequest req) {
        LoginResponse response = authService.register(req);
        return ApiResponse.success("注册成功", response);
    }

    /**
     * 用户登录
     *
     * @param req     登录请求 DTO
     * @param session HTTP 会话，用于写入登录态
     * @return 包含登录响应的统一包装结果
     */
    @PostMapping("/login")
    public ApiResponse login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        LoginResponse response = authService.login(req, session);
        return ApiResponse.success("登录成功", response);
    }

    /**
     * 管理员登录
     *
     * @param req     登录请求 DTO
     * @param session HTTP 会话，用于写入管理员登录态
     * @return 包含登录响应的统一包装结果
     */
    @PostMapping("/admin/login")
    public ApiResponse adminLogin(@Valid @RequestBody LoginRequest req, HttpSession session) {
        LoginResponse response = authService.adminLogin(req, session);
        return ApiResponse.success("登录成功", response);
    }

    /**
     * 登出
     * <p>
     * 失效当前 session，清除登录态。
     * </p>
     *
     * @param session HTTP 会话
     * @return 统一包装结果
     */
    @PostMapping("/logout")
    public ApiResponse logout(HttpSession session) {
        authService.logout(session);
        return ApiResponse.success("已登出", null);
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 从 session 读取当前登录用户，未登录时返回错误提示。
     * </p>
     *
     * @param session HTTP 会话
     * @return 包含用户实体的统一包装结果，未登录返回 error
     */
    @GetMapping("/me")
    public ApiResponse me(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        return ApiResponse.success(currentUser);
    }

    /**
     * 获取当前登录管理员信息
     * <p>
     * 从 session 读取管理员登录态，供管理后台前端自动检测登录状态。
     * 未登录时返回错误提示，前端据此显示登录页。
     * </p>
     *
     * @param session HTTP 会话
     * @return 包含管理员实体的统一包装结果，未登录返回 error
     */
    @GetMapping("/admin/me")
    public ApiResponse adminMe(HttpSession session) {
        AdminUser admin = authService.getCurrentAdmin(session);
        if (admin == null) {
            return ApiResponse.error("未登录");
        }
        // 清除密码字段后返回，避免敏感信息泄露
        admin.setPassword(null);
        return ApiResponse.success(admin);
    }
}
