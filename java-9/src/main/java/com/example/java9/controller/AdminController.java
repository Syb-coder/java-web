package com.example.java9.controller;  // 声明控制器包路径

import com.example.java9.dto.MerchantAuditRequest;  // 导入商户审核请求 DTO
import com.example.java9.dto.StatsResponse;  // 导入统计响应 DTO
import com.example.java9.dto.SystemConfigRequest;  // 导入系统配置请求 DTO
import com.example.java9.dto.TicketReplyRequest;  // 导入工单回复请求 DTO
import com.example.java9.model.AdminUser;  // 导入管理员实体
import com.example.java9.model.Merchant;  // 导入商户实体
import com.example.java9.model.SupportTicket;  // 导入工单实体
import com.example.java9.model.SystemConfig;  // 导入系统配置实体
import com.example.java9.model.User;  // 导入用户实体
import com.example.java9.model.UserStatus;  // 导入用户状态枚举
import com.example.java9.repository.SystemConfigRepository;  // 导入系统配置仓库
import com.example.java9.service.MerchantService;  // 导入商户服务
import com.example.java9.service.StatsService;  // 导入统计服务
import com.example.java9.service.TicketService;  // 导入工单服务
import com.example.java9.service.UserService;  // 导入用户服务
import jakarta.servlet.http.HttpSession;  // 导入 Servlet HTTP 会话对象
import jakarta.validation.Valid;  // 导入 Bean Validation 校验注解
import org.springframework.http.ResponseEntity;  // 导入 ResponseEntity，封装响应体与状态码
import org.springframework.web.bind.annotation.GetMapping;  // 导入 GET 请求映射注解
import org.springframework.web.bind.annotation.PathVariable;  // 导入路径变量绑定注解
import org.springframework.web.bind.annotation.PostMapping;  // 导入 POST 请求映射注解
import org.springframework.web.bind.annotation.PutMapping;  // 导入 PUT 请求映射注解
import org.springframework.web.bind.annotation.RequestBody;  // 导入请求体绑定注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入类级路由映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入 REST 控制器注解

import java.time.LocalDateTime;  // 导入日期时间类
import java.util.List;  // 导入 List 集合
import java.util.Map;  // 导入 Map 集合

/**
 * 运营管理后台控制器
 * <p>
 * 提供平台运营所需的用户管理、商户审核、工单处理、系统配置等接口。
 * 写操作由 {@code LoginInterceptor} 强制要求管理员登录态。
 * </p>
 * <p>
 * 接口列表：
 * <ul>
 *   <li>GET /api/admin/users - 查询所有用户</li>
 *   <li>GET /api/admin/merchants - 查询所有商户</li>
 *   <li>POST /api/admin/merchants/audit - 商户审核</li>
 *   <li>GET /api/admin/stats - 获取平台统计</li>
 *   <li>GET /api/admin/tickets - 查询所有工单</li>
 *   <li>POST /api/admin/tickets/{id}/reply - 回复工单</li>
 *   <li>POST /api/admin/tickets/{id}/close - 关闭工单</li>
 *   <li>GET /api/admin/system-configs - 查询所有系统配置</li>
 *   <li>PUT /api/admin/system-configs - 更新系统配置</li>
 *   <li>POST /api/admin/users/{id}/freeze - 冻结用户</li>
 *   <li>POST /api/admin/users/{id}/unfreeze - 解冻用户</li>
 * </ul>
 * </p>
 */
@RestController  // 声明为 REST 控制器，返回值自动序列化为 JSON
@RequestMapping("/api/admin")  // 类级路由前缀，本类所有接口均以 /api/admin 开头
public class AdminController {

    private final UserService userService;  // 用户服务
    private final MerchantService merchantService;  // 商户服务
    private final TicketService ticketService;  // 工单服务
    private final StatsService statsService;  // 统计服务
    private final SystemConfigRepository systemConfigRepository;  // 系统配置仓库

    public AdminController(UserService userService, MerchantService merchantService,
                           TicketService ticketService, StatsService statsService,
                           SystemConfigRepository systemConfigRepository) {  // 构造函数注入全部依赖
        this.userService = userService;  // 赋值用户服务
        this.merchantService = merchantService;  // 赋值商户服务
        this.ticketService = ticketService;  // 赋值工单服务
        this.statsService = statsService;  // 赋值统计服务
        this.systemConfigRepository = systemConfigRepository;  // 赋值系统配置仓库
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @GetMapping("/users")  // 映射 GET /api/admin/users 请求
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userService.findAll());  // 200 + 全部用户列表
    }

    /**
     * 查询所有商户
     *
     * @return 商户列表
     */
    @GetMapping("/merchants")  // 映射 GET /api/admin/merchants 请求
    public ResponseEntity<List<Merchant>> listMerchants() {
        return ResponseEntity.ok(merchantService.findAll());  // 200 + 全部商户列表
    }

    /**
     * 商户审核
     * <p>
     * 从 session 获取 AdminUser 的 username 作为审核人记录，
     * 但 auditMerchant 方法无审核人参数，直接调用即可。
     * </p>
     *
     * @param req 审核请求（merchantId、action、rejectReason）
     * @return 200 审核完成 / 400 参数错误
     */
    @PostMapping("/merchants/audit")  // 映射 POST /api/admin/merchants/audit 请求
    public ResponseEntity<Map<String, String>> auditMerchant(@RequestBody MerchantAuditRequest req) {
        // @RequestBody 将请求体 JSON 绑定为 MerchantAuditRequest 对象
        try {
            merchantService.auditMerchant(req.getMerchantId(), req.getAction(), req.getRejectReason());  // 调用 Service 执行审核（通过/驳回）
            return ResponseEntity.ok(Map.of("message", "审核完成"));  // 200 + 成功消息
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 参数错误，返回 400
        }
    }

    /**
     * 获取平台统计
     *
     * @return 平台统计数据
     */
    @GetMapping("/stats")  // 映射 GET /api/admin/stats 请求
    public ResponseEntity<StatsResponse> stats() {
        return ResponseEntity.ok(statsService.getStats());  // 200 + 平台统计 DTO
    }

    /**
     * 查询所有工单
     *
     * @return 工单列表
     */
    @GetMapping("/tickets")  // 映射 GET /api/admin/tickets 请求
    public ResponseEntity<List<SupportTicket>> listTickets() {
        return ResponseEntity.ok(ticketService.findAll());  // 200 + 全部工单列表
    }

    /**
     * 回复工单
     * <p>
     * 从 session 获取 AdminUser 的 username 作为 repliedBy，
     * session 中无管理员时使用 "system" 兜底。
     * </p>
     *
     * @param id      工单 ID
     * @param req     回复请求
     * @param session HTTP 会话
     * @return 200 回复成功
     */
    @PostMapping("/tickets/{id}/reply")  // 映射 POST /api/admin/tickets/{id}/reply 请求
    public ResponseEntity<Map<String, String>> replyTicket(@PathVariable Long id,
                                                           @Valid @RequestBody TicketReplyRequest req,
                                                           HttpSession session) {
        // @PathVariable 将 URL 路径中的 {id} 绑定为方法参数 id
        // @Valid 触发 TicketReplyRequest 字段校验
        AdminUser admin = (AdminUser) session.getAttribute(AuthController.SESSION_ADMIN_KEY);  // 从 Session 读取当前管理员
        // 管理员未登录时使用 system 兜底，避免回复人字段为空
        String handler = admin != null ? admin.getUsername() : "system";  // 三元运算符兜底处理人
        ticketService.reply(id, req.getReply(), handler);  // 调用工单服务写入回复
        return ResponseEntity.ok(Map.of("message", "回复成功"));  // 200 + 成功消息
    }

    /**
     * 关闭工单
     *
     * @param id 工单 ID
     * @return 200 关闭成功
     */
    @PostMapping("/tickets/{id}/close")  // 映射 POST /api/admin/tickets/{id}/close 请求
    public ResponseEntity<Map<String, String>> closeTicket(@PathVariable Long id) {
        // @PathVariable 绑定路径变量 id
        try {
            ticketService.close(id);  // 调用工单服务关闭工单
            return ResponseEntity.ok(Map.of("message", "工单已关闭"));  // 200 + 成功消息
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 工单不存在，返回 400
        }
    }

    /**
     * 查询所有系统配置
     *
     * @return 系统配置列表
     */
    @GetMapping("/system-configs")  // 映射 GET /api/admin/system-configs 请求
    public ResponseEntity<List<SystemConfig>> listConfigs() {
        return ResponseEntity.ok(systemConfigRepository.findAll());  // 200 + 全部系统配置
    }

    /**
     * 更新系统配置
     * <p>
     * 按 configKey 查找：存在则更新 value 与 description，不存在则新建。
     * </p>
     *
     * @param req 配置请求
     * @return 200 配置更新成功
     */
    @PutMapping("/system-configs")  // 映射 PUT /api/admin/system-configs 请求
    public ResponseEntity<Map<String, String>> updateConfig(@Valid @RequestBody SystemConfigRequest req) {
        // @Valid 校验 SystemConfigRequest 字段
        SystemConfig config = systemConfigRepository.findByConfigKey(req.getConfigKey())  // 按 configKey 查询现有配置
                .orElseGet(() -> new SystemConfig(req.getConfigKey(), req.getConfigValue(), req.getDescription()));  // 不存在则新建配置对象
        config.setConfigValue(req.getConfigValue());  // 更新配置值
        // description 非空时才覆盖，避免清空已有说明
        if (req.getDescription() != null) {  // 判断描述非空
            config.setDescription(req.getDescription());  // 覆盖描述
        }
        config.setUpdatedAt(LocalDateTime.now());  // 更新修改时间
        systemConfigRepository.save(config);  // 持久化到数据库（新增或更新）
        return ResponseEntity.ok(Map.of("message", "配置更新成功"));  // 200 + 成功消息
    }

    /**
     * 冻结用户
     *
     * @param id 用户 ID
     * @return 200 冻结成功 / 400 用户不存在
     */
    @PostMapping("/users/{id}/freeze")  // 映射 POST /api/admin/users/{id}/freeze 请求
    public ResponseEntity<Map<String, String>> freezeUser(@PathVariable Long id) {
        // @PathVariable 绑定用户 ID
        try {
            userService.updateUserStatus(id, UserStatus.FROZEN);  // 调用 Service 将用户状态置为冻结
            return ResponseEntity.ok(Map.of("message", "用户已冻结"));  // 200 + 成功消息
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 用户不存在，返回 400
        }
    }

    /**
     * 解冻用户
     *
     * @param id 用户 ID
     * @return 200 解冻成功 / 400 用户不存在
     */
    @PostMapping("/users/{id}/unfreeze")  // 映射 POST /api/admin/users/{id}/unfreeze 请求
    public ResponseEntity<Map<String, String>> unfreezeUser(@PathVariable Long id) {
        // @PathVariable 绑定用户 ID
        try {
            userService.updateUserStatus(id, UserStatus.NORMAL);  // 调用 Service 将用户状态置为正常
            return ResponseEntity.ok(Map.of("message", "用户已解冻"));  // 200 + 成功消息
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 用户不存在，返回 400
        }
    }
}
