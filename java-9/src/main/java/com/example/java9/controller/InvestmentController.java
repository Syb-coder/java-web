package com.example.java9.controller;  // 声明控制器包路径

import com.example.java9.dto.InvestmentResponse;  // 导入投资响应 DTO
import com.example.java9.model.FinancialProduct;  // 导入理财产品实体
import com.example.java9.model.InvestmentOrder;  // 导入投资订单实体
import com.example.java9.model.User;  // 导入用户实体
import com.example.java9.service.InvestmentService;  // 导入投资服务
import com.example.java9.service.ProductService;  // 导入产品服务
import jakarta.servlet.http.HttpSession;  // 导入 Servlet HTTP 会话对象
import org.springframework.http.ResponseEntity;  // 导入 ResponseEntity，封装响应体与状态码
import org.springframework.web.bind.annotation.GetMapping;  // 导入 GET 请求映射注解
import org.springframework.web.bind.annotation.PathVariable;  // 导入路径变量绑定注解
import org.springframework.web.bind.annotation.PostMapping;  // 导入 POST 请求映射注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入类级路由映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入 REST 控制器注解

import java.util.List;  // 导入 List 集合
import java.util.Map;  // 导入 Map 集合
import java.util.stream.Collectors;  // 导入 Stream 收集器

/**
 * 投资订单查询与赎回控制器
 * <p>
 * 提供投资订单的查询（当前用户/全部/按订单号）与赎回接口。
 * GET /api/investments/all 与 GET /api/investments/{orderNo} 由拦截器放行，需处理 user 为 null 的场景。
 * </p>
 * <p>
 * 接口列表：
 * <ul>
 *   <li>GET /api/investments - 查询当前用户投资订单</li>
 *   <li>GET /api/investments/all - 查询所有投资订单</li>
 *   <li>GET /api/investments/{orderNo} - 根据订单号查询</li>
 *   <li>POST /api/investments/{orderId}/redeem - 赎回投资</li>
 * </ul>
 * </p>
 */
@RestController  // 声明为 REST 控制器，返回值自动序列化为 JSON
@RequestMapping("/api/investments")  // 类级路由前缀，本类所有接口均以 /api/investments 开头
public class InvestmentController {

    private final InvestmentService investmentService;  // 投资服务
    private final ProductService productService;  // 产品服务（用于冗余产品名）

    public InvestmentController(InvestmentService investmentService, ProductService productService) {  // 构造函数注入依赖
        this.investmentService = investmentService;  // 赋值投资服务
        this.productService = productService;  // 赋值产品服务
    }

    /**
     * 查询当前用户投资订单
     *
     * @param session HTTP 会话
     * @return 当前用户投资订单列表 / 401 未登录
     */
    @GetMapping  // 映射 GET /api/investments 请求
    public ResponseEntity<List<InvestmentResponse>> myInvestments(HttpSession session) {
        User user = (User) session.getAttribute(UserController.SESSION_USER_KEY);  // 从 Session 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        List<InvestmentResponse> list = investmentService.findByUserId(user.getId()).stream()  // 查询当前用户投资订单
                .map(this::toResponse)  // 逐条转换为响应 DTO
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 投资订单列表
    }

    /**
     * 查询所有投资订单
     *
     * @return 全部投资订单列表
     */
    @GetMapping("/all")  // 映射 GET /api/investments/all 请求
    public ResponseEntity<List<InvestmentResponse>> listAll() {
        List<InvestmentResponse> list = investmentService.findAll().stream()  // 查询全部投资订单
                .map(this::toResponse)  // 逐条转换为响应 DTO
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 全部投资订单
    }

    /**
     * 根据订单号查询
     *
     * @param orderNo 订单号
     * @return 投资订单详情 / 400 订单不存在
     */
    @GetMapping("/{orderNo}")  // 映射 GET /api/investments/{orderNo} 请求
    public ResponseEntity<InvestmentResponse> detail(@PathVariable String orderNo) {
        // @PathVariable 将 URL 路径中的 {orderNo} 绑定为方法参数
        try {
            InvestmentOrder order = investmentService.findByOrderNo(orderNo);  // 按订单号查询投资订单
            return ResponseEntity.ok(toResponse(order));  // 200 + 投资订单详情
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();  // 订单不存在，返回 400
        }
    }

    /**
     * 赎回投资
     *
     * @param orderId 投资订单 ID
     * @return 200 赎回成功 / 400 订单不存在或状态不允许赎回
     */
    @PostMapping("/{orderId}/redeem")  // 映射 POST /api/investments/{orderId}/redeem 请求
    public ResponseEntity<Map<String, String>> redeem(@PathVariable Long orderId) {
        // @PathVariable 绑定投资订单 ID
        try {
            investmentService.redeem(orderId);  // 调用投资服务执行赎回
            return ResponseEntity.ok(Map.of("message", "赎回成功"));  // 200 + 成功消息
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 订单不存在或状态不允许，返回 400
        }
    }

    /**
     * 将 InvestmentOrder 实体转换为响应 DTO
     * <p>
     * 通过 ProductService 查询产品名，冗余至响应中便于前端展示。
     * 产品不存在时 productName 留空，避免阻断列表渲染。
     * </p>
     *
     * @param o 投资订单实体
     * @return 投资订单响应 DTO
     */
    private InvestmentResponse toResponse(InvestmentOrder o) {
        // 使用 DTO 而非直接返回实体：冗余产品名便于前端展示，且可裁剪敏感字段
        InvestmentResponse resp = new InvestmentResponse();  // 创建响应 DTO 对象
        resp.setId(o.getId());  // 设置订单 ID
        resp.setOrderNo(o.getOrderNo());  // 设置订单号
        resp.setUserId(o.getUserId());  // 设置用户 ID
        resp.setProductId(o.getProductId());  // 设置产品 ID
        // 冗余产品名，产品被删除时留空避免阻断展示
        try {
            FinancialProduct product = productService.findById(o.getProductId());  // 查询关联产品
            resp.setProductName(product.getName());  // 设置产品名
        } catch (IllegalArgumentException e) {
            resp.setProductName(null);  // 产品不存在时留空
        }
        resp.setAmount(o.getAmount());  // 设置投资金额
        resp.setExpectedReturn(o.getExpectedReturn());  // 设置预期收益
        resp.setStatus(o.getStatus().name());  // 设置订单状态（枚举转字符串）
        resp.setCreatedAt(o.getCreatedAt());  // 设置创建时间
        return resp;  // 返回 DTO
    }
}
