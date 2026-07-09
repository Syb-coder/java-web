package com.example.java9.controller;  // 声明控制器包路径

import com.example.java9.dto.InvestRequest;  // 导入投资请求 DTO
import com.example.java9.dto.ProductResponse;  // 导入产品响应 DTO
import com.example.java9.model.FinancialProduct;  // 导入理财产品实体
import com.example.java9.model.User;  // 导入用户实体
import com.example.java9.service.InvestmentService;  // 导入投资服务
import com.example.java9.service.ProductService;  // 导入产品服务
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
 * 理财产品展示与查询控制器
 * <p>
 * 提供 C 端可访问的产品查询与投资接口。管理操作不在此处，由 AdminController 承担。
 * GET 请求放行，POST 投资操作需 C 端用户登录态。
 * </p>
 * <p>
 * 接口列表：
 * <ul>
 *   <li>GET /api/products - 查询在售产品</li>
 *   <li>GET /api/products/all - 查询所有产品</li>
 *   <li>GET /api/products/{id} - 查询产品详情</li>
 *   <li>POST /api/products/invest - 用户投资</li>
 * </ul>
 * </p>
 */
@RestController  // 声明为 REST 控制器，返回值自动序列化为 JSON
@RequestMapping("/api/products")  // 类级路由前缀，本类所有接口均以 /api/products 开头
public class ProductController {

    private final ProductService productService;  // 产品服务
    private final InvestmentService investmentService;  // 投资服务

    public ProductController(ProductService productService, InvestmentService investmentService) {  // 构造函数注入依赖
        this.productService = productService;  // 赋值产品服务
        this.investmentService = investmentService;  // 赋值投资服务
    }

    /**
     * 查询在售产品
     *
     * @return 在售产品响应列表
     */
    @GetMapping  // 映射 GET /api/products 请求（无子路径，直接访问类前缀）
    public ResponseEntity<List<ProductResponse>> listOnSale() {
        List<ProductResponse> list = productService.findOnSale().stream()  // 查询在售产品并转为 Stream
                .map(this::toResponse)  // 逐条转换为响应 DTO
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 在售产品列表
    }

    /**
     * 查询所有产品
     *
     * @return 全部产品响应列表
     */
    @GetMapping("/all")  // 映射 GET /api/products/all 请求
    public ResponseEntity<List<ProductResponse>> listAll() {
        List<ProductResponse> list = productService.findAll().stream()  // 查询全部产品并转为 Stream
                .map(this::toResponse)  // 逐条转换为响应 DTO
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 全部产品列表
    }

    /**
     * 查询产品详情
     *
     * @param id 产品 ID
     * @return 产品详情 / 400 产品不存在
     */
    @GetMapping("/{id}")  // 映射 GET /api/products/{id} 请求
    public ResponseEntity<ProductResponse> detail(@PathVariable Long id) {
        // @PathVariable 绑定产品 ID
        try {
            FinancialProduct product = productService.findById(id);  // 按 ID 查询产品
            return ResponseEntity.ok(toResponse(product));  // 200 + 产品详情 DTO
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();  // 产品不存在，返回 400
        }
    }

    /**
     * 用户投资
     * <p>
     * 从 session 获取当前登录用户，调用投资服务完成申购。
     * </p>
     *
     * @param req     投资请求（productId、amount）
     * @param session HTTP 会话
     * @return 200 投资成功 / 400 参数错误 / 401 未登录
     */
    @PostMapping("/invest")  // 映射 POST /api/products/invest 请求
    public ResponseEntity<Map<String, String>> invest(@Valid @RequestBody InvestRequest req, HttpSession session) {
        // @Valid 触发 InvestRequest 字段校验
        // @RequestBody 绑定请求体为 InvestRequest 对象
        User user = (User) session.getAttribute(UserController.SESSION_USER_KEY);  // 从 Session 读取当前登录用户
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
     * 将 FinancialProduct 实体转换为响应 DTO
     *
     * @param p 理财产品实体
     * @return 产品响应 DTO
     */
    private ProductResponse toResponse(FinancialProduct p) {
        // 使用 DTO 而非直接返回实体：枚举字段转字符串，且可裁剪不需要的字段
        ProductResponse resp = new ProductResponse();  // 创建响应 DTO 对象
        resp.setId(p.getId());  // 设置产品 ID
        resp.setName(p.getName());  // 设置产品名称
        resp.setType(p.getType().name());  // 设置产品类型（枚举转字符串）
        resp.setAnnualRate(p.getAnnualRate());  // 设置年化收益率
        resp.setMinAmount(p.getMinAmount());  // 设置起投金额
        resp.setDuration(p.getDuration());  // 设置投资期限
        resp.setRiskLevel(p.getRiskLevel());  // 设置风险等级
        resp.setTotalAmount(p.getTotalAmount());  // 设置募集总额
        resp.setInvestedAmount(p.getInvestedAmount());  // 设置已募集金额
        resp.setStatus(p.getStatus().name());  // 设置产品状态（枚举转字符串）
        resp.setDescription(p.getDescription());  // 设置产品描述
        resp.setCreatedAt(p.getCreatedAt());  // 设置创建时间
        return resp;  // 返回 DTO
    }
}
