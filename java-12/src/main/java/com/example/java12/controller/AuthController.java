package com.example.java12.controller;  // 控制器层包

import com.example.java12.config.AuthInterceptor;  // 拦截器（获取当前用户 key）
import com.example.java12.dto.*;  // 全部 DTO
import com.example.java12.model.User;  // 用户实体
import com.example.java12.service.AuthService;  // 认证服务
import jakarta.servlet.http.HttpServletRequest;  // HTTP 请求
import jakarta.validation.Valid;  // 参数校验注解
import org.springframework.web.bind.annotation.*;  // Web 注解

/**
 * 认证控制器
 * <p>
 * 提供用户注册、登录、登出、修改密码接口。
 * </p>
 */
@RestController  // 声明为 RESTful 控制器
@RequestMapping("/api/auth")  // 基础路径
public class AuthController {

    /** 认证服务 */
    private final AuthService authService;

    /**
     * 构造器注入
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册
     *
     * @param req 注册请求
     * @return 登录响应（含 Token）
     */
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        LoginResponse response = authService.register(req);
        return ApiResponse.success("注册成功", response);
    }

    /**
     * 用户登录
     *
     * @param req 登录请求
     * @return 登录响应（含 Token）
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse response = authService.login(req);
        return ApiResponse.success("登录成功", response);
    }

    /**
     * 管理员登录
     *
     * @param req 登录请求
     * @return 登录响应
     */
    @PostMapping("/admin/login")
    public ApiResponse<LoginResponse> adminLogin(@Valid @RequestBody LoginRequest req) {
        LoginResponse response = authService.adminLogin(req);
        return ApiResponse.success("登录成功", response);
    }

    /**
     * 登出
     *
     * @param request HTTP 请求（获取 Token）
     * @return 操作结果
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader != null ? authHeader.substring(7) : null;
        authService.logout(token);
        return ApiResponse.success("已退出登录", null);
    }

    /**
     * 修改密码
     * <p>
     * 验证旧密码，更新新密码，修改后自动失效 Token（需重新登录）。
     * </p>
     *
     * @param req     修改密码请求
     * @param request HTTP 请求
     * @return 操作结果
     */
    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req,
                                             HttpServletRequest request) {
        User currentUser = (User) request.getAttribute(AuthInterceptor.CURRENT_USER_KEY);
        String authHeader = request.getHeader("Authorization");
        String token = authHeader != null ? authHeader.substring(7) : null;
        authService.changePassword(currentUser.getId(), req, token);
        return ApiResponse.success("密码修改成功，请重新登录", null);
    }
}
