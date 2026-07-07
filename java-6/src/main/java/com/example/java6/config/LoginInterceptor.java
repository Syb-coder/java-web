package com.example.java6.config; // 声明当前类所在的包路径，属于应用配置层（Config）

import com.example.java6.controller.AuthController; // 导入认证控制器，引用其 SESSION_USER_KEY 常量以读取 Session 中的登录用户
import com.example.java6.model.AdminUser; // 导入管理员实体类，用于校验 Session 中的对象类型
import jakarta.servlet.http.HttpServletRequest; // 导入 Jakarta 规范的 HTTP 请求对象，用于获取请求 URI 与方法
import jakarta.servlet.http.HttpServletResponse; // 导入 Jakarta 规范的 HTTP 响应对象，用于设置 401 状态码
import jakarta.servlet.http.HttpSession; // 导入 Jakarta 规范的 HTTP Session，用于校验登录态
import org.springframework.http.HttpStatus; // 导入 HTTP 状态码枚举，使用 UNAUTHORIZED(401) 表示未登录
import org.springframework.stereotype.Component; // 导入组件注解，将此类注册为 Spring Bean 供 WebMvcConfig 注入
import org.springframework.web.servlet.HandlerInterceptor; // 导入拦截器接口，实现 preHandle 在 Controller 执行前进行登录态校验

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
@Component // 注册为 Spring 组件，可被 WebMvcConfig 通过构造器注入使用
public class LoginInterceptor implements HandlerInterceptor { // 实现 HandlerInterceptor 接口，重写 preHandle 方法在请求到达 Controller 前进行拦截

    @Override // 重写父接口的 preHandle 方法，返回 true 放行，返回 false 中止请求
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception { // handler 参数为即将执行的 Controller 方法处理器
        String uri = request.getRequestURI(); // 获取请求的 URI 路径（不含 query string），用于按路径放行判断
        String method = request.getMethod(); // 获取 HTTP 方法（GET/POST/PUT/DELETE），用于按方法放行判断

        // 规则1：认证相关接口始终放行
        // 业务理由：登录、登出、获取当前用户信息等接口本身不能要求已登录，否则用户无法登录系统
        if (uri.startsWith("/api/auth/")) { // 判断是否为认证模块路径
            return true; // 放行认证接口
        }

        // 规则2：GET 请求（前台浏览查询）放行
        // 业务理由：本网站为宣传性质官网，所有浏览类查询对公众开放，无需登录
        if ("GET".equalsIgnoreCase(method)) { // 判断是否为 GET 方法（忽略大小写）
            return true; // 放行所有 GET 请求
        }

        // 规则3：前台举报提交 POST /api/reports 放行（普通用户也可举报）
        // 业务理由：举报中心面向公众，鼓励网民举报网络违法信息，强制登录会降低举报意愿
        // 注意：仅对 POST /api/reports 精确匹配放行，POST /api/reports/{id} 等子路径不放行
        if ("POST".equalsIgnoreCase(method) && "/api/reports".equals(uri)) { // 同时校验方法与精确路径
            return true; // 放行举报提交接口
        }

        // 规则4：其余写操作需登录校验
        // 业务理由：新闻、知识、法规的增删改以及举报的处置、删除均为后台管理操作，必须由登录管理员执行
        HttpSession session = request.getSession(false); // getSession(false) 表示不创建新 Session，仅获取已存在的 Session，避免无谓的 Session 创建开销
        if (session == null) { // 无 Session 表示从未登录
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 设置响应状态码为 401 未授权
            return false; // 中止请求，阻止访问 Controller
        }
        Object user = session.getAttribute(AuthController.SESSION_USER_KEY); // 从 Session 中取出登录时存储的管理员对象
        if (!(user instanceof AdminUser)) { // 校验 Session 中的对象确实是 AdminUser 类型（防止伪造 Session 数据）
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 类型不匹配视为未登录，返回 401
            return false; // 中止请求
        }
        return true; // 已登录且 Session 中存在合法管理员，放行请求
    }
}
