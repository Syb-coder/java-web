// 声明包路径
package com.example.java2.controller;

// 导入 DTO 与实体类
import com.example.java2.dto.ChangePasswordRequest;
import com.example.java2.dto.LoginRequest;
import com.example.java2.dto.LoginResponse;
import com.example.java2.dto.RegisterRequest;
import com.example.java2.dto.UserResponse;
import com.example.java2.model.User;
import com.example.java2.service.UserService;

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
 * 前台用户认证控制器
 * <p>
 * 提供用户注册、登录、登出、当前用户信息、修改密码接口。
 * 登录成功后将用户信息写入 HttpSession，由 {@code LoginInterceptor} 校验登录态。
 * </p>
 */
// @RestController = @Controller + @ResponseBody：返回值自动经 Jackson 序列化为 JSON，无需逐方法标注 @ResponseBody
@RestController
// 路径设计遵循 RESTful 约定：/api/users 以"用户"资源域为根，子路径 register/login/logout/me/change-password
// 表达该资源域下的认证与账户管理子操作，便于拦截器按 /api/users 前缀统一匹配
@RequestMapping("/api/users")
public class UserController {

    /** Session 中存储当前登录前台用户的键名 */
    // 公开常量供 LoginInterceptor、ArticleController、CommentController 等共享同一 key，避免魔法字符串拼写不一致
    public static final String SESSION_USER_KEY = "loginUser";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     *
     * @param req 注册请求
     * @return 用户响应（成功）或 400（用户名已存在）
     */
    @PostMapping("/register")
    // POST 新增资源：注册本质是"创建用户"动作，符合 RESTful 用 POST 表达资源创建的约定
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        // @Valid 在方法进入前触发 Bean Validation，对 RegisterRequest 字段约束（如 @NotBlank、@Size）逐一校验，
        // 失败抛 MethodArgumentNotValidException 由全局异常处理器返回 400，拦截非法参数进入 service 层
        try {
            // 200 OK 携带新建用户脱敏信息返回，前端据此跳转登录页
            return ResponseEntity.ok(userService.register(req));
        } catch (IllegalArgumentException e) {
            // 用户名重复等业务约束冲突返回 400，区别于 409 Conflict：注册场景下用户名重复属参数不合规
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
    // POST 携带密码等敏感凭证，避免 GET 将参数暴露在 URL 与浏览器历史中
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        LoginResponse resp = userService.login(req);
        if (resp == null) {
            // 登录失败统一返回 401，不区分用户名不存在与密码错误，防止账号枚举攻击
            return ResponseEntity.status(401).build();
        }
        // 二次查询用户实体写入 Session，登录接口仅返回脱敏的 LoginResponse
        User user = userService.getByUsername(req.username());
        if (user != null) {
            // 写入 Session 供后续 @SessionAttribute 与拦截器使用，建立登录态
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
    // POST 而非 DELETE：登出是状态变更动作，CSRF 防护要求非 GET 方法，DELETE 语义偏向"删除资源"而非"执行动作"
    public ResponseEntity<Void> logout(HttpSession session) {
        // 销毁整个 Session 而非仅移除 user 属性，确保收藏/点赞等登录态衍生数据一并清理，防止会话固定攻击
        session.invalidate();
        // 204 No Content 表示操作成功且无响应体，符合 RESTful 登出语义
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取当前登录用户信息
     *
     * @param session HTTP 会话
     * @return 用户响应（已登录）或 401（未登录）
     */
    @GetMapping("/me")
    // GET 查询当前用户：幂等读操作不修改状态，符合 RESTful 用 GET 表达资源获取的约定
    public ResponseEntity<UserResponse> me(HttpSession session) {
        // 以 Object 接收再做 instanceof 校验，避免 Session 属性被篡改时抛 ClassCastException 暴露内部信息
        Object user = session.getAttribute(SESSION_USER_KEY);
        if (user instanceof User u) {
            // 重新查询数据库，保证最新状态（如管理员禁用用户后立即生效）
            User fresh = userService.getById(u.getId());
            if (fresh == null || !fresh.isEnabled()) {
                // 账号被禁用或删除后立即失效 Session，强制下线避免越权访问
                session.invalidate();
                return ResponseEntity.status(401).build();
            }
            return ResponseEntity.ok(UserResponse.from(fresh));
        }
        // 未登录或 Session 失效统一 401，前端据此跳转登录页
        return ResponseEntity.status(401).build();
    }

    /**
     * 修改当前登录用户密码
     *
     * @param req  修改密码请求
     * @param user 当前登录用户（由 Session 注入）
     * @return 200（成功） / 400（参数不合规） / 401（未登录） / 422（旧密码错误）
     */
    @PostMapping("/change-password")
    // POST 表达密码变更动作，且携带旧密码/新密码等敏感参数，必须走非 GET 方法
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest req,
            // @SessionAttribute 工作原理：Spring MVC 在方法调用前从 HttpSession 按 value 指定的 key 取属性注入参数，
            // 等价于 session.getAttribute(SESSION_USER_KEY) 但由框架完成类型转换
            // required = false：未登录或无该 Session 属性时不抛异常而是注入 null，
            // 让方法体内统一返回 401，避免框架默认抛异常导致返回 400 误导前端登录态判断
            @SessionAttribute(value = SESSION_USER_KEY, required = false) User user,
            HttpSession session) {
        // 双重身份校验之一（方法内判断）：拦截器放行 /api/users/change-password（因属 /api/users 路径但需特殊处理），
        // 此处二次校验防御拦截器规则遗漏，确保未登录用户无法触发密码变更
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            boolean ok = userService.changePassword(user.getId(), req);
            if (ok) {
                // 密码修改后强制下线，要求重新登录
                // 防止旧密码泄露后的 Session 仍可继续操作，符合密码变更后重新认证的安全实践
                session.invalidate();
                // 200 OK 表示密码修改成功，前端应跳转登录页重新认证
                return ResponseEntity.ok().build();
            }
            // 选用 422 而非 403，区分"格式合法但语义错误（旧密码错）"与"权限不足"
            return ResponseEntity.status(422).build();
        } catch (IllegalArgumentException e) {
            // 参数格式不合规返回 400，与 service 层校验异常语义对齐
            return ResponseEntity.badRequest().build();
        }
    }
}
