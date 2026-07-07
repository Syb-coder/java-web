package com.example.java8.controller;

import com.example.java8.dto.LoginRequest;
import com.example.java8.dto.LoginResponse;
import com.example.java8.dto.RegisterRequest;
import com.example.java8.dto.UserResponse;
import com.example.java8.model.User;
import com.example.java8.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * 前台用户控制器
 * <p>提供用户注册、登录、登出、当前用户信息接口。</p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /** Session 中存储当前登录前台用户的键名 */
    public static final String SESSION_USER_KEY = "frontUser";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     *
     * @param req 注册请求
     * @return 201（成功） / 400（参数不合规或用户名已存在）
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest req) {
        try {
            userService.register(req);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 用户登录
     *
     * @param req     登录请求
     * @param session HTTP 会话
     * @return 登录响应（成功）或 401（失败）
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req, HttpSession session) {
        LoginResponse resp = userService.login(req);
        if (resp == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getById(resp.id());
        if (user != null) {
            session.setAttribute(SESSION_USER_KEY, user);
        }
        return ResponseEntity.ok(resp);
    }

    /**
     * 用户登出
     *
     * @param session HTTP 会话
     * @return 204
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取当前登录用户信息
     *
     * @param user 当前登录用户（由 Session 注入）
     * @return 用户响应（已登录）或 401（未登录）
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(
            @SessionAttribute(value = SESSION_USER_KEY, required = false) User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userService.toResponse(user));
    }
}
