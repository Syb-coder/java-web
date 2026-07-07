package com.example.java6.config;

import com.example.java6.controller.AuthController;
import com.example.java6.model.AdminUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 *
 * <p>保护后台管理 API。规则如下：
 * <ul>
 *   <li>/api/auth/** 始终放行（登录、登出、当前用户接口）；</li>
 *   <li>GET 请求放行（前台浏览查询）；</li>
 *   <li>/api/reports 的 POST 请求放行（前台用户举报提交）；</li>
 *   <li>其余 POST/PUT/DELETE 请求必须已登录，否则返回 401。</li>
 * </ul>
 * </p>
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 规则1：认证相关接口始终放行
        if (uri.startsWith("/api/auth/")) {
            return true;
        }

        // 规则2：GET 请求（前台浏览查询）放行
        if ("GET".equalsIgnoreCase(method)) {
            return true;
        }

        // 规则3：前台举报提交 POST /api/reports 放行（普通用户也可举报）
        if ("POST".equalsIgnoreCase(method) && "/api/reports".equals(uri)) {
            return true;
        }

        // 规则4：其余写操作需登录校验
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Object user = session.getAttribute(AuthController.SESSION_USER_KEY);
        if (!(user instanceof AdminUser)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }
}
