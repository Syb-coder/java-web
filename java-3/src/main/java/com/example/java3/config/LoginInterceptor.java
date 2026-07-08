package com.example.java3.config;

import com.example.java3.controller.AuthController;
import com.example.java3.controller.UserController;
import com.example.java3.model.AdminUser;
import com.example.java3.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 * <p>
 * 规则：
 * <ul>
 *   <li>/api/auth/** 与 /api/users/register、/api/users/login、/api/users/logout 始终放行；</li>
 *   <li>GET 请求（前台浏览查询）放行；</li>
 *   <li>/api/admin/** 必须管理员登录；</li>
 *   <li>其余 POST/PUT/DELETE 请求需前台用户登录；</li>
 *   <li>未登录返回 401。</li>
 * </ul>
 * </p>
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 规则1：认证相关接口始终放行（学生/管理员登录、登出、获取当前用户、修改密码）
        if (uri.startsWith("/api/auth/login") || uri.startsWith("/api/auth/admin/login")
                || uri.startsWith("/api/auth/logout") || uri.startsWith("/api/auth/me")
                || uri.startsWith("/api/auth/change-password")) {
            return true;
        }
        // 规则2：前台用户注册/登录/登出始终放行
        if (uri.equals("/api/users/register") || uri.equals("/api/users/login")
                || uri.equals("/api/users/logout")) {
            return true;
        }
        // 规则3：GET 请求（前台浏览查询）放行
        if ("GET".equalsIgnoreCase(method)) {
            return true;
        }

        // 规则4：/api/admin/** 写操作需管理员登录
        if (uri.startsWith("/api/admin/")) {
            return requireAdmin(request, response);
        }

        // 规则5：其余写操作需前台用户登录
        return requireUser(request, response);
    }

    /**
     * 校验管理员登录态
     */
    private boolean requireAdmin(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Object user = session.getAttribute(AuthController.SESSION_ADMIN_KEY);
        if (!(user instanceof AdminUser)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }

    /**
     * 校验前台用户登录态
     */
    private boolean requireUser(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Object user = session.getAttribute(UserController.SESSION_USER_KEY);
        if (!(user instanceof User)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }
}
