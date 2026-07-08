package com.example.java3.controller;

import com.example.java3.dto.*;
import com.example.java3.model.User;
import com.example.java3.service.AuthService;
import com.example.java3.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生用户控制器
 * <p>
 * 提供注册、登录、登出、个人信息、收藏列表等接口。
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /** 学生会话 key（与 AuthController 保持一致） */
    public static final String SESSION_USER_KEY = "user";

    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * 学生注册
     *
     * @param req 注册请求
     * @return 空响应
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            authService.register(req);
            return ResponseEntity.ok(Map.of("message", "注册成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 学生登录（兼容 /api/users/login 路径，实际调用 /api/auth/login）
     *
     * @param req 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        try {
            LoginResponse resp = authService.userLogin(req);
            User sessionUser = new User();
            sessionUser.setId(resp.getId());
            session.setAttribute(SESSION_USER_KEY, sessionUser);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 学生登出
     *
     * @param session 会话
     * @return 空响应
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "已登出"));
    }

    /**
     * 获取个人信息
     *
     * @param session 会话
     * @return 用户信息
     */
    @GetMapping("/profile")
    public ResponseEntity<?> profile(HttpSession session) {
        User u = currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        return ResponseEntity.ok(userService.getProfile(u.getId()));
    }

    /**
     * 更新个人信息
     *
     * @param req     资料请求
     * @param session 会话
     * @return 更新后的用户信息
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserProfileRequest req, HttpSession session) {
        User u = currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        return ResponseEntity.ok(userService.updateProfile(u.getId(), req));
    }

    /**
     * 获取当前会话中的学生用户
     *
     * @param session 会话
     * @return 学生用户，未登录返回 null
     */
    public static User currentUser(HttpSession session) {
        Object obj = session.getAttribute(SESSION_USER_KEY);
        return obj instanceof User ? (User) obj : null;
    }
}
