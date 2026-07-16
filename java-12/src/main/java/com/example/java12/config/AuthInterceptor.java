package com.example.java12.config;  // 配置层包，存放拦截器与 Web 配置

import com.example.java12.model.Moderator;  // 版主关联实体
import com.example.java12.model.Role;  // 角色枚举
import com.example.java12.model.User;  // 用户实体
import com.example.java12.model.UserStatus;  // 用户状态枚举
import com.example.java12.repository.ModeratorRepository;  // 版主数据访问层
import com.example.java12.repository.UserRepository;  // 用户数据访问层
import com.example.java12.util.TokenUtil;  // Token 工具类
import jakarta.servlet.http.HttpServletRequest;  // HTTP 请求
import jakarta.servlet.http.HttpServletResponse;  // HTTP 响应
import org.springframework.stereotype.Component;  // Spring 组件注解
import org.springframework.web.servlet.HandlerInterceptor;  // 拦截器接口

import java.util.List;  // 列表
import java.util.Optional;  // Optional 包装类

/**
 * 认证与权限拦截器
 * <p>
 * 拦截所有 /api/** 路径，按以下规则校验：
 * <ol>
 *   <li>OPTIONS 预检请求直接放行（CORS 支持）</li>
 *   <li>尝试从 Authorization 请求头解析 Token，获取当前登录用户并存入 request 属性</li>
 *   <li>/api/admin/** 路径要求 ADMIN 角色，否则返回 403</li>
 *   <li>/api/moderator/** 路径要求版主权限或 ADMIN 角色，否则返回 403</li>
 *   <li>GET 请求且非 /api/user/** 路径放行（游客可浏览公开内容）</li>
 *   <li>POST /api/auth/login 和 /api/auth/register 放行（公开接口）</li>
 *   <li>其余写操作要求已登录，否则返回 401</li>
 * </ol>
 * </p>
 */
@Component  // 声明为 Spring 组件，由容器管理
public class AuthInterceptor implements HandlerInterceptor {

    /** request 属性中存储当前登录用户的 key */
    public static final String CURRENT_USER_KEY = "currentUser";

    /** Token 工具类 */
    private final TokenUtil tokenUtil;

    /** 用户数据访问层 */
    private final UserRepository userRepository;

    /** 版主关联数据访问层（用于版主权限校验） */
    private final ModeratorRepository moderatorRepository;

    /**
     * 构造器注入依赖
     *
     * @param tokenUtil           Token 工具类
     * @param userRepository      用户数据访问层
     * @param moderatorRepository 版主关联数据访问层
     */
    public AuthInterceptor(TokenUtil tokenUtil, UserRepository userRepository, ModeratorRepository moderatorRepository) {
        this.tokenUtil = tokenUtil;
        this.userRepository = userRepository;
        this.moderatorRepository = moderatorRepository;
    }

    /**
     * 请求预处理：校验登录状态与权限
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @return true 放行，false 拦截
     * @throws Exception 写入响应时可能抛出 IO 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 尝试从 Authorization 请求头解析 Token，获取当前用户
        User currentUser = resolveUser(request);
        request.setAttribute(CURRENT_USER_KEY, currentUser);

        String path = request.getRequestURI();
        String method = request.getMethod();

        // 3. 管理员路径校验：/api/admin/** 要求 ADMIN 角色
        if (path.startsWith("/api/admin/")) {
            if (currentUser == null) {
                sendError(response, 401, "请先登录");
                return false;
            }
            if (currentUser.getRole() != Role.ADMIN) {
                sendError(response, 403, "无管理员权限");
                return false;
            }
            return true;
        }

        // 4. 版主路径校验：/api/moderator/** 要求版主权限或 ADMIN 角色
        if (path.startsWith("/api/moderator/")) {
            if (currentUser == null) {
                sendError(response, 401, "请先登录");
                return false;
            }
            // 管理员自动拥有版主权限
            if (currentUser.getRole() == Role.ADMIN) {
                return true;
            }
            // 检查是否是任何板块的版主
            List<Moderator> moderations = moderatorRepository.findByUserId(currentUser.getId());
            if (moderations.isEmpty()) {
                sendError(response, 403, "无版主权限");
                return false;
            }
            return true;
        }

        // 5. 公开 GET 路径放行（游客可浏览，但 /api/user/** 除外）
        if ("GET".equalsIgnoreCase(method) && !path.startsWith("/api/user/")) {
            return true;
        }

        // 6. 公开 POST 路径放行（登录、注册、管理员登录）
        if ("POST".equalsIgnoreCase(method) && (path.equals("/api/auth/login") || path.equals("/api/auth/register") || path.equals("/api/auth/admin/login"))) {
            return true;
        }

        // 7. 其余写操作要求已登录
        if (currentUser == null) {
            sendError(response, 401, "请先登录");
            return false;
        }

        return true;
    }

    /**
     * 从请求头解析 Token 并获取当前登录用户
     * <p>
     * Token 格式：Authorization: Bearer {token}
     * 如果 Token 无效或用户已被封禁，返回 null。
     * </p>
     *
     * @param request HTTP 请求
     * @return 当前登录用户（未登录时返回 null）
     */
    private User resolveUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);  // 去掉 "Bearer " 前缀
        if (!tokenUtil.isValid(token)) {
            return null;
        }
        Long userId = tokenUtil.getUserId(token);
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isEmpty()) {
            return null;
        }
        User user = optional.get();
        // 封禁用户的 Token 自动失效
        if (user.getStatus() == UserStatus.BANNED) {
            tokenUtil.removeToken(token);
            return null;
        }
        return user;
    }

    /**
     * 向客户端写入错误响应（JSON 格式）
     *
     * @param response HTTP 响应
     * @param code     状态码
     * @param message  错误信息
     * @throws Exception 写入时可能抛出 IO 异常
     */
    private void sendError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + message + "\",\"data\":null}");
    }
}
