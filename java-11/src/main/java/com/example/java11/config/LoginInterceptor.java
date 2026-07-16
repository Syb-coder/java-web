package com.example.java11.config;  // 配置层包，存放 Web 配置与拦截器

import com.example.java11.controller.AuthController;  // 引入认证 Controller，复用其 Session 常量 KEY
import com.example.java11.model.AdminRole;  // 引入管理员角色枚举，用于版主角色校验
import com.example.java11.model.AdminUser;  // 引入管理员实体，用于 instanceof 类型判定
import com.example.java11.model.User;  // 引入普通用户实体，用于 instanceof 类型判定
import jakarta.servlet.http.HttpServletRequest;  // 引入 Servlet 请求对象，用于读取 URI 与 Session
import jakarta.servlet.http.HttpServletResponse;  // 引入 Servlet 响应对象，用于写入 401/403 状态码
import jakarta.servlet.http.HttpSession;  // 引入 Session 对象，用于读取登录态
import org.springframework.http.HttpStatus;  // 引入 HTTP 状态码常量（UNAUTHORIZED/FORBIDDEN）
import org.springframework.stereotype.Component;  // 引入 @Component，让 Spring 扫描并注册为 Bean
import org.springframework.web.servlet.HandlerInterceptor;  // 引入拦截器接口，preHandle 在 Controller 前执行

/**
 * 登录拦截器
 * <p>
 * 保护平台各类 API，按角色精细化放行。规则：
 * <ol>
 *   <li>认证相关接口（登录/注册/登出）始终放行；</li>
 *   <li>GET 请求（浏览查询）放行；</li>
 *   <li>/api/admin/** 写操作需管理员（ADMIN 或 MODERATOR）登录；</li>
 *   <li>其余写操作需普通用户登录；</li>
 *   <li>未登录返回 401。</li>
 * </ol>
 * </p>
 */
@Component  // 注册为 Spring Bean，可被 WebMvcConfig 注入到拦截器链
public class LoginInterceptor implements HandlerInterceptor {  // 实现拦截器接口

    @Override  // 重写父接口方法，请求到达 Controller 前执行
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {  // 返回 true 放行，false 中止请求
        String uri = request.getRequestURI();  // 取出当前请求路径，用于按路径分支放行
        String method = request.getMethod();  // 取出 HTTP 方法，用于区分读/写请求

        // 规则1：认证相关接口始终放行（登录/注册/管理员登录/会话检测）
        if (uri.endsWith("/login") || uri.endsWith("/register")
                || uri.startsWith("/api/auth/logout")
                || uri.startsWith("/api/auth/me")
                || uri.startsWith("/api/auth/admin/me")) {  // 认证入口无需登录态
            return true;  // 直接放行
        }

        // 规则2：GET 请求放行（浏览查询类操作不要求登录态，降低使用门槛）
        if ("GET".equalsIgnoreCase(method)) {  // 所有读操作放行
            return true;  // 放行
        }

        // 规则3：后台管理写操作需管理员登录（ADMIN 或 MODERATOR）
        if (uri.startsWith("/api/admin/")) {  // 后台写操作（如审核帖子、管理用户）
            return requireAdmin(request, response);  // 限定管理员登录态
        }

        // 规则4：其余写操作需普通用户登录（如发帖、评论、点赞）
        return requireUser(request, response);  // 默认走用户校验
    }

    /**
     * 校验管理员登录态（ADMIN 或 MODERATOR 均可访问 /api/admin/）
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @return true 已登录管理员，false 已写入 401
     * @throws java.io.IOException 写入响应时可能抛出
     */
    private boolean requireAdmin(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {  // 抛 IO 异常由 Spring 统一处理
        HttpSession session = request.getSession(false);  // false：无 Session 不创建新 Session
        if (session == null) {  // 完全没有 Session，必定未登录
            response.setStatus(HttpStatus.UNAUTHORIZED.value());  // 写入 401 未授权
            return false;  // 中止请求
        }
        Object user = session.getAttribute(AuthController.SESSION_ADMIN_KEY);  // 取出管理员 Session 属性
        if (!(user instanceof AdminUser)) {  // 类型不匹配说明非管理员登录
            response.setStatus(HttpStatus.UNAUTHORIZED.value());  // 写入 401
            return false;  // 中止
        }
        return true;  // 已登录管理员，放行
    }

    /**
     * 校验普通用户登录态
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @return true 已登录，false 已写入 401
     * @throws java.io.IOException 写入响应时可能抛出
     */
    private boolean requireUser(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {  // 抛 IO 异常由 Spring 统一处理
        HttpSession session = request.getSession(false);  // 不创建新 Session
        if (session == null) {  // 无 Session
            response.setStatus(HttpStatus.UNAUTHORIZED.value());  // 401
            return false;  // 中止
        }
        Object user = session.getAttribute(AuthController.SESSION_USER_KEY);  // 取出普通用户 Session 属性
        if (!(user instanceof User)) {  // 非用户登录态
            response.setStatus(HttpStatus.UNAUTHORIZED.value());  // 401
            return false;  // 中止
        }
        return true;  // 用户已登录，放行
    }
}
