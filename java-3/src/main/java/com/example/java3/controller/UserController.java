// 声明当前类所在的包路径
package com.example.java3.controller;

// 导入 dto 包下所有 DTO（RegisterRequest、LoginRequest、LoginResponse、UserProfileRequest、UserResponse）
import com.example.java3.dto.*;
// 导入学生用户实体
import com.example.java3.model.User;
// 导入认证服务
import com.example.java3.service.AuthService;
// 导入用户服务
import com.example.java3.service.UserService;
// 导入 HttpSession
import jakarta.servlet.http.HttpSession;
// 导入 @Valid
import jakarta.validation.Valid;
// 导入 HttpStatus
import org.springframework.http.HttpStatus;
// 导入 ResponseEntity
import org.springframework.http.ResponseEntity;
// 导入 Spring MVC 注解
import org.springframework.web.bind.annotation.*;

// 导入 List（本类未直接使用，保留以备扩展）
import java.util.List;
// 导入 Map，构建 JSON 响应体
import java.util.Map;

/**
 * 学生用户控制器
 * <p>
 * 提供注册、登录、登出、个人信息、收藏列表等接口。
 * </p>
 */
// @RestController：REST 控制器，返回 JSON
@RestController
// @RequestMapping("/api/users")：基础路径 /api/users
@RequestMapping("/api/users")
public class UserController {

    /** 学生会话 key（与 AuthController 保持一致） */
    public static final String SESSION_USER_KEY = "user";

    // 注入的认证服务
    private final AuthService authService;
    // 注入的用户服务
    private final UserService userService;

    // 构造器注入两个服务
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
    // @PostMapping("/register")：处理 POST /api/users/register 请求
    @PostMapping("/register")
    // @Valid @RequestBody：校验并反序列化请求体为 RegisterRequest
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            // 调用认证服务完成注册
            authService.register(req);
            // 返回 200 OK
            return ResponseEntity.ok(Map.of("message", "注册成功"));
        } catch (IllegalArgumentException e) {
            // 注册失败（学号重复、用户名重复等），返回 422
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
    // @PostMapping("/login")：处理 POST /api/users/login 请求（与 /api/auth/login 等价）
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        try {
            // 调用认证服务校验账号密码
            LoginResponse resp = authService.userLogin(req);
            // 创建会话用户对象，仅存 ID
            User sessionUser = new User();
            sessionUser.setId(resp.getId());
            // 写入会话
            session.setAttribute(SESSION_USER_KEY, sessionUser);
            // 返回 200 OK 和登录响应
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            // 登录失败，返回 422
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
    // @PostMapping("/logout")：处理 POST /api/users/logout 请求
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        // 使会话失效
        session.invalidate();
        // 返回 200 OK
        return ResponseEntity.ok(Map.of("message", "已登出"));
    }

    /**
     * 获取个人信息
     *
     * @param session 会话
     * @return 用户信息
     */
    // @GetMapping("/profile")：处理 GET /api/users/profile 请求
    @GetMapping("/profile")
    public ResponseEntity<?> profile(HttpSession session) {
        // 从会话获取当前学生
        User u = currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 返回 200 OK 和个人资料
        return ResponseEntity.ok(userService.getProfile(u.getId()));
    }

    /**
     * 更新个人信息
     *
     * @param req     资料请求
     * @param session 会话
     * @return 更新后的用户信息
     */
    // @PutMapping("/profile")：处理 PUT /api/users/profile 请求
    @PutMapping("/profile")
    // @RequestBody：反序列化请求体为 UserProfileRequest（此处未加 @Valid，因 profile 更新字段较宽松）
    public ResponseEntity<?> updateProfile(@RequestBody UserProfileRequest req, HttpSession session) {
        User u = currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 返回 200 OK 和更新后的资料
        return ResponseEntity.ok(userService.updateProfile(u.getId(), req));
    }

    /**
     * 获取当前会话中的学生用户
     *
     * @param session 会话
     * @return 学生用户，未登录返回 null
     */
    // 静态工具方法，供其他 Controller 复用（如 ProductController、OrderController）
    public static User currentUser(HttpSession session) {
        // 从会话读取用户对象
        Object obj = session.getAttribute(SESSION_USER_KEY);
        // instanceof 校验类型后返回，否则返回 null
        return obj instanceof User ? (User) obj : null;
    }
}
