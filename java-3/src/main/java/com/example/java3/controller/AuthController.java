// 声明当前类所在的包路径
package com.example.java3.controller;

// 导入修改密码请求 DTO
import com.example.java3.dto.ChangePasswordRequest;
// 导入登录请求 DTO
import com.example.java3.dto.LoginRequest;
// 导入登录响应 DTO
import com.example.java3.dto.LoginResponse;
// 导入管理员实体，用于会话存储与 instanceof 校验
import com.example.java3.model.AdminUser;
// 导入学生用户实体，用于会话存储与 instanceof 校验
import com.example.java3.model.User;
// 导入认证服务，处理登录与改密业务
import com.example.java3.service.AuthService;
// 导入 HttpSession，用于读写会话登录态
import jakarta.servlet.http.HttpSession;
// 导入 @Valid，触发 Bean Validation 校验
import jakarta.validation.Valid;
// 导入 HttpStatus，使用其状态码常量
import org.springframework.http.HttpStatus;
// 导入 ResponseEntity，构建带状态码的 HTTP 响应
import org.springframework.http.ResponseEntity;
// 导入 Spring MVC 注解（@RestController、@RequestMapping、@PostMapping 等）
import org.springframework.web.bind.annotation.*;

// 导入 Map，用于构建 JSON 响应体
import java.util.Map;

/**
 * 认证控制器
 * <p>
 * 提供学生/管理员登录、登出、获取当前用户、修改密码接口。
 * </p>
 */
// @RestController：标识为 REST 控制器，返回 JSON 而非视图，等价于 @Controller + @ResponseBody
@RestController
// @RequestMapping("/api/auth")：基础路径，所有方法路径以此为前缀
@RequestMapping("/api/auth")
public class AuthController {

    /** 管理员会话 key：拦截器通过此 key 从会话读取管理员对象 */
    public static final String SESSION_ADMIN_KEY = "admin";

    /** 学生会话 key：拦截器通过此 key 从会话读取学生对象 */
    public static final String SESSION_USER_KEY = "user";

    // 注入的认证服务，由 Spring 通过构造器注入
    private final AuthService authService;

    // 构造器注入 AuthService
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 学生登录
     *
     * @param req 登录请求
     * @return 登录响应
     */
    // @PostMapping("/login")：处理 POST /api/auth/login 请求
    @PostMapping("/login")
    // @Valid：触发 LoginRequest 上的字段校验注解（如 @NotBlank）
    // @RequestBody：将请求体 JSON 反序列化为 LoginRequest 对象
    // HttpSession session：Spring 自动注入当前会话
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        try {
            // 调用服务层校验用户名密码，返回登录响应（含用户 ID、昵称等）
            LoginResponse resp = authService.userLogin(req);
            // 登录成功后，将用户信息写入会话（用于拦截器校验）
            // 仅存 ID 即可，拦截器只校验类型与存在性
            User sessionUser = new User();
            // 设置会话用户的 ID
            sessionUser.setId(resp.getId());
            // 将用户对象写入会话，key 为 SESSION_USER_KEY
            session.setAttribute(SESSION_USER_KEY, sessionUser);
            // 返回 200 OK 和登录响应体
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            // 业务异常（用户名密码错误、账号被封禁等），返回 422 Unprocessable Entity
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
    // @PostMapping("/admin/login")：处理 POST /api/auth/admin/login 请求
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginRequest req, HttpSession session) {
        try {
            // 调用服务层校验管理员账号密码
            LoginResponse resp = authService.adminLogin(req);
            // 仅存 ID 即可，拦截器只校验类型与存在性
            AdminUser sessionAdmin = new AdminUser();
            // 设置会话管理员的 ID
            sessionAdmin.setId(resp.getId());
            // 将管理员对象写入会话，key 为 SESSION_ADMIN_KEY
            session.setAttribute(SESSION_ADMIN_KEY, sessionAdmin);
            // 返回 200 OK
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            // 管理员账号密码错误，返回 422
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
    // @PostMapping("/logout")：处理 POST /api/auth/logout 请求
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        // 使当前会话失效，清除所有会话属性
        session.invalidate();
        // 返回 200 OK 和提示消息
        return ResponseEntity.ok(Map.of("message", "已登出"));
    }

    /**
     * 获取当前登录用户信息
     *
     * @param session 会话
     * @return 用户信息 map
     */
    // @GetMapping("/me")：处理 GET /api/auth/me 请求
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        // 从会话读取学生对象
        Object user = session.getAttribute(SESSION_USER_KEY);
        // 从会话读取管理员对象
        Object admin = session.getAttribute(SESSION_ADMIN_KEY);
        // 优先判断管理员（模式匹配变量 a）
        if (admin instanceof AdminUser a) {
            // 返回 200 OK 和管理员信息（role=ADMIN）
            return ResponseEntity.ok(Map.of(
                    "role", "ADMIN",
                    "id", a.getId(),
                    "username", a.getUsername(),
                    "displayName", a.getDisplayName()
            ));
        }
        // 其次判断学生（模式匹配变量 u）
        if (user instanceof User u) {
            // 返回 200 OK 和学生信息（role=USER）
            return ResponseEntity.ok(Map.of(
                    "role", "USER",
                    "id", u.getId(),
                    "username", u.getUsername(),
                    "nickname", u.getNickname()
            ));
        }
        // 既无管理员也无学生，返回 401 Unauthorized
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
    // @PostMapping("/change-password")：处理 POST /api/auth/change-password 请求
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest req,
                                            HttpSession session) {
        // 从会话读取学生与管理员对象
        Object user = session.getAttribute(SESSION_USER_KEY);
        Object admin = session.getAttribute(SESSION_ADMIN_KEY);
        try {
            // 优先按管理员身份改密
            if (admin instanceof AdminUser a) {
                authService.changeAdminPassword(a.getId(), req);
            } else if (user instanceof User u) {
                // 其次按学生身份改密
                authService.changeUserPassword(u.getId(), req);
            } else {
                // 既无管理员也无学生，返回 401 Unauthorized
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "未登录"));
            }
            // 修改密码后使会话失效，强制重新登录
            session.invalidate();
            // 返回 200 OK 和提示
            return ResponseEntity.ok(Map.of("message", "密码修改成功，请重新登录"));
        } catch (IllegalArgumentException e) {
            // 原密码错误等业务异常，返回 422
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
