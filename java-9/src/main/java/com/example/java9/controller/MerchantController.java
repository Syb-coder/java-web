package com.example.java9.controller;  // 声明控制器包路径

import com.example.java9.dto.AmountRequest;  // 导入金额请求 DTO
import com.example.java9.dto.LoginRequest;  // 导入登录请求 DTO
import com.example.java9.dto.LoginResponse;  // 导入登录响应 DTO
import com.example.java9.dto.MerchantRegisterRequest;  // 导入商户入驻请求 DTO
import com.example.java9.dto.MerchantResponse;  // 导入商户响应 DTO
import com.example.java9.model.Merchant;  // 导入商户实体
import com.example.java9.model.Transaction;  // 导入流水实体
import com.example.java9.service.AuthService;  // 导入认证服务
import com.example.java9.service.MerchantService;  // 导入商户服务
import jakarta.servlet.http.HttpSession;  // 导入 Servlet HTTP 会话对象
import jakarta.validation.Valid;  // 导入 Bean Validation 校验注解
import org.springframework.http.ResponseEntity;  // 导入 ResponseEntity，封装响应体与状态码
import org.springframework.web.bind.annotation.GetMapping;  // 导入 GET 请求映射注解
import org.springframework.web.bind.annotation.PostMapping;  // 导入 POST 请求映射注解
import org.springframework.web.bind.annotation.RequestBody;  // 导入请求体绑定注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入类级路由映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入 REST 控制器注解

import java.util.List;  // 导入 List 集合
import java.util.Map;  // 导入 Map 集合

/**
 * B端商户控制器
 * <p>
 * 提供商户入驻注册、登录、商户信息查询、收款流水查询、提现等接口。
 * 商户入驻后需运营审核通过方可登录使用。
 * </p>
 */
@RestController  // 声明为 REST 控制器，返回值自动序列化为 JSON
@RequestMapping("/api/merchants")  // 类级路由前缀，本类所有接口均以 /api/merchants 开头
public class MerchantController {

    /** Session 中存储当前登录商户的键名 */
    public static final String SESSION_MERCHANT_KEY = "loginMerchant";  // 定义 Session 键名常量，供其他类复用

    private final AuthService authService;  // 认证服务
    private final MerchantService merchantService;  // 商户服务

    public MerchantController(AuthService authService, MerchantService merchantService) {  // 构造函数注入依赖
        this.authService = authService;  // 赋值认证服务
        this.merchantService = merchantService;  // 赋值商户服务
    }

    /**
     * 商户入驻注册
     *
     * @param req 入驻请求
     * @return 200 成功 / 400 账号已存在
     */
    @PostMapping("/register")  // 映射 POST /api/merchants/register 请求
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody MerchantRegisterRequest req) {
        // @Valid 触发 Bean Validation 校验入驻请求字段
        // @RequestBody 将请求体 JSON 反序列化为 MerchantRegisterRequest 对象
        try {
            authService.registerMerchant(req);  // 调用 Service 提交入驻申请（需运营审核）
            return ResponseEntity.ok(Map.of("message", "入驻申请提交成功，等待审核"));  // 200 + 成功消息
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 账号已存在，返回 400
        }
    }

    /**
     * 商户登录
     *
     * @param req     登录请求
     * @param session HTTP 会话
     * @return 登录响应
     */
    @PostMapping("/login")  // 映射 POST /api/merchants/login 请求
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req, HttpSession session) {
        // @RequestBody 绑定请求体为 LoginRequest 对象
        try {
            Merchant merchant = authService.loginMerchant(req);  // 调用 Service 校验商户账号密码及审核状态
            session.setAttribute(SESSION_MERCHANT_KEY, merchant);  // 写入商户登录态到 Session
            return ResponseEntity.ok(authService.buildMerchantLoginResponse(merchant));  // 200 + 登录响应
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).build();  // 账号或密码错误，返回 401
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();  // 商户未通过审核，返回 403
        }
    }

    /**
     * 商户登出
     *
     * @param session HTTP 会话
     * @return 204
     */
    @PostMapping("/logout")  // 映射 POST /api/merchants/logout 请求
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();  // 使 Session 失效，清除登录态
        return ResponseEntity.noContent().build();  // 返回 204 No Content
    }

    /**
     * 查询当前登录商户信息
     *
     * @param session HTTP 会话
     * @return 商户信息
     */
    @GetMapping("/me")  // 映射 GET /api/merchants/me 请求
    public ResponseEntity<MerchantResponse> me(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute(SESSION_MERCHANT_KEY);  // 读取当前登录商户
        if (merchant == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        Merchant fresh = merchantService.findById(merchant.getId());  // 重新查询数据库获取最新商户数据
        session.setAttribute(SESSION_MERCHANT_KEY, fresh);  // 更新 Session 中的商户信息
        return ResponseEntity.ok(toResponse(fresh));  // 200 + 商户响应 DTO
    }

    /**
     * 查询当前商户收款流水
     *
     * @param session HTTP 会话
     * @return 流水列表
     */
    @GetMapping("/transactions")  // 映射 GET /api/merchants/transactions 请求
    public ResponseEntity<List<Transaction>> transactions(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute(SESSION_MERCHANT_KEY);  // 读取当前登录商户
        if (merchant == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        return ResponseEntity.ok(merchantService.getMerchantTransactions(merchant.getId()));  // 200 + 收款流水列表
    }

    /**
     * 商户提现
     *
     * @param req     提现请求
     * @param session HTTP 会话
     * @return 提现后商户信息
     */
    @PostMapping("/withdraw")  // 映射 POST /api/merchants/withdraw 请求
    public ResponseEntity<MerchantResponse> withdraw(@Valid @RequestBody AmountRequest req, HttpSession session) {
        // @Valid 校验提现金额参数
        Merchant merchant = (Merchant) session.getAttribute(SESSION_MERCHANT_KEY);  // 读取当前登录商户
        if (merchant == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        try {
            Merchant updated = merchantService.withdraw(merchant.getId(), req.getAmount(), req.getRemark());  // 调用 Service 执行提现
            session.setAttribute(SESSION_MERCHANT_KEY, updated);  // 刷新 Session 中的商户信息
            return ResponseEntity.ok(toResponse(updated));  // 200 + 商户响应 DTO
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();  // 余额不足或金额非法，返回 400
        }
    }

    /**
     * 将 Merchant 实体转换为响应 DTO
     */
    private MerchantResponse toResponse(Merchant m) {
        // 使用 DTO 而非直接返回实体：避免暴露密码字段，且可定制响应结构
        MerchantResponse resp = new MerchantResponse();  // 创建响应 DTO 对象
        resp.setId(m.getId());  // 设置商户 ID
        resp.setUsername(m.getUsername());  // 设置登录账号
        resp.setMerchantName(m.getMerchantName());  // 设置商户名称
        resp.setContactPhone(m.getContactPhone());  // 设置联系电话
        resp.setLicenseNo(m.getLicenseNo());  // 设置营业执照号
        resp.setDescription(m.getDescription());  // 设置商户描述
        resp.setBalance(m.getBalance());  // 设置账户余额
        resp.setStatus(m.getStatus().name());  // 设置审核状态（枚举转字符串）
        resp.setRejectReason(m.getRejectReason());  // 设置驳回原因（审核未通过时）
        resp.setCreatedAt(m.getCreatedAt());  // 设置创建时间
        return resp;  // 返回 DTO
    }
}
