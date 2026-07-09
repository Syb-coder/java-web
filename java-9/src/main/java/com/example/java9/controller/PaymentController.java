package com.example.java9.controller;  // 声明控制器包路径

import com.example.java9.dto.PaymentRequest;  // 导入支付请求 DTO
import com.example.java9.dto.PaymentResponse;  // 导入支付响应 DTO
import com.example.java9.model.Merchant;  // 导入商户实体
import com.example.java9.model.PaymentOrder;  // 导入支付订单实体
import com.example.java9.model.User;  // 导入用户实体
import com.example.java9.service.MerchantService;  // 导入商户服务
import com.example.java9.service.PaymentService;  // 导入支付服务
import jakarta.servlet.http.HttpSession;  // 导入 Servlet HTTP 会话对象
import jakarta.validation.Valid;  // 导入 Bean Validation 校验注解
import org.springframework.http.ResponseEntity;  // 导入 ResponseEntity，封装响应体与状态码
import org.springframework.web.bind.annotation.GetMapping;  // 导入 GET 请求映射注解
import org.springframework.web.bind.annotation.PathVariable;  // 导入路径变量绑定注解
import org.springframework.web.bind.annotation.PostMapping;  // 导入 POST 请求映射注解
import org.springframework.web.bind.annotation.RequestBody;  // 导入请求体绑定注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入类级路由映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入 REST 控制器注解

import java.util.List;  // 导入 List 集合
import java.util.Map;  // 导入 Map 集合
import java.util.stream.Collectors;  // 导入 Stream 收集器

/**
 * 支付订单管理控制器
 * <p>
 * 提供支付订单的创建、确认、退款与查询接口。
 * POST 写操作需 C 端用户登录态，GET 查询接口放行（按当前用户过滤时需登录）。
 * </p>
 * <p>
 * 接口列表：
 * <ul>
 *   <li>POST /api/payments - 创建支付订单</li>
 *   <li>POST /api/payments/{orderNo}/confirm - 确认支付</li>
 *   <li>POST /api/payments/{orderNo}/refund - 退款</li>
 *   <li>GET /api/payments - 查询当前用户支付订单</li>
 *   <li>GET /api/payments/all - 查询所有支付订单</li>
 *   <li>GET /api/payments/{orderNo} - 根据订单号查询</li>
 * </ul>
 * </p>
 */
@RestController  // 声明为 REST 控制器，返回值自动序列化为 JSON
@RequestMapping("/api/payments")  // 类级路由前缀，本类所有接口均以 /api/payments 开头
public class PaymentController {

    private final PaymentService paymentService;  // 支付服务
    private final MerchantService merchantService;  // 商户服务（用于冗余商户名）

    public PaymentController(PaymentService paymentService, MerchantService merchantService) {  // 构造函数注入依赖
        this.paymentService = paymentService;  // 赋值支付服务
        this.merchantService = merchantService;  // 赋值商户服务
    }

    /**
     * 创建支付订单
     *
     * @param req     支付请求（merchantId 可空、amount、channel、description）
     * @param session HTTP 会话
     * @return 200 创建成功 / 400 参数错误 / 401 未登录
     */
    @PostMapping  // 映射 POST /api/payments 请求
    public ResponseEntity<Map<String, String>> create(@Valid @RequestBody PaymentRequest req, HttpSession session) {
        // @Valid 触发 PaymentRequest 字段校验
        // @RequestBody 绑定请求体为 PaymentRequest 对象
        User user = (User) session.getAttribute(UserController.SESSION_USER_KEY);  // 从 Session 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        try {
            PaymentOrder order = paymentService.createPayment(user.getId(), req.getMerchantId(),
                    req.getAmount(), req.getChannel(), req.getDescription());  // 调用支付服务创建订单
            return ResponseEntity.ok(Map.of("message", "支付订单已创建", "orderNo", order.getOrderNo()));  // 200 + 成功消息与订单号
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 参数错误或状态不允许，返回 400
        }
    }

    /**
     * 确认支付
     *
     * @param orderNo 订单号
     * @return 200 确认成功 / 400 订单不存在或状态不允许
     */
    @PostMapping("/{orderNo}/confirm")  // 映射 POST /api/payments/{orderNo}/confirm 请求
    public ResponseEntity<Map<String, String>> confirm(@PathVariable String orderNo) {
        // @PathVariable 绑定订单号
        try {
            paymentService.confirmPayment(orderNo);  // 调用支付服务确认支付
            return ResponseEntity.ok(Map.of("message", "支付成功"));  // 200 + 成功消息
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 订单不存在或状态不允许，返回 400
        }
    }

    /**
     * 退款
     *
     * @param orderNo 订单号
     * @return 200 退款成功 / 400 订单不存在或状态不允许
     */
    @PostMapping("/{orderNo}/refund")  // 映射 POST /api/payments/{orderNo}/refund 请求
    public ResponseEntity<Map<String, String>> refund(@PathVariable String orderNo) {
        // @PathVariable 绑定订单号
        try {
            paymentService.refund(orderNo);  // 调用支付服务执行退款
            return ResponseEntity.ok(Map.of("message", "退款成功"));  // 200 + 成功消息
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 订单不存在或状态不允许，返回 400
        }
    }

    /**
     * 查询当前用户支付订单
     *
     * @param session HTTP 会话
     * @return 当前用户支付订单列表 / 401 未登录
     */
    @GetMapping  // 映射 GET /api/payments 请求
    public ResponseEntity<List<PaymentResponse>> myPayments(HttpSession session) {
        User user = (User) session.getAttribute(UserController.SESSION_USER_KEY);  // 从 Session 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        List<PaymentResponse> list = paymentService.findByUserId(user.getId()).stream()  // 查询当前用户支付订单
                .map(this::toResponse)  // 逐条转换为响应 DTO
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 支付订单列表
    }

    /**
     * 查询所有支付订单
     *
     * @return 全部支付订单列表
     */
    @GetMapping("/all")  // 映射 GET /api/payments/all 请求
    public ResponseEntity<List<PaymentResponse>> listAll() {
        List<PaymentResponse> list = paymentService.findAll().stream()  // 查询全部支付订单
                .map(this::toResponse)  // 逐条转换为响应 DTO
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 全部支付订单
    }

    /**
     * 根据订单号查询
     *
     * @param orderNo 订单号
     * @return 支付订单详情 / 400 订单不存在
     */
    @GetMapping("/{orderNo}")  // 映射 GET /api/payments/{orderNo} 请求
    public ResponseEntity<PaymentResponse> detail(@PathVariable String orderNo) {
        // @PathVariable 绑定订单号
        try {
            PaymentOrder order = paymentService.findByOrderNo(orderNo);  // 按订单号查询支付订单
            return ResponseEntity.ok(toResponse(order));  // 200 + 支付订单详情
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();  // 订单不存在，返回 400
        }
    }

    /**
     * 将 PaymentOrder 实体转换为响应 DTO
     * <p>
     * 商户 ID 非空时通过 MerchantService 查询商户名，冗余至响应中便于展示。
     * 商户不存在时 merchantName 留空。
     * </p>
     *
     * @param o 支付订单实体
     * @return 支付订单响应 DTO
     */
    private PaymentResponse toResponse(PaymentOrder o) {
        // 使用 DTO 而非直接返回实体：冗余商户名便于前端展示，且可裁剪敏感字段
        PaymentResponse resp = new PaymentResponse();  // 创建响应 DTO 对象
        resp.setId(o.getId());  // 设置订单 ID
        resp.setOrderNo(o.getOrderNo());  // 设置订单号
        resp.setUserId(o.getUserId());  // 设置用户 ID
        resp.setMerchantId(o.getMerchantId());  // 设置商户 ID
        // 冗余商户名，无商户或商户被删除时留空
        if (o.getMerchantId() != null) {  // 判断商户 ID 非空
            try {
                Merchant merchant = merchantService.findById(o.getMerchantId());  // 查询关联商户
                resp.setMerchantName(merchant.getMerchantName());  // 设置商户名
            } catch (IllegalArgumentException e) {
                resp.setMerchantName(null);  // 商户不存在时留空
            }
        }
        resp.setAmount(o.getAmount());  // 设置支付金额
        resp.setChannel(o.getChannel().name());  // 设置支付渠道（枚举转字符串）
        resp.setStatus(o.getStatus().name());  // 设置订单状态（枚举转字符串）
        resp.setDescription(o.getDescription());  // 设置订单描述
        resp.setCreatedAt(o.getCreatedAt());  // 设置创建时间
        return resp;  // 返回 DTO
    }
}
