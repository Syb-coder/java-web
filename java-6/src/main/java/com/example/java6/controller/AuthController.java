package com.example.java6.controller; // 声明当前类所在的包路径，属于后台管理认证模块的 Controller 层

import com.example.java6.dto.ChangePasswordRequest; // 导入修改密码请求 DTO，封装旧密码与新密码字段
import com.example.java6.dto.LoginRequest; // 导入登录请求 DTO，封装用户名与密码字段
import com.example.java6.dto.LoginResponse; // 导入登录响应 DTO，封装登录成功后返回给前端的管理员信息
import com.example.java6.model.AdminUser; // 导入管理员实体类，对应数据库 admin_user 表
import com.example.java6.service.AuthService; // 导入认证服务层接口，封装登录校验、密码修改等业务逻辑
import jakarta.servlet.http.HttpSession; // 导入 Jakarta 规范的 HTTP Session，用于维护登录态
import org.springframework.http.ResponseEntity; // 导入 Spring 的响应实体，可携带 HTTP 状态码与响应体返回
import org.springframework.web.bind.annotation.GetMapping; // 导入 GET 请求映射注解，标记查询类接口
import org.springframework.web.bind.annotation.PostMapping; // 导入 POST 请求映射注解，标记创建/提交类接口
import org.springframework.web.bind.annotation.RequestBody; // 导入请求体绑定注解，将 JSON 请求体反序列化为 Java 对象
import org.springframework.web.bind.annotation.RequestMapping; // 导入路径映射注解，声明 Controller 的基础路径
import org.springframework.web.bind.annotation.RestController; // 导入 REST 控制器注解，标识此类返回 JSON 而非视图
import org.springframework.web.bind.annotation.SessionAttribute; // 导入 Session 属性绑定注解，可直接从 Session 取值注入方法参数

/**
 * 认证控制器
 *
 * <p>提供管理员登录、登出、当前用户信息接口。
 * 登录成功后将管理员信息写入 HttpSession，由 LoginInterceptor 校验登录态。</p>
 */
@RestController // 标记为 REST 控制器，所有方法返回值默认转为 JSON 响应体
@RequestMapping("/api/auth") // 基础路径 /api/auth，所有认证相关接口都挂在此路径下，便于拦截器统一放行
public class AuthController {

    /** Session 中存储当前登录管理员的键名 */
    public static final String SESSION_USER_KEY = "adminUser"; // 定义 Session 键名常量，供 LoginInterceptor 与本类共享，避免魔法字符串

    private final AuthService authService; // 通过构造器注入认证服务，声明为 final 保证不可变（线程安全）

    public AuthController(AuthService authService) { // 构造器注入：Spring 自动装配 AuthService 实现类
        this.authService = authService; // 完成字段赋值
    }

    /**
     * 管理员登录
     *
     * @param req     登录请求
     * @param session HTTP 会话
     * @return 登录响应（成功）或 401（失败）
     */
    @PostMapping("/login") // POST /api/auth/login，处理管理员登录提交，采用 POST 避免账号密码暴露在 URL 中
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req, HttpSession session) { // @RequestBody 从请求体解析 JSON 为 LoginRequest；session 由容器注入用于写入登录态
        LoginResponse resp = authService.login(req); // 调用服务层校验账号密码（内部使用 BCrypt 比对哈希）
        if (resp == null) { // 服务层返回 null 表示账号不存在或密码错误
            return ResponseEntity.status(401).build(); // 返回 401 未授权，不携带响应体（避免泄露失败原因以防爆破）
        }
        // 写入 session 标记登录态
        AdminUser user = authService.getByUsername(req.username()); // 通过用户名查询管理员实体，用于写入 Session
        if (user != null) { // 二次防御判断，确保用户存在（避免极端并发场景下账号被删除）
            session.setAttribute(SESSION_USER_KEY, user); // 将管理员实体存入 Session，后续拦截器据此判定登录状态
        }
        return ResponseEntity.ok(resp); // 返回 200 OK，响应体携带登录响应信息供前端使用
    }

    /**
     * 管理员登出
     *
     * @param session HTTP 会话
     * @return 204
     */
    @PostMapping("/logout") // POST /api/auth/logout，处理管理员登出请求，POST 避免 CSRF 风险（GET 易被恶意链接触发）
    public ResponseEntity<Void> logout(HttpSession session) { // session 由容器注入，用于销毁当前会话
        session.invalidate(); // 使当前 Session 失效，清除所有登录态信息
        return ResponseEntity.noContent().build(); // 返回 204 No Content，表示操作成功且无响应体返回
    }

    /**
     * 获取当前登录管理员信息（用于前端校验登录态）
     *
     * @param session HTTP 会话
     * @return 登录响应（已登录）或 401（未登录）
     */
    @GetMapping("/me") // GET /api/me/auth，前端刷新页面时调用此接口确认登录状态并获取当前用户信息
    public ResponseEntity<LoginResponse> me(HttpSession session) { // session 由容器注入，用于读取当前登录用户
        Object user = session.getAttribute(SESSION_USER_KEY); // 从 Session 取出登录时存储的管理员对象
        if (user instanceof AdminUser adminUser) { // 使用 Java 16+ 模式匹配 instanceof，类型匹配则绑定到 adminUser 变量
            return ResponseEntity.ok(LoginResponse.from(adminUser)); // 已登录：返回 200 OK，响应体为管理员信息（已脱敏）
        }
        return ResponseEntity.status(401).build(); // 未登录或 Session 失效：返回 401，前端据此跳转登录页
    }

    /**
     * 修改当前登录管理员的密码
     *
     * @param req     修改密码请求（含旧密码、新密码）
     * @param admin   当前登录管理员（由 Session 注入）
     * @return 200（成功） / 400（参数不合规） / 401（未登录） / 422（旧密码错误）
     */
    @PostMapping("/change-password") // POST /api/auth/change-password，提交修改密码请求，POST 用于提交敏感数据
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest req, // @RequestBody 从请求体解析 JSON，封装旧密码、新密码
            @SessionAttribute(value = SESSION_USER_KEY, required = false) AdminUser admin) { // @SessionAttribute 直接从 Session 取当前登录管理员；required=false 表示未登录时注入 null 而非抛异常
        // 未登录拦截
        if (admin == null) { // Session 中无管理员信息，说明未登录
            return ResponseEntity.status(401).build(); // 返回 401 未授权，提示前端重新登录
        }
        try {
            boolean ok = authService.changePassword(admin.getId(), req); // 调用服务层校验旧密码并更新新密码（BCrypt 加密存储）
            if (ok) { // 修改成功
                return ResponseEntity.ok().build(); // 返回 200 OK
            }
            // 旧密码错误
            return ResponseEntity.status(422).build(); // 返回 422 Unprocessable Entity，表示请求格式正确但语义错误（旧密码不匹配）
        } catch (IllegalArgumentException e) { // 服务层校验新密码不合规时抛出此异常（如长度不足、复杂度不够）
            // 新密码不合规
            return ResponseEntity.badRequest().build(); // 返回 400 Bad Request，提示参数不合规
        }
    }
}
