// 声明当前类所在的包路径，Spring Boot 通过包扫描机制加载该拦截器
package com.example.java3.config;

// 导入 AuthController，用于读取会话 key 常量
import com.example.java3.controller.AuthController;
// 导入 UserController，用于读取学生会话 key 常量
import com.example.java3.controller.UserController;
// 导入管理员实体，用于 instanceof 类型校验
import com.example.java3.model.AdminUser;
// 导入学生用户实体，用于 instanceof 类型校验
import com.example.java3.model.User;
// 导入 HttpServletRequest，用于读取请求 URI 与 method
import jakarta.servlet.http.HttpServletRequest;
// 导入 HttpServletResponse，用于写出 401 状态码
import jakarta.servlet.http.HttpServletResponse;
// 导入 HttpSession，用于读取会话中的登录用户
import jakarta.servlet.http.HttpSession;
// 导入 HttpStatus，使用其 UNAUTHORIZED 常量（401）
import org.springframework.http.HttpStatus;
// 导入 @Component，将拦截器注册为 Spring Bean，可被 WebMvcConfig 注入
import org.springframework.stereotype.Component;
// 导入 HandlerInterceptor 接口，实现 preHandle 完成 handler 前置拦截
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
// @Component：将该类注册为 Spring 容器管理的 Bean，便于 WebMvcConfig 通过构造器注入
@Component
public class LoginInterceptor implements HandlerInterceptor {

    // 重写 preHandle 方法，在 Controller 方法执行前进行登录态校验
    @Override
    // 返回 true 表示放行，返回 false 表示拦截（不再继续后续 handler）
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 获取本次请求的 URI 路径，用于后续规则匹配
        String uri = request.getRequestURI();
        // 获取 HTTP 方法（GET/POST/PUT/DELETE），用于判断是否为读操作
        String method = request.getMethod();

        // 规则1：认证相关接口始终放行（学生/管理员登录、登出、获取当前用户、修改密码）
        // 这些接口本身不依赖登录态，否则会形成循环依赖（登录前就需要登录态）
        if (uri.startsWith("/api/auth/login") || uri.startsWith("/api/auth/admin/login")
                || uri.startsWith("/api/auth/logout") || uri.startsWith("/api/auth/me")
                || uri.startsWith("/api/auth/change-password")) {
            // 放行：交由后续 Controller 处理
            return true;
        }
        // 规则2：前台用户注册/登录/登出始终放行
        // register/login 是登录前操作，logout 即使会话已失效也应允许
        if (uri.equals("/api/users/register") || uri.equals("/api/users/login")
                || uri.equals("/api/users/logout")) {
            // 放行
            return true;
        }
        // 规则3：GET 请求（前台浏览查询）放行
        // 浏览商品、查看详情等读操作无需登录，提升用户体验
        if ("GET".equalsIgnoreCase(method)) {
            // 放行
            return true;
        }

        // 规则4：/api/admin/** 写操作需管理员登录
        // 管理员专属接口（审核、封禁、公告管理等）必须校验管理员身份
        if (uri.startsWith("/api/admin/")) {
            // 委托 requireAdmin 校验，未通过则返回 401
            return requireAdmin(request, response);
        }

        // 规则5：其余写操作需前台用户登录
        // 发布商品、下单、评论、私信等写操作必须先登录
        return requireUser(request, response);
    }

    /**
     * 校验管理员登录态
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @return true 已登录管理员；false 未登录，已写入 401
     */
    private boolean requireAdmin(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        // getSession(false)：不创建新会话，仅返回已存在的会话；若无则返回 null
        HttpSession session = request.getSession(false);
        // 会话不存在，说明从未登录过
        if (session == null) {
            // 设置 401 Unauthorized 状态码
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // 拦截请求
            return false;
        }
        // 从会话中读取管理员对象（key 为 AuthController.SESSION_ADMIN_KEY）
        Object user = session.getAttribute(AuthController.SESSION_ADMIN_KEY);
        // 使用 instanceof 校验类型，防止伪造会话；Java 16+ 模式匹配
        if (!(user instanceof AdminUser)) {
            // 类型不匹配或为 null，设置 401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // 拦截
            return false;
        }
        // 校验通过，放行
        return true;
    }

    /**
     * 校验前台用户登录态
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @return true 已登录；false 未登录，已写入 401
     */
    private boolean requireUser(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        // 不创建新会话，仅获取已有会话
        HttpSession session = request.getSession(false);
        // 会话不存在即未登录
        if (session == null) {
            // 401 Unauthorized
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        // 从会话中读取前台用户对象（key 为 UserController.SESSION_USER_KEY）
        Object user = session.getAttribute(UserController.SESSION_USER_KEY);
        // 类型校验：必须为 User 实例
        if (!(user instanceof User)) {
            // 401 Unauthorized
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        // 放行
        return true;
    }
}
