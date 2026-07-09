// 声明包路径，存放 Web 配置类
package com.example.java4.config;

// 导入 Servlet 与 Spring 工具
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 * <p>
 * 保护后台管理 API 与用户端写操作。规则如下：
 * <ul>
 *   <li>/api/auth/** 始终放行（登录、注册接口）；</li>
 *   <li>GET 请求（浏览查询）始终放行；</li>
 *   <li>/api/admin/** 必须管理员登录；</li>
 *   <li>其余 POST/PUT/DELETE 请求（借阅、借阅车、个人信息）必须读者登录；</li>
 *   <li>未登录返回 401。</li>
 * </ul>
 * </p>
 * <p>
 * Session 约定：
 * - "userId" 存储登录用户 ID（Long 类型）；
 * - "userRole" 存储用户角色（"reader" 或 "admin"）。
 * </p>
 */
@Component // 声明为 Spring 组件，由容器管理
public class LoginInterceptor implements HandlerInterceptor {

    /** Session 中存储用户 ID 的 key */
    public static final String SESSION_USER_ID_KEY = "userId";

    /** Session 中存储用户角色的 key */
    public static final String SESSION_USER_ROLE_KEY = "userRole";

    /** 读者角色标识 */
    private static final String ROLE_READER = "reader";

    /** 管理员角色标识 */
    private static final String ROLE_ADMIN = "admin";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 规则1：认证相关接口始终放行（登录、注册、登出等）
        if (uri.startsWith("/api/auth/")) {
            return true;
        }
        // 规则2：GET 请求（浏览查询）始终放行
        if ("GET".equalsIgnoreCase(method)) {
            return true;
        }
        // 规则3：/api/admin/** 写操作需管理员登录
        if (uri.startsWith("/api/admin/")) {
            return requireRole(request, response, ROLE_ADMIN);
        }
        // 规则4：其余写操作（借阅、借阅车、个人信息修改）需读者登录
        return requireRole(request, response, ROLE_READER);
    }

    /**
     * 校验指定角色的登录态
     *
     * @param request      HTTP 请求对象（用于获取 Session）
     * @param response     HTTP 响应对象（用于写入 401 状态码）
     * @param requiredRole 需要的角色（reader/admin）
     * @return true 已登录且角色匹配，false 未登录或角色不匹配（已写入 401 响应）
     */
    private boolean requireRole(HttpServletRequest request, HttpServletResponse response,
                                String requiredRole) throws java.io.IOException {
        HttpSession session = request.getSession(false);
        // Session 不存在或未登录
        if (session == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Object role = session.getAttribute(SESSION_USER_ROLE_KEY);
        // 角色不匹配
        if (!requiredRole.equals(role)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }
}
