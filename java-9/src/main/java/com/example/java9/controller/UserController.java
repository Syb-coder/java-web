package com.example.java9.controller;  // 声明控制器包路径

import com.example.java9.dto.AmountRequest;  // 导入充值/提现金额请求 DTO
import com.example.java9.dto.InvestRequest;  // 导入投资请求 DTO
import com.example.java9.dto.LoginRequest;  // 导入登录请求 DTO
import com.example.java9.dto.LoginResponse;  // 导入登录响应 DTO
import com.example.java9.dto.PaymentRequest;  // 导入支付请求 DTO
import com.example.java9.dto.RegisterRequest;  // 导入注册请求 DTO
import com.example.java9.dto.TicketRequest;  // 导入工单请求 DTO
import com.example.java9.dto.UserResponse;  // 导入用户响应 DTO
import com.example.java9.dto.VerifyRequest;  // 导入实名认证请求 DTO
import com.example.java9.model.Transaction;  // 导入流水实体
import com.example.java9.model.User;  // 导入用户实体
import com.example.java9.service.AuthService;  // 导入认证服务
import com.example.java9.service.InvestmentService;  // 导入投资服务
import com.example.java9.service.PaymentService;  // 导入支付服务
import com.example.java9.service.TicketService;  // 导入工单服务
import com.example.java9.service.UserService;  // 导入用户服务
import jakarta.servlet.http.HttpSession;  // 导入 Servlet HTTP 会话对象
import jakarta.validation.Valid;  // 导入 Bean Validation 校验注解
import org.springframework.http.ResponseEntity;  // 导入 ResponseEntity，封装响应体与状态码
import org.springframework.web.bind.annotation.GetMapping;  // 导入 GET 请求映射注解
import org.springframework.web.bind.annotation.PostMapping;  // 导入 POST 请求映射注解
import org.springframework.web.bind.annotation.RequestBody;  // 导入请求体绑定注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入类级路由映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入 REST 控制器注解

import java.math.BigDecimal;  // 导入高精度十进制类（金额计算）
import java.util.List;  // 导入 List 集合
import java.util.Map;  // 导入 Map 集合

/**
 * C端用户控制器
 * <p>
 * 提供用户注册、登录、实名认证、资产管理（余额/流水）、投资入口、支付入口、工单提交等接口。
 * </p>
 */
@RestController  // 声明为 REST 控制器，返回值自动序列化为 JSON
@RequestMapping("/api/users")  // 类级路由前缀，本类所有接口均以 /api/users 开头
public class UserController {

    /** Session 中存储当前登录用户的键名 */
    public static final String SESSION_USER_KEY = "loginUser";  // 定义 Session 键名常量，供其他类复用

    private final AuthService authService;  // 认证服务
    private final UserService userService;  // 用户服务
    private final InvestmentService investmentService;  // 投资服务
    private final PaymentService paymentService;  // 支付服务
    private final TicketService ticketService;  // 工单服务

    public UserController(AuthService authService, UserService userService,
                          InvestmentService investmentService, PaymentService paymentService,
                          TicketService ticketService) {  // 构造函数注入全部依赖 Bean
        this.authService = authService;  // 赋值认证服务
        this.userService = userService;  // 赋值用户服务
        this.investmentService = investmentService;  // 赋值投资服务
        this.paymentService = paymentService;  // 赋值支付服务
        this.ticketService = ticketService;  // 赋值工单服务
    }

    /**
     * 用户注册
     *
     * @param req 注册请求
     * @return 200 成功 / 400 用户名已存在
     */
    @PostMapping("/register")  // 映射 POST /api/users/register 请求
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest req) {
        // @Valid 触发 Bean Validation 校验 RegisterRequest 字段约束（如非空、长度）
        // @RequestBody 将请求体 JSON 反序列化为 RegisterRequest 对象
        try {
            authService.registerUser(req);  // 调用 Service 执行用户注册
            return ResponseEntity.ok(Map.of("message", "注册成功"));  // 200 + 成功消息
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 用户名已存在，返回 400 + 错误信息
        }
    }

    /**
     * 用户登录
     *
     * @param req     登录请求
     * @param session HTTP 会话
     * @return 登录响应
     */
    @PostMapping("/login")  // 映射 POST /api/users/login 请求
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpSession session) {
        // @RequestBody 绑定请求体为 LoginRequest 对象
        try {
            User user = authService.loginUserService(req);  // 调用 Service 校验账号密码
            session.setAttribute(SESSION_USER_KEY, user);  // 将用户信息写入 Session，标记登录态
            return ResponseEntity.ok(authService.buildUserLoginResponse(user));  // 200 + 登录响应
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));  // 账号或密码错误，返回 401
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));  // 账户被冻结，返回 403
        }
    }

    /**
     * 用户登出
     *
     * @param session HTTP 会话
     * @return 204
     */
    @PostMapping("/logout")  // 映射 POST /api/users/logout 请求
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();  // 使 Session 失效，清除登录态
        return ResponseEntity.noContent().build();  // 返回 204 No Content
    }

    /**
     * 实名认证
     *
     * @param req     实名请求
     * @param session HTTP 会话
     * @return 200 成功 / 400 身份证已存在 / 401 未登录
     */
    @PostMapping("/verify")  // 映射 POST /api/users/verify 请求
    public ResponseEntity<Map<String, String>> verify(@Valid @RequestBody VerifyRequest req, HttpSession session) {
        // @Valid 触发 VerifyRequest 字段校验
        User user = (User) session.getAttribute(SESSION_USER_KEY);  // 从 Session 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        try {
            userService.verify(user.getId(), req.getRealName(), req.getIdCard());  // 调用 Service 执行实名认证
            return ResponseEntity.ok(Map.of("message", "实名认证成功"));  // 200 + 成功消息
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 身份证已存在等错误，返回 400
        }
    }

    /**
     * 充值
     *
     * @param req     充值请求
     * @param session HTTP 会话
     * @return 充值后用户信息
     */
    @PostMapping("/recharge")  // 映射 POST /api/users/recharge 请求
    public ResponseEntity<UserResponse> recharge(@Valid @RequestBody AmountRequest req, HttpSession session) {
        // @Valid 校验 AmountRequest（金额必须为正等）
        User user = (User) session.getAttribute(SESSION_USER_KEY);  // 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        try {
            User updated = userService.recharge(user.getId(), req.getAmount(), req.getRemark());  // 调用 Service 执行充值
            session.setAttribute(SESSION_USER_KEY, updated);  // 刷新 Session 中的用户信息（余额已变更）
            return ResponseEntity.ok(toResponse(updated));  // 200 + 用户响应 DTO
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();  // 金额非法，返回 400
        }
    }

    /**
     * 提现
     *
     * @param req     提现请求
     * @param session HTTP 会话
     * @return 提现后用户信息
     */
    @PostMapping("/withdraw")  // 映射 POST /api/users/withdraw 请求
    public ResponseEntity<UserResponse> withdraw(@Valid @RequestBody AmountRequest req, HttpSession session) {
        // @Valid 校验提现金额参数
        User user = (User) session.getAttribute(SESSION_USER_KEY);  // 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        try {
            User updated = userService.withdraw(user.getId(), req.getAmount(), req.getRemark());  // 调用 Service 执行提现
            session.setAttribute(SESSION_USER_KEY, updated);  // 刷新 Session 中的用户信息
            return ResponseEntity.ok(toResponse(updated));  // 200 + 用户响应 DTO
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();  // 余额不足或金额非法，返回 400
        }
    }

    /**
     * 查询当前用户信息
     *
     * @param session HTTP 会话
     * @return 用户信息
     */
    @GetMapping("/me")  // 映射 GET /api/users/me 请求
    public ResponseEntity<UserResponse> me(HttpSession session) {
        User user = (User) session.getAttribute(SESSION_USER_KEY);  // 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        // 从数据库重新查询，保证余额最新
        User fresh = userService.findById(user.getId());  // 重新查询数据库获取最新用户数据
        session.setAttribute(SESSION_USER_KEY, fresh);  // 更新 Session 中的用户信息
        return ResponseEntity.ok(toResponse(fresh));  // 200 + 用户响应 DTO
    }

    /**
     * 查询当前用户资产流水
     *
     * @param session HTTP 会话
     * @return 流水列表
     */
    @GetMapping("/transactions")  // 映射 GET /api/users/transactions 请求
    public ResponseEntity<List<Transaction>> transactions(HttpSession session) {
        User user = (User) session.getAttribute(SESSION_USER_KEY);  // 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        return ResponseEntity.ok(userService.getUserTransactions(user.getId()));  // 200 + 流水列表
    }

    /**
     * 用户发起投资（快捷入口，转发到 InvestmentService）
     *
     * @param req     投资请求
     * @param session HTTP 会话
     * @return 200 成功 / 400 参数错误 / 401 未登录
     */
    @PostMapping("/invest")  // 映射 POST /api/users/invest 请求
    public ResponseEntity<Map<String, String>> invest(@Valid @RequestBody InvestRequest req, HttpSession session) {
        // @Valid 校验 InvestRequest（productId、amount 约束）
        User user = (User) session.getAttribute(SESSION_USER_KEY);  // 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        try {
            investmentService.invest(user.getId(), req.getProductId(), req.getAmount());  // 调用投资服务完成申购
            return ResponseEntity.ok(Map.of("message", "投资成功"));  // 200 + 成功消息
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 参数错误或状态不允许，返回 400
        }
    }

    /**
     * 用户发起支付（快捷入口，创建并确认支付）
     *
     * @param req     支付请求
     * @param session HTTP 会话
     * @return 200 成功 / 400 参数错误
     */
    @PostMapping("/pay")  // 映射 POST /api/users/pay 请求
    public ResponseEntity<Map<String, String>> pay(@Valid @RequestBody PaymentRequest req, HttpSession session) {
        // @Valid 校验 PaymentRequest 字段
        User user = (User) session.getAttribute(SESSION_USER_KEY);  // 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        try {
            var order = paymentService.createPayment(user.getId(), req.getMerchantId(),
                    req.getAmount(), req.getChannel(), req.getDescription());  // 创建支付订单
            paymentService.confirmPayment(order.getOrderNo());  // 立即确认支付（一键支付场景）
            return ResponseEntity.ok(Map.of("message", "支付成功", "orderNo", order.getOrderNo()));  // 200 + 成功消息与订单号
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 参数错误或状态不允许，返回 400
        }
    }

    /**
     * 提交客服工单
     *
     * @param req     工单请求
     * @param session HTTP 会话
     * @return 200 成功
     */
    @PostMapping("/tickets")  // 映射 POST /api/users/tickets 请求
    public ResponseEntity<Map<String, String>> createTicket(@Valid @RequestBody TicketRequest req, HttpSession session) {
        // @Valid 校验工单请求字段
        User user = (User) session.getAttribute(SESSION_USER_KEY);  // 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        ticketService.create(user.getId(), req.getTitle(), req.getDescription());  // 调用工单服务创建工单
        return ResponseEntity.ok(Map.of("message", "工单提交成功"));  // 200 + 成功消息
    }

    /**
     * 查询当前用户的工单列表
     *
     * @param session HTTP 会话
     * @return 工单列表
     */
    @GetMapping("/tickets")  // 映射 GET /api/users/tickets 请求
    public ResponseEntity<?> myTickets(HttpSession session) {
        User user = (User) session.getAttribute(SESSION_USER_KEY);  // 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        return ResponseEntity.ok(ticketService.findByUserId(user.getId()));  // 200 + 当前用户工单列表
    }

    /**
     * 将 User 实体转换为响应 DTO
     */
    private UserResponse toResponse(User user) {
        // 使用 DTO 而非直接返回实体：避免暴露密码等敏感字段，且可定制字段结构
        UserResponse resp = new UserResponse();  // 创建响应 DTO 对象
        resp.setId(user.getId());  // 设置用户 ID
        resp.setUsername(user.getUsername());  // 设置用户名
        resp.setRealName(user.getRealName());  // 设置真实姓名
        resp.setIdCard(user.getIdCard());  // 设置身份证号
        resp.setPhone(user.getPhone());  // 设置手机号
        resp.setBalance(user.getBalance());  // 设置账户余额
        resp.setVerified(user.getVerified());  // 设置是否已实名
        resp.setStatus(user.getStatus().name());  // 设置账户状态（枚举转字符串）
        resp.setCreatedAt(user.getCreatedAt());  // 设置创建时间
        return resp;  // 返回 DTO
    }
}
