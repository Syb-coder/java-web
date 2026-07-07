// 声明包路径
package com.example.java2.config;

// 导入 Controller 与 Model
import com.example.java2.controller.AuthController;
import com.example.java2.controller.UserController;
import com.example.java2.model.AdminUser;
import com.example.java2.model.User;

// 导入 Servlet 与 Spring Web 注解
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 *
 * <p>保护后台管理 API 与前台用户写操作。规则如下：
 * <ul>
 *   <li>认证相关接口（/api/auth/**、/api/users/register|login|logout）始终放行；</li>
 *   <li>GET 请求（前台浏览查询）放行；</li>
 *   <li>/api/admin/** 必须管理员登录；</li>
 *   <li>其余 POST/PUT/DELETE 请求（收藏/点赞/评论/答题/修改密码）必须前台用户登录；</li>
 *   <li>未登录返回 401。</li>
 * </ul>
 * </p>
 */
// @Component 将本拦截器注册为 Spring Bean，使其可被 WebMvcConfig 通过构造注入自动装配，
// 同时便于单元测试中替换为 mock 实例验证拦截逻辑
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 提前抽取 URI 与 HTTP 方法，后续多条规则均需据此判断，避免重复取值
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 规则1：管理员认证相关接口始终放行（login/logout/me；change-password 由会话内部校验）
        // 为什么放行 login：登录入口本身不能要求已登录，否则形成"鸡生蛋"死循环，永远无法登录
        // 为什么放行 logout：登出是无害操作，即便未登录调用也无副作用，强制校验反而冗余
        // 为什么放行 me：me 接口内部根据 session 返回登录态或匿名信息，由 controller 自行处理，拦截器无需介入
        // 注意：change-password 不在此列，因其为高危写操作，需在 controller 内部再次校验 session 真实性以防 CSRF
        if (uri.startsWith("/api/auth/login") || uri.startsWith("/api/auth/logout")
                || uri.startsWith("/api/auth/me")) {
            return true;
        }
        // 规则2：前台用户注册/登录/登出始终放行
        // 原因同规则1：这些是匿名用户进入系统的入口接口，强制登录会阻断首次访问
        if (uri.equals("/api/users/register") || uri.equals("/api/users/login")
                || uri.equals("/api/users/logout")) {
            return true;
        }
        // 规则3：GET 请求（前台浏览查询）放行
        // 前台浏览查询是无害的读操作，不修改任何数据，强制登录会损害匿名用户体验与 SEO 抓取
        if ("GET".equalsIgnoreCase(method)) {
            return true;
        }

        // 规则4：/api/admin/** 写操作需管理员登录
        // 后台管理接口涉及文章/分类/题库/用户/评论的增删改，权限要求高，必须校验管理员身份
        if (uri.startsWith("/api/admin/")) {
            return requireAdmin(request, response);
        }

        // 规则5：其余写操作（收藏/点赞/评论/答题/修改密码/文章CRUD/题库CRUD）需前台用户登录，
        //       但文章/题库/分类的 admin 子路径之外的管理操作仍走规则5：
        //       为兼容后台对文章/题库的 CRUD（/api/articles/admin/**、/api/questions/admin/**），
        //       这些路径由管理员完成，因此也允许管理员登录态。
        // 为什么单独识别 admin 子路径：文章/题库的 CRUD 路径分散在 /api/articles、/api/questions 下，
        // 若不单独识别会落入规则5的 requireUser，导致管理员用前台用户 session 校验而误判未授权
        if (uri.startsWith("/api/articles/admin/") || uri.startsWith("/api/questions/admin/")) {
            return requireAdmin(request, response);
        }

        // 其余写操作需前台用户登录
        // 覆盖收藏/点赞/评论/答题/修改密码等前台用户写操作，确保数据归属可追溯
        return requireUser(request, response);
    }

    /**
     * 校验管理员登录态
     *
     * @return true 已登录，false 已写入 401 响应
     */
    private boolean requireAdmin(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        // getSession(false)：不创建新 session，未登录时返回 null，避免为匿名请求生成空 session 浪费内存
        HttpSession session = request.getSession(false);
        if (session == null) {
            // 无 session 即未登录，直接返回 401，不进入业务逻辑
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Object user = session.getAttribute(AuthController.SESSION_ADMIN_KEY);
        // instanceof 严格校验类型：防止前台用户 session 冒充管理员，仅 AdminUser 实例才视为合法管理员
        // instanceof 模式匹配的好处：类型判断与变量绑定一步完成（Java 16+ 特性），
        // 相比传统先 instanceof 再 (AdminUser) 强转，避免显式 cast 代码冗余与潜在 ClassCastException
        if (!(user instanceof AdminUser)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }

    /**
     * 校验前台用户登录态
     *
     * @return true 已登录，false 已写入 401 响应
     */
    private boolean requireUser(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {
        // getSession(false)：同 requireAdmin，不创建新 session，未登录直接判 null
        HttpSession session = request.getSession(false);
        if (session == null) {
            // 无 session 即未登录，返回 401 拒绝写操作，保护数据归属
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Object user = session.getAttribute(UserController.SESSION_USER_KEY);
        // instanceof 严格校验类型：防止管理员 session 越权操作前台用户写接口，仅 User 实例才视为合法前台用户
        // instanceof 模式匹配的好处：类型判断与变量绑定一步完成，无需显式强转，代码更简洁安全
        if (!(user instanceof User)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }
}
