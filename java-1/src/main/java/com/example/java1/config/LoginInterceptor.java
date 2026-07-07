// 声明包路径，归类为 config 配置层，存放拦截器与 Web 配置
package com.example.java1.config;

// 以下导入控制器与实体，用于引用 Session 键名常量与判定登录态类型
import com.example.java1.controller.AuthController; // 引入认证控制器，复用其 Session 键名常量
import com.example.java1.controller.ReaderController; // 引入读者控制器，预留扩展（当前未直接使用）
import com.example.java1.model.AdminUser; // 引入管理员实体，用于类型判定管理员登录态
import com.example.java1.model.Reader; // 引入读者实体，预留扩展（当前未直接使用）
// 以下导入 Servlet 相关类型，拦截器需操作请求与响应
import jakarta.servlet.http.HttpServletRequest; // 引入 HTTP 请求对象，用于读取 URI 与方法
import jakarta.servlet.http.HttpServletResponse; // 引入 HTTP 响应对象，用于写入 401 状态码
import jakarta.servlet.http.HttpSession; // 引入 HTTP 会话，用于读取登录态
import org.springframework.http.HttpStatus; // 引入 HTTP 状态码枚举，使用语义化常量
import org.springframework.stereotype.Component; // 引入 @Component，声明为 Spring 组件供容器扫描
import org.springframework.web.servlet.HandlerInterceptor; // 引入拦截器接口，实现请求前置拦截

/**
 * 登录拦截器
 * <p>
 * 保护后台管理 API 与读者写操作。规则如下：
 * <ul>
 *   <li>/api/auth/** 与 /api/readers/login 始终放行；</li>
 *   <li>GET 请求（查询浏览）放行；</li>
 *   <li>/api/admin/** 必须管理员登录；</li>
 *   <li>读者借还书 POST /api/borrows/** 需管理员登录（校园场景由管理员代办借还）；</li>
 *   <li>读者管理 POST/PUT/DELETE 需管理员登录；</li>
 *   <li>未登录返回 401。</li>
 * </ul>
 * </p>
 * <p>
 * 设计说明：采用"先放行公开接口、再按方法粗粒度判定"的策略，
 * 兼顾安全与性能——读操作无需鉴权以提升师生检索体验，
 * 写操作统一要求管理员登录态以保障数据权威性，符合校园图书代办借还的真实场景。
 * </p>
 */
@Component // 声明为 Spring 组件，由容器扫描注册为 Bean，供 WebMvcConfig 注入
public class LoginInterceptor implements HandlerInterceptor {

    @Override // 重写父类前置拦截方法，在 Controller 处理前执行鉴权
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception { // 返回 true 放行，false 中断请求
        String uri = request.getRequestURI(); // 获取请求路径，用于按路径分支判定
        String method = request.getMethod(); // 获取 HTTP 方法，用于区分读写操作

        // 规则1：认证相关接口始终放行（管理员登录、登出、当前用户、修改密码由会话内部校验）
        // 原因：登录接口本身不能要求已登录；登出/me/change-password 内部已通过 Session 校验
        if (uri.startsWith("/api/auth/login") || uri.startsWith("/api/auth/logout")
                || uri.startsWith("/api/auth/me") || uri.startsWith("/api/auth/change-password")) { // 命中认证模块任意子路径
            return true; // 放行，由对应 Controller 内部处理登录态
        }
        // 规则2：读者登录接口始终放行
        // 原因：读者登录是获取登录态的入口，不能被拦截器阻断
        if (uri.equals("/api/readers/login")) { // 精确匹配读者登录路径，避免误放行读者管理写操作
            return true; // 放行读者登录请求
        }
        // 规则3：GET 请求（查询浏览）放行
        // 原因：查询为幂等读操作，不改变数据状态，对师生公开以提升检索体验
        if ("GET".equalsIgnoreCase(method)) { // 忽略大小写判定 GET 方法
            return true; // 放行所有 GET 查询请求
        }

        // 规则4：其余写操作需管理员登录（图书管理、读者管理、借还书、续借）
        // 原因：校园场景下借还书与档案维护由管理员代办，统一要求管理员登录态
        return requireAdmin(request, response); // 交由管理员鉴权方法处理，未登录则写入 401
    }

    /**
     * 校验管理员登录态
     *
     * @return true 已登录，false 已写入 401 响应
     */
    private boolean requireAdmin(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException { // 抛出 IO 异常由框架统一处理
        HttpSession session = request.getSession(false); // 参数 false：不创建新会话，仅获取已有会话，避免无谓会话创建
        if (session == null) { // 无会话即未登录场景
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 写入 401 状态码，前端引导登录
            return false; // 中断请求，不进入 Controller
        }
        Object user = session.getAttribute(AuthController.SESSION_ADMIN_KEY); // 读取管理员 Session 属性
        if (!(user instanceof AdminUser)) { // 类型判定：非 AdminUser 则视为未登录或登录态非法
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 写入 401 状态码，前端引导登录
            return false; // 中断请求，不进入 Controller
        }
        return true; // 管理员已登录，放行进入 Controller
    }
}
