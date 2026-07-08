package com.example.java3.controller;

import com.example.java3.dto.ChangePasswordRequest;
import com.example.java3.dto.LoginRequest;
import com.example.java3.dto.LoginResponse;
import com.example.java3.model.AdminUser;
import com.example.java3.model.User;
import com.example.java3.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * <p>
 * 提供学生/管理员登录、登出、获取当前用户、修改密码接口。
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** 管理员会话 key */
    public static final String SESSION_ADMIN_KEY = "admin";

    /** 学生会话 key */
    public static final String SESSION_USER_KEY = "user";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 学生登录
     *
     * @param req 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        try {
            LoginResponse resp = authService.userLogin(req);
            // 登录成功后，将用户信息写入会话（用于拦截器校验）
            // 仅存 ID 即可，拦截器只校验类型与存在性
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
     * 管理员登录
     *
     * @param req 登录请求
     * @return 登录响应
     */
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginRequest req, HttpSession session) {
        try {
            LoginResponse resp = authService.adminLogin(req);
            // 仅存 ID 即可，拦截器只校验类型与存在性
            AdminUser sessionAdmin = new AdminUser();
            sessionAdmin.setId(resp.getId());
            session.setAttribute(SESSION_ADMIN_KEY, sessionAdmin);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 登出
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
     * 获取当前登录用户信息
     *
     * @param session 会话
     * @return 用户信息 map
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object user = session.getAttribute(SESSION_USER_KEY);
        Object admin = session.getAttribute(SESSION_ADMIN_KEY);
        if (admin instanceof AdminUser a) {
            return ResponseEntity.ok(Map.of(
                    "role", "ADMIN",
                    "id", a.getId(),
                    "username", a.getUsername(),
                    "displayName", a.getDisplayName()
            ));
        }
        if (user instanceof User u) {
            return ResponseEntity.ok(Map.of(
                    "role", "USER",
                    "id", u.getId(),
                    "username", u.getUsername(),
                    "nickname", u.getNickname()
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "未登录"));
    }

    /**
     * 学生修改密码
     *
     * @param req     修改密码请求
     * @param session 会话
     * @return 空响应
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest req,
                                            HttpSession session) {
        Object user = session.getAttribute(SESSION_USER_KEY);
        Object admin = session.getAttribute(SESSION_ADMIN_KEY);
        try {
            if (admin instanceof AdminUser a) {
                authService.changeAdminPassword(a.getId(), req);
            } else if (user instanceof User u) {
                authService.changeUserPassword(u.getId(), req);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "未登录"));
            }
            // 修改密码后使会话失效，强制重新登录
            session.invalidate();
            return ResponseEntity.ok(Map.of("message", "密码修改成功，请重新登录"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
