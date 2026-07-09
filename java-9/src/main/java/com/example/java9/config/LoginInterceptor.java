package com.example.java9.config; // 声明 Config 层包路径，集中存放 Web 配置与拦截器

import com.example.java9.controller.AuthController; // 引入认证 Controller，复用其 Session 常量 KEY
import com.example.java9.controller.MerchantController; // 引入商户 Controller，复用其 Session 常量 KEY
import com.example.java9.controller.UserController; // 引入用户 Controller，复用其 Session 常量 KEY
import com.example.java9.model.AdminRole; // 引入管理员角色枚举，用于风控角色校验
import com.example.java9.model.AdminUser; // 引入管理员实体，用于 instanceof 类型判定
import com.example.java9.model.Merchant; // 引入商户实体，用于 instanceof 类型判定
import com.example.java9.model.User; // 引入 C 端用户实体，用于 instanceof 类型判定
import jakarta.servlet.http.HttpServletRequest; // 引入 Servlet 请求对象，用于读取 URI 与 Session
import jakarta.servlet.http.HttpServletResponse; // 引入 Servlet 响应对象，用于写入 401/403 状态码
import jakarta.servlet.http.HttpSession; // 引入 Session 对象，用于读取登录态
import org.springframework.http.HttpStatus; // 引入 HTTP 状态码常量（UNAUTHORIZED/FORBIDDEN）
import org.springframework.stereotype.Component; // 引入 @Component，让 Spring 扫描并注册为 Bean
import org.springframework.web.servlet.HandlerInterceptor; // 引入拦截器接口，preHandle 在 Controller 前执行

/**
 * 登录拦截器
 * <p>
 * 保护平台各类 API，按角色精细化放行。规则：
 * <ol>
 *   <li>认证相关接口（登录/注册/登出）始终放行；</li>
 *   <li>GET 请求（C 端浏览查询）放行；</li>
 *   <li>/api/admin/** 写操作需运营管理员登录；</li>
 *   <li>/api/risk/** 写操作需风控专员登录（RISK 角色）；</li>
 *   <li>/api/merchant/** 写操作需商户登录；</li>
 *   <li>其余写操作需 C 端用户登录；</li>
 *   <li>未登录返回 401。</li>
 * </ol>
 * </p>
 */
@Component // 注册为 Spring Bean，可被 WebMvcConfig 注入到拦截器链
public class LoginInterceptor implements HandlerInterceptor {

    @Override // 重写父接口方法，请求到达 Controller 前执行
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception { // 返回 true 放行，false 中止请求
        String uri = request.getRequestURI(); // 取出当前请求路径，用于按路径分支放行
        String method = request.getMethod(); // 取出 HTTP 方法，用于区分读/写请求

        // 规则1：认证相关接口始终放行
        if (uri.endsWith("/login") || uri.startsWith("/api/auth/logout")
                || uri.startsWith("/api/auth/me")) { // 登录/登出/查当前用户接口无需登录态
            return true; // 直接放行
        }
        // 规则2：C端用户注册/登录/登出放行
        if (uri.equals("/api/users/register") || uri.equals("/api/users/login")
                || uri.equals("/api/users/logout")) { // C 端用户身份入口，未登录也应可访问
            return true; // 放行
        }
        // 规则3：商户入驻/登录/登出放行
        if (uri.equals("/api/merchants/register") || uri.equals("/api/merchants/login")
                || uri.equals("/api/merchants/logout")) { // 商户身份入口，未登录也应可访问
            return true; // 放行
        }
        // 规则4：GET 请求放行（C端浏览查询、风控报表查询等）
        if ("GET".equalsIgnoreCase(method)) { // 所有读操作不要求登录态，降低使用门槛
            return true; // 放行
        }

        // 规则5：风控后台写操作需风控专员登录
        if (uri.startsWith("/api/risk/")) { // 风控相关写操作（如处置工单）
            return requireRiskAdmin(request, response); // 限定 RISK 角色登录态
        }

        // 规则6：运营后台写操作需管理员登录
        if (uri.startsWith("/api/admin/")) { // 运营后台写操作（如审核商户、改配置）
            return requireAdmin(request, response); // 限定运营或风控管理员登录态
        }

        // 规则7：商户后台写操作需商户登录
        if (uri.startsWith("/api/merchants/")) { // 商户写操作（如发起收款）
            return requireMerchant(request, response); // 限定商户登录态
        }

        // 规则8：其余写操作需 C 端用户登录
        return requireUser(request, response); // 默认走用户校验（如投资下单、提现）
    }

    /**
     * 校验管理员登录态（运营或风控均可访问 /api/admin/）
     *
     * @return true 已登录，false 已写入 401
     */
    private boolean requireAdmin(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException { // 抛 IO 异常由 Spring 统一处理
        HttpSession session = request.getSession(false); // false：无 Session 不创建新 Session
        if (session == null) { // 完全没有 Session，必定未登录
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 写入 401 未授权
            return false; // 中止请求
        }
        Object user = session.getAttribute(AuthController.SESSION_ADMIN_KEY); // 取出管理员 Session 属性
        if (!(user instanceof AdminUser)) { // 类型不匹配说明非管理员登录
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 写入 401
            return false; // 中止
        }
        return true; // 已登录管理员，放行
    }

    /**
     * 校验风控专员登录态（仅 RISK 角色可访问 /api/risk/）
     *
     * @return true 已登录且为风控角色，false 已写入 401/403
     */
    private boolean requireRiskAdmin(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException { // 抛 IO 异常由 Spring 统一处理
        HttpSession session = request.getSession(false); // 不创建新 Session
        if (session == null) { // 无 Session 视为未登录
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401
            return false; // 中止
        }
        Object user = session.getAttribute(AuthController.SESSION_ADMIN_KEY); // 取管理员属性
        if (!(user instanceof AdminUser admin)) { // pattern matching：非 AdminUser 直接拒绝
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401
            return false; // 中止
        }
        // 风控接口仅限 RISK 角色访问，运营管理员访问返回 403
        if (admin.getRole() != AdminRole.RISK) { // 已登录但角色不符（如运营管理员）
            response.setStatus(HttpStatus.FORBIDDEN.value()); // 403 禁止访问
            return false; // 中止
        }
        return true; // 风控专员，放行
    }

    /**
     * 校验商户登录态
     *
     * @return true 已登录，false 已写入 401
     */
    private boolean requireMerchant(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException { // 抛 IO 异常由 Spring 统一处理
        HttpSession session = request.getSession(false); // 不创建新 Session
        if (session == null) { // 无 Session
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401
            return false; // 中止
        }
        Object user = session.getAttribute(MerchantController.SESSION_MERCHANT_KEY); // 取商户 Session 属性
        if (!(user instanceof Merchant)) { // 非商户登录态
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401
            return false; // 中止
        }
        return true; // 商户已登录，放行
    }

    /**
     * 校验 C 端用户登录态
     *
     * @return true 已登录，false 已写入 401
     */
    private boolean requireUser(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException { // 抛 IO 异常由 Spring 统一处理
        HttpSession session = request.getSession(false); // 不创建新 Session
        if (session == null) { // 无 Session
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401
            return false; // 中止
        }
        Object user = session.getAttribute(UserController.SESSION_USER_KEY); // 取 C 端用户 Session 属性
        if (!(user instanceof User)) { // 非用户登录态
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401
            return false; // 中止
        }
        return true; // 用户已登录，放行
    }
}
