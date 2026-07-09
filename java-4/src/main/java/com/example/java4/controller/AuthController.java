// 声明包路径，存放控制器层
package com.example.java4.controller;

// 导入 DTO 类
import com.example.java4.dto.ChangePasswordRequest;
import com.example.java4.dto.LoginRequest;
import com.example.java4.dto.LoginResponse;
import com.example.java4.dto.RegisterRequest;

// 导入 Service
import com.example.java4.service.AuthService;

// 导入 Spring 与 Servlet 工具
import com.example.java4.config.LoginInterceptor;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * <p>
 * 统一处理读者端与管理端的登录、注册、登出、修改密码等认证请求。
 * </p>
 * <p>
 * API 路径设计：
 * <ul>
 *   <li>POST /api/auth/login - 统一登录（loginType 区分 reader/admin）</li>
 *   <li>POST /api/auth/register - 读者注册</li>
 *   <li>POST /api/auth/logout - 登出</li>
 *   <li>GET /api/auth/me - 获取当前登录用户信息</li>
 *   <li>POST /api/auth/change-password - 修改密码</li>
 * </ul>
 * </p>
 */
@RestController // 声明为 RESTful 控制器，返回 JSON
@RequestMapping("/api/auth") // 统一路径前缀
public class AuthController {

    /** 认证服务 */
    private final AuthService authService;

    /**
     * 构造方法注入依赖
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 统一登录接口
     * <p>
     * 根据 loginType 分发到读者登录或管理员登录，登录成功后写入 Session。
     * </p>
     *
     * @param req 登录请求
     * @param session HTTP 会话
     * @return 登录响应（200 成功，401 账号或密码错误）
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        LoginResponse resp = authService.login(req);
        if (resp == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 将用户 ID 与角色写入 Session，建立登录态
        session.setAttribute(LoginInterceptor.SESSION_USER_ID_KEY, resp.getId());
        session.setAttribute(LoginInterceptor.SESSION_USER_ROLE_KEY, resp.getRole());
        return ResponseEntity.ok(resp);
    }

    /**
     * 读者注册接口
     *
     * @param req 注册请求
     * @return 注册成功返回 200，参数不合法返回 400
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            authService.register(req);
            // 注册成功返回提示信息
            Map<String, String> result = new HashMap<>();
            result.put("message", "注册成功，请登录");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            // 参数不合法（如学号已注册），返回 400
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * 登出接口
     * <p>
     * 清除 Session 中的登录信息，使当前会话失效。
     * </p>
     *
     * @param session HTTP 会话
     * @return 200 成功
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // 使会话失效
        Map<String, String> result = new HashMap<>();
        result.put("message", "已退出登录");
        return ResponseEntity.ok(result);
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 从 Session 读取用户 ID 与角色，返回对应的用户信息。
     * </p>
     *
     * @param session HTTP 会话
     * @return 用户信息（200 已登录，401 未登录）
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object userId = session.getAttribute(LoginInterceptor.SESSION_USER_ID_KEY);
        Object role = session.getAttribute(LoginInterceptor.SESSION_USER_ROLE_KEY);
        // 未登录
        if (userId == null || role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 根据角色返回对应信息
        Map<String, Object> result = new HashMap<>();
        result.put("id", userId);
        result.put("role", role);
        if ("reader".equals(role)) {
            // 读者返回读者信息
            var reader = authService.getReaderById((Long) userId);
            if (reader != null) {
                result.put("username", reader.getReaderNo());
                result.put("displayName", reader.getName());
                result.put("readerType", reader.getType().name());
                result.put("department", reader.getDepartment());
            }
        } else if ("admin".equals(role)) {
            // 管理员返回管理员信息
            var admin = authService.getAdminById((Long) userId);
            if (admin != null) {
                result.put("username", admin.getUsername());
                result.put("displayName", admin.getRealName());
            }
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 修改密码接口
     * <p>
     * 根据当前登录角色（reader/admin）调用对应的修改密码方法。
     * 修改成功后自动清除 Session，要求重新登录。
     * </p>
     *
     * @param req     修改密码请求
     * @param session HTTP 会话
     * @return 200 修改成功，400 参数不合法，401 未登录或旧密码错误
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest req, HttpSession session) {
        Object userId = session.getAttribute(LoginInterceptor.SESSION_USER_ID_KEY);
        Object role = session.getAttribute(LoginInterceptor.SESSION_USER_ROLE_KEY);
        // 未登录
        if (userId == null || role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            boolean success;
            if ("reader".equals(role)) {
                success = authService.changeReaderPassword((Long) userId, req);
            } else {
                success = authService.changeAdminPassword((Long) userId, req);
            }
            if (!success) {
                // 旧密码错误
                Map<String, String> error = new HashMap<>();
                error.put("error", "旧密码错误");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            // 修改成功后清除 Session，要求重新登录
            session.invalidate();
            Map<String, String> result = new HashMap<>();
            result.put("message", "密码修改成功，请重新登录");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            // 参数不合法（如新旧密码相同）
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
