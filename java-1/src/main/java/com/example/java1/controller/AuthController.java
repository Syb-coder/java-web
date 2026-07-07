// 声明包路径，归类为 controller 控制器层，承接 HTTP 请求并调用 service
package com.example.java1.controller;

// 以下导入本模块内的 DTO 与模型、服务，遵循分层架构：controller 不直接访问 repository
import com.example.java1.dto.ChangePasswordRequest; // 引入修改密码请求 DTO，封装旧密码与新密码
import com.example.java1.dto.LoginRequest; // 引入登录请求 DTO，封装用户名与密码
import com.example.java1.dto.LoginResponse; // 引入登录响应 DTO，对外返回脱敏后的管理员信息
import com.example.java1.model.AdminUser; // 引入管理员实体，对应数据库 admin_users 表
import com.example.java1.service.AuthService; // 引入认证服务，封装登录校验与密码变更逻辑
// 以下导入 Servlet 会话相关类型，本项目采用 HttpSession 维持登录态（单体应用简化方案）
import jakarta.servlet.http.HttpSession; // 引入 HTTP 会话，用于存储登录态
// 以下导入校验注解，配合 DTO 上的约束注解实现参数自动校验
import jakarta.validation.Valid; // 引入 @Valid，触发请求体参数的 Bean Validation
import org.springframework.http.ResponseEntity; // 引入响应实体，可灵活控制状态码与响应体
import org.springframework.web.bind.annotation.GetMapping; // 引入 @GetMapping，映射 HTTP GET 请求
import org.springframework.web.bind.annotation.PostMapping; // 引入 @PostMapping，映射 HTTP POST 请求
import org.springframework.web.bind.annotation.RequestBody; // 引入 @RequestBody，绑定请求体到 DTO
import org.springframework.web.bind.annotation.RequestMapping; // 引入 @RequestMapping，定义控制器根路径
import org.springframework.web.bind.annotation.RestController; // 引入 @RestController，声明 RESTful 控制器
import org.springframework.web.bind.annotation.SessionAttribute; // 引入 @SessionAttribute，从 Session 中提取属性

/**
 * 管理员认证控制器
 * <p>
 * 提供后台管理员登录、登出、当前用户信息、修改密码接口。
 * 登录成功后将管理员信息写入 HttpSession，由 LoginInterceptor 校验登录态。
 * </p>
 * <p>
 * 设计说明：校园图书借阅系统中管理员负责代办借还书、维护图书与读者档案，
 * 因此认证控制器仅服务于管理员身份，读者登录见 ReaderController。
 * </p>
 */
@RestController // 声明为 RESTful 控制器，返回值自动序列化为 JSON，不渲染视图
@RequestMapping("/api/auth") // 统一前缀 /api/auth，与前端约定的认证模块路径
public class AuthController {

    /** Session 中存储当前登录管理员的键名，对外公开以便拦截器与其它组件复用 */
    public static final String SESSION_ADMIN_KEY = "adminUser"; // 常量键名，避免魔法字符串散落各处

    private final AuthService authService; // 认证服务依赖，final 保证构造后不可变

    /**
     * 构造方法注入
     * <p>采用构造方法注入而非字段注入，便于单元测试 Mock 且符合 Spring 推荐实践。</p>
     *
     * @param authService 认证服务
     */
    public AuthController(AuthService authService) { // 构造方法注入依赖
        this.authService = authService; // 赋值认证服务
    }

    /**
     * 管理员登录
     *
     * @param req     登录请求
     * @param session HTTP 会话
     * @return 登录响应（成功）或 401（失败）
     */
    @PostMapping("/login") // 映射 POST /api/auth/login，登录为写操作故用 POST
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req, HttpSession session) { // @Valid 触发请求体校验，session 由容器注入
        LoginResponse resp = authService.login(req); // 委托服务层校验账号密码，返回响应或 null
        if (resp == null) { // 登录失败：账号不存在或密码错误
            return ResponseEntity.status(401).build(); // 返回 401 Unauthorized，统一错误信息避免泄露账号是否存在
        }
        // 写入 session 标记登录态
        AdminUser user = authService.getByUsername(req.username()); // 二次查询管理员实体，用于写入 Session（响应 DTO 不含敏感字段）
        if (user != null) { // 二次校验防止并发删除账号导致空指针
            session.setAttribute(SESSION_ADMIN_KEY, user); // 将管理员实体存入 Session，后续拦截器据此判定登录态
        }
        return ResponseEntity.ok(resp); // 返回 200 与登录响应，前端据此跳转后台
    }

    /**
     * 管理员登出
     *
     * @param session HTTP 会话
     * @return 204
     */
    @PostMapping("/logout") // 映射 POST /api/auth/logout，登出改变服务端状态故用 POST
    public ResponseEntity<Void> logout(HttpSession session) { // session 由容器注入
        session.invalidate(); // 销毁当前会话，清除所有登录态与 Session 数据
        return ResponseEntity.noContent().build(); // 返回 204 No Content，表示操作成功且无响应体
    }

    /**
     * 获取当前登录管理员信息（用于前端校验登录态）
     *
     * @param session HTTP 会话
     * @return 登录响应（已登录）或 401（未登录）
     */
    @GetMapping("/me") // 映射 GET /api/auth/me，仅查询当前会话登录态，幂等且无副作用
    public ResponseEntity<LoginResponse> me(HttpSession session) { // session 由容器注入
        Object user = session.getAttribute(SESSION_ADMIN_KEY); // 从 Session 读取管理员属性，可能为 null
        if (user instanceof AdminUser adminUser) { // 类型守卫：仅当属性为 AdminUser 时才视为已登录，防止篡改
            return ResponseEntity.ok(LoginResponse.from(adminUser)); // 转换为脱敏响应 DTO 返回 200
        }
        return ResponseEntity.status(401).build(); // 未登录或类型不符返回 401，前端引导至登录页
    }

    /**
     * 修改当前登录管理员的密码
     *
     * @param req   修改密码请求
     * @param admin 当前登录管理员（由 Session 注入）
     * @return 200（成功） / 400（参数不合规） / 401（未登录） / 422（旧密码错误）
     */
    @PostMapping("/change-password") // 映射 POST /api/auth/change-password，密码变更为敏感写操作
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest req, // 触发请求体校验，确保新旧密码格式合规
            @SessionAttribute(value = SESSION_ADMIN_KEY, required = false) AdminUser admin) { // 从 Session 注入管理员，required=false 避免未登录时抛异常改为手动判定
        if (admin == null) { // 未登录场景：Session 中无管理员属性
            return ResponseEntity.status(401).build(); // 返回 401，引导前端重新登录
        }
        try {
            boolean ok = authService.changePassword(admin.getId(), req); // 委托服务层校验旧密码并更新
            if (ok) { // 旧密码正确且更新成功
                // 修改密码后失效当前会话，强制重新登录
                // 注：此处未主动 invalidate，由前端收到 200 后调用 logout 清理
                return ResponseEntity.ok().build(); // 返回 200，前端引导重新登录
            }
            // 旧密码错误
            return ResponseEntity.status(422).build(); // 返回 422 Unprocessable Entity，表示请求格式正确但业务校验未通过
        } catch (IllegalArgumentException e) {
            // 新密码不合规
            return ResponseEntity.badRequest().build(); // 返回 400，表示请求参数不符合约束规则
        }
    }
}
