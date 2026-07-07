package com.example.java6.controller;

import com.example.java6.dto.ChangePasswordRequest;
import com.example.java6.dto.LoginRequest;
import com.example.java6.dto.LoginResponse;
import com.example.java6.model.AdminUser;
import com.example.java6.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * 认证控制器
 *
 * <p>提供管理员登录、登出、当前用户信息接口。
 * 登录成功后将管理员信息写入 HttpSession，由 LoginInterceptor 校验登录态。</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** Session 中存储当前登录管理员的键名 */
    public static final String SESSION_USER_KEY = "adminUser";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 管理员登录
     *
     * @param req     登录请求
     * @param session HTTP 会话
     * @return 登录响应（成功）或 401（失败）
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req, HttpSession session) {
        LoginResponse resp = authService.login(req);
        if (resp == null) {
            return ResponseEntity.status(401).build();
        }
        // 写入 session 标记登录态
        AdminUser user = authService.getByUsername(req.username());
        if (user != null) {
            session.setAttribute(SESSION_USER_KEY, user);
        }
        return ResponseEntity.ok(resp);
    }

    /**
     * 管理员登出
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
     * 获取当前登录管理员信息（用于前端校验登录态）
     *
     * @param session HTTP 会话
     * @return 登录响应（已登录）或 401（未登录）
     */
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> me(HttpSession session) {
        Object user = session.getAttribute(SESSION_USER_KEY);
        if (user instanceof AdminUser adminUser) {
            return ResponseEntity.ok(LoginResponse.from(adminUser));
        }
        return ResponseEntity.status(401).build();
    }

    /**
     * 修改当前登录管理员的密码
     *
     * @param req     修改密码请求（含旧密码、新密码）
     * @param admin   当前登录管理员（由 Session 注入）
     * @return 200（成功） / 400（参数不合规） / 401（未登录） / 422（旧密码错误）
     */
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest req,
            @SessionAttribute(value = SESSION_USER_KEY, required = false) AdminUser admin) {
        // 未登录拦截
        if (admin == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            boolean ok = authService.changePassword(admin.getId(), req);
            if (ok) {
                return ResponseEntity.ok().build();
            }
            // 旧密码错误
            return ResponseEntity.status(422).build();
        } catch (IllegalArgumentException e) {
            // 新密码不合规
            return ResponseEntity.badRequest().build();
        }
    }
}
