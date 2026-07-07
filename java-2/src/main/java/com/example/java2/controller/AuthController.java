// 声明包路径
package com.example.java2.controller;

// 导入 DTO 与实体类
import com.example.java2.dto.ChangePasswordRequest;
import com.example.java2.dto.LoginRequest;
import com.example.java2.dto.LoginResponse;
import com.example.java2.model.AdminUser;
import com.example.java2.service.AuthService;

// 导入 Spring Web 注解
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * 管理员认证控制器
 * <p>
 * 提供后台管理员登录、登出、当前用户信息、修改密码接口。
 * 登录成功后将管理员信息写入 HttpSession，由 {@code LoginInterceptor} 校验登录态。
 * </p>
 */
// @RestController = @Controller + @ResponseBody：每个方法返回值自动通过 Jackson 序列化为 JSON 写入响应体，
// 无需在每个方法上单独标注 @ResponseBody，契合前后端分离架构下纯 API 接口的开发模式
@RestController
// 路径设计遵循 RESTful 约定：/api 表示 API 边界，/auth 表达"认证"资源域，
// 子路径 login/logout/me/change-password 均为该资源域下的子操作，层级清晰便于拦截器按前缀匹配
@RequestMapping("/api/auth")
public class AuthController {

    /** Session 中存储当前登录管理员的键名 */
    // 公开常量供 LoginInterceptor 与本 Controller 共享 key，避免魔法字符串散落多处导致拼写不一致
    public static final String SESSION_ADMIN_KEY = "adminUser";

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
    // POST 而非 GET：登录请求携带敏感凭证（密码），GET 会将参数暴露在 URL 与访问日志中，存在泄露风险
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        // @Valid 在方法进入前由 Spring 校验框架触发：对 LoginRequest 字段上的 @NotBlank 等约束逐一校验，
        // 失败直接抛 MethodArgumentNotValidException 返回 400，避免空用户名/密码进入 service 浪费 DB 查询
        LoginResponse resp = authService.login(req);
        if (resp == null) {
            // 登录失败统一返回 401，不区分用户名不存在与密码错误，防止账号枚举攻击
            return ResponseEntity.status(401).build();
        }
        // 写入 session 标记登录态
        AdminUser user = authService.getByUsername(req.username());
        if (user != null) {
            // 仅在确实查到管理员后写 Session，避免污染匿名 Session
            session.setAttribute(SESSION_ADMIN_KEY, user);
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
    // POST 而非 DELETE：登出是"执行动作"而非"删除资源"，且 CSRF 防护要求状态变更操作走非 GET 方法
    public ResponseEntity<Void> logout(HttpSession session) {
        // 销毁整个 Session 而非仅移除 admin 属性，确保登录态相关衍生数据一并清理，防止会话固定攻击
        session.invalidate();
        // 204 No Content 表示操作成功且无响应体，符合 RESTful 登出语义
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取当前登录管理员信息（用于前端校验登录态）
     *
     * @param session HTTP 会话
     * @return 登录响应（已登录）或 401（未登录）
     */
    @GetMapping("/me")
    // GET 查询当前用户信息：幂等读操作，不修改服务端状态，符合 RESTful 用 GET 表达资源获取的约定
    public ResponseEntity<LoginResponse> me(HttpSession session) {
        // 以 Object 接收再做 instanceof 校验，避免类型篡改攻击下抛 ClassCastException 暴露内部信息
        Object user = session.getAttribute(SESSION_ADMIN_KEY);
        if (user instanceof AdminUser adminUser) {
            return ResponseEntity.ok(LoginResponse.from(adminUser));
        }
        // 未登录或 Session 失效统一 401，前端据此跳转登录页
        return ResponseEntity.status(401).build();
    }

    /**
     * 修改当前登录管理员的密码
     *
     * @param req   修改密码请求
     * @param admin 当前登录管理员（由 Session 注入）
     * @return 200（成功） / 400（参数不合规） / 401（未登录） / 422（旧密码错误）
     */
    @PostMapping("/change-password")
    // POST 而非 PUT：密码修改虽是"更新"语义，但作为认证域子操作且无独立资源 URI，统一用 POST 表达动作执行
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest req,
            // @SessionAttribute 工作原理：Spring MVC 在调用方法前从 HttpSession 中按 value 指定的 key 取出属性并注入参数，
            // 等价于 session.getAttribute(SESSION_ADMIN_KEY) 但由框架自动完成类型转换与注入
            // required = false 的关键作用：未登录或 Session 中无该 key 时不抛 MissingSessionAttributeException，
            // 而是注入 null，让方法体内部统一返回 401，避免框架默认行为返回 400 误导前端
            @SessionAttribute(value = SESSION_ADMIN_KEY, required = false) AdminUser admin) {
        // 双重身份校验之一（方法内判断）：拦截器已放行 /api/auth/change-password，
        // 此处再次校验 admin 是否为 null，防御拦截器配置遗漏或未来路径调整带来的越权风险
        if (admin == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            boolean ok = authService.changePassword(admin.getId(), req);
            // 200 OK 表示密码修改成功，响应体为空
            if (ok) return ResponseEntity.ok().build();
            // 旧密码错误
            // 选用 422 Unprocessable Entity 而非 403，区分"格式合法但语义错误"与"权限不足"
            return ResponseEntity.status(422).build();
        } catch (IllegalArgumentException e) {
            // 新密码不合规
            // 参数格式错误返回 400，与 service 层抛出的校验异常语义对齐
            return ResponseEntity.badRequest().build();
        }
    }
}
