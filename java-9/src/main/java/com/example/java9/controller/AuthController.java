package com.example.java9.controller;  // 声明控制器包路径

import com.example.java9.dto.ChangePasswordRequest;  // 导入修改密码请求 DTO
import com.example.java9.dto.LoginRequest;  // 导入登录请求 DTO
import com.example.java9.dto.LoginResponse;  // 导入登录响应 DTO
import com.example.java9.model.AdminUser;  // 导入管理员实体
import com.example.java9.model.Merchant;  // 导入商户实体
import com.example.java9.model.User;  // 导入用户实体
import com.example.java9.service.AuthService;  // 导入认证服务
import jakarta.servlet.http.HttpSession;  // 导入 Servlet HTTP 会话对象
import org.springframework.http.ResponseEntity;  // 导入 ResponseEntity，封装 HTTP 响应体与状态码
import org.springframework.web.bind.annotation.GetMapping;  // 导入 GET 请求映射注解
import org.springframework.web.bind.annotation.PostMapping;  // 导入 POST 请求映射注解
import org.springframework.web.bind.annotation.RequestBody;  // 导入请求体绑定注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入类级路由映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入 REST 控制器注解
import org.springframework.web.bind.annotation.SessionAttribute;  // 导入会话属性绑定注解（本类未使用，保留导入）
import java.util.Map;  // 导入 Map 集合

/**
 * 统一认证控制器
 * <p>
 * 提供管理员、C端用户、商户三类账户的登录/登出/当前用户/修改密码接口。
 * 登录成功后将账户信息写入 HttpSession，由 {@code LoginInterceptor} 校验登录态。
 * </p>
 * <p>
 * 接口列表：
 * <ul>
 *   <li>POST /api/auth/admin/login - 管理员登录</li>
 *   <li>POST /api/auth/user/login - C端用户登录</li>
 *   <li>POST /api/auth/merchant/login - 商户登录</li>
 *   <li>POST /api/auth/logout - 登出（通用）</li>
 *   <li>GET /api/auth/me - 获取当前登录账户信息</li>
 *   <li>POST /api/auth/change-password - 修改当前账户密码</li>
 * </ul>
 * </p>
 */
@RestController  // 声明为 REST 控制器，返回值自动序列化为 JSON 响应体
@RequestMapping("/api/auth")  // 类级路由前缀，本类所有接口均以 /api/auth 开头
public class AuthController {

    /** Session 中存储当前登录管理员的键名 */
    public static final String SESSION_ADMIN_KEY = "adminUser";  // 定义 Session 键名常量，用于存取管理员登录态

    private final AuthService authService;  // 注入认证服务，处理登录/改密等业务逻辑

    public AuthController(AuthService authService) {  // 构造函数注入，Spring 自动装配 AuthService Bean
        this.authService = authService;  // 赋值给字段（final 字段只能在构造函数中赋值）
    }

    /**
     * 管理员登录（运营/风控）
     *
     * @param req     登录请求
     * @param session HTTP 会话
     * @return 登录响应（成功 200 / 失败 401）
     */
    @PostMapping("/admin/login")  // 映射 POST /api/auth/admin/login 请求
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest req, HttpSession session) {
        // @RequestBody 将请求体 JSON 自动反序列化为 LoginRequest 对象
        // HttpSession 由 Servlet 容器自动注入，用于存储登录态
        try {
            AdminUser admin = authService.loginAdmin(req);  // 调用 Service 校验管理员账号密码并返回实体
            session.setAttribute(SESSION_ADMIN_KEY, admin);  // 将管理员信息写入 Session，标记登录态
            return ResponseEntity.ok(authService.buildAdminLoginResponse(admin));  // 200 OK + 登录响应 DTO
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));  // 账号或密码错误，返回 401
        }
    }

    /**
     * C端用户登录
     *
     * @param req     登录请求
     * @param session HTTP 会话
     * @return 登录响应（成功 200 / 失败 401 / 冻结 403）
     */
    @PostMapping("/user/login")  // 映射 POST /api/auth/user/login 请求
    public ResponseEntity<?> userLogin(@RequestBody LoginRequest req, HttpSession session) {
        // @RequestBody 将请求体 JSON 绑定为 LoginRequest 对象
        try {
            User user = authService.loginUserService(req);  // 调用 Service 校验用户账号密码
            session.setAttribute(UserController.SESSION_USER_KEY, user);  // 写入用户登录态到 Session（键名复用 UserController 常量）
            return ResponseEntity.ok(authService.buildUserLoginResponse(user));  // 200 + 用户登录响应
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));  // 账号或密码错误，返回 401
        } catch (IllegalStateException e) {
            // 账户被冻结
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));  // 账户被冻结，返回 403
        }
    }

    /**
     * 商户登录
     *
     * @param req     登录请求
     * @param session HTTP 会话
     * @return 登录响应（成功 200 / 失败 401 / 未审核 403）
     */
    @PostMapping("/merchant/login")  // 映射 POST /api/auth/merchant/login 请求
    public ResponseEntity<?> merchantLogin(@RequestBody LoginRequest req, HttpSession session) {
        // @RequestBody 绑定请求体为 LoginRequest 对象
        try {
            Merchant merchant = authService.loginMerchant(req);  // 调用 Service 校验商户账号密码及审核状态
            session.setAttribute(MerchantController.SESSION_MERCHANT_KEY, merchant);  // 写入商户登录态到 Session
            return ResponseEntity.ok(authService.buildMerchantLoginResponse(merchant));  // 200 + 商户登录响应
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));  // 账号或密码错误，返回 401
        } catch (IllegalStateException e) {
            // 账号未通过审核
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));  // 商户未通过审核，返回 403
        }
    }

    /**
     * 登出（通用，清除 session）
     *
     * @param session HTTP 会话
     * @return 204
     */
    @PostMapping("/logout")  // 映射 POST /api/auth/logout 请求
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();  // 使当前 Session 失效，清除所有登录态属性
        return ResponseEntity.noContent().build();  // 返回 204 No Content，表示操作成功无返回体
    }

    /**
     * 获取当前登录账户信息
     * <p>
     * 依次检查管理员、用户、商户三类 session 属性。
     * </p>
     *
     * @param session HTTP 会话
     * @return 登录响应（已登录 200 / 未登录 401）
     */
    @GetMapping("/me")  // 映射 GET /api/auth/me 请求
    public ResponseEntity<LoginResponse> me(HttpSession session) {
        Object admin = session.getAttribute(SESSION_ADMIN_KEY);  // 先尝试读取管理员登录态
        if (admin instanceof AdminUser adminUser) {  // 使用模式匹配判断类型并绑定变量
            return ResponseEntity.ok(authService.buildAdminLoginResponse(adminUser));  // 是管理员，返回 200 + 管理员信息
        }
        Object user = session.getAttribute(UserController.SESSION_USER_KEY);  // 再尝试读取用户登录态
        if (user instanceof User u) {  // 模式匹配判断为 User 类型
            return ResponseEntity.ok(authService.buildUserLoginResponse(u));  // 是用户，返回 200 + 用户信息
        }
        Object merchant = session.getAttribute(MerchantController.SESSION_MERCHANT_KEY);  // 最后尝试读取商户登录态
        if (merchant instanceof Merchant m) {  // 模式匹配判断为 Merchant 类型
            return ResponseEntity.ok(authService.buildMerchantLoginResponse(m));  // 是商户，返回 200 + 商户信息
        }
        return ResponseEntity.status(401).build();  // 三类登录态均不存在，返回 401 未登录
    }

    /**
     * 修改当前账户密码
     * <p>
     * 依次判断账户类型，调用对应密码修改方法。修改成功后失效当前会话。
     * </p>
     *
     * @param req     修改密码请求
     * @param session HTTP 会话
     * @return 200 成功 / 400 参数不合规 / 401 未登录 / 422 旧密码错误
     */
    @PostMapping("/change-password")  // 映射 POST /api/auth/change-password 请求
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest req, HttpSession session) {
        // @RequestBody 绑定请求体为 ChangePasswordRequest 对象（含旧密码、新密码）
        try {
            Object admin = session.getAttribute(SESSION_ADMIN_KEY);  // 读取管理员登录态
            if (admin instanceof AdminUser adminUser) {  // 若为管理员
                authService.changeAdminPassword(adminUser.getId(), req.getOldPassword(), req.getNewPassword());  // 调用 Service 修改管理员密码
                session.invalidate();  // 密码修改成功后失效会话，强制重新登录
                return ResponseEntity.ok().build();  // 返回 200 成功
            }
            Object user = session.getAttribute(UserController.SESSION_USER_KEY);  // 读取用户登录态
            if (user instanceof User u) {  // 若为用户
                authService.changeUserPassword(u.getId(), req.getOldPassword(), req.getNewPassword());  // 调用 Service 修改用户密码
                session.invalidate();  // 失效会话
                return ResponseEntity.ok().build();  // 返回 200
            }
            Object merchant = session.getAttribute(MerchantController.SESSION_MERCHANT_KEY);  // 读取商户登录态
            if (merchant instanceof Merchant m) {  // 若为商户
                authService.changeMerchantPassword(m.getId(), req.getOldPassword(), req.getNewPassword());  // 调用 Service 修改商户密码
                session.invalidate();  // 失效会话
                return ResponseEntity.ok().build();  // 返回 200
            }
            return ResponseEntity.status(401).build();  // 无任何登录态，返回 401
        } catch (IllegalArgumentException e) {
            // 旧密码错误或新密码不合规
            if (e.getMessage().contains("旧密码")) {  // 根据异常消息区分错误类型
                return ResponseEntity.status(422).build();  // 旧密码错误，返回 422 Unprocessable Entity
            }
            return ResponseEntity.badRequest().build();  // 新密码不合规，返回 400
        }
    }
}
