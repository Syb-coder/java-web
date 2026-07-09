package com.example.java9.controller;  // 声明控制器包路径

import com.example.java9.dto.RiskHandleRequest;  // 导入风控处理请求 DTO
import com.example.java9.dto.RiskRecordResponse;  // 导入风控记录响应 DTO
import com.example.java9.model.AdminUser;  // 导入管理员实体
import com.example.java9.model.RiskRecord;  // 导入风控记录实体
import com.example.java9.model.RiskStatus;  // 导入风控状态枚举
import com.example.java9.service.RiskService;  // 导入风控服务
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
 * 风控合规后台控制器
 * <p>
 * 提供风控记录的查询与处理接口。
 * GET 请求放行（所有管理员可见），POST 写操作由 {@code LoginInterceptor} 强制要求 RISK 角色登录态。
 * </p>
 * <p>
 * 接口列表：
 * <ul>
 *   <li>GET /api/risk/records - 查询所有风控记录</li>
 *   <li>GET /api/risk/records/pending - 查询待处理风控记录</li>
 *   <li>GET /api/risk/records/status/{status} - 根据状态查询</li>
 *   <li>POST /api/risk/records/{id}/handle - 处理风控记录</li>
 * </ul>
 * </p>
 */
@RestController  // 声明为 REST 控制器，返回值自动序列化为 JSON
@RequestMapping("/api/risk")  // 类级路由前缀，本类所有接口均以 /api/risk 开头
public class RiskController {

    private final RiskService riskService;  // 风控服务

    public RiskController(RiskService riskService) {  // 构造函数注入风控服务
        this.riskService = riskService;  // 赋值风控服务
    }

    /**
     * 查询所有风控记录
     *
     * @return 风控记录响应列表
     */
    @GetMapping("/records")  // 映射 GET /api/risk/records 请求
    public ResponseEntity<List<RiskRecordResponse>> listRecords() {
        List<RiskRecordResponse> list = riskService.findAll().stream()  // 查询全部风控记录并转为 Stream
                .map(this::toResponse)  // 逐条转换为响应 DTO（方法引用）
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 风控记录列表
    }

    /**
     * 查询待处理风控记录
     *
     * @return 待处理风控记录响应列表
     */
    @GetMapping("/records/pending")  // 映射 GET /api/risk/records/pending 请求
    public ResponseEntity<List<RiskRecordResponse>> listPending() {
        List<RiskRecordResponse> list = riskService.findPending().stream()  // 查询待处理风控记录并转为 Stream
                .map(this::toResponse)  // 逐条转换为响应 DTO
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 待处理记录列表
    }

    /**
     * 根据状态查询风控记录
     *
     * @param status 风控状态字符串（PENDING/HANDLED/IGNORED）
     * @return 符合状态的风控记录响应列表
     */
    @GetMapping("/records/status/{status}")  // 映射 GET /api/risk/records/status/{status} 请求
    public ResponseEntity<List<RiskRecordResponse>> listByStatus(@PathVariable String status) {
        // @PathVariable 将 URL 路径中的 {status} 绑定为方法参数
        RiskStatus riskStatus;  // 声明风控状态枚举变量
        try {
            // 路径变量转大写以匹配枚举名，避免大小写敏感问题
            riskStatus = RiskStatus.valueOf(status.toUpperCase());  // 将字符串转为枚举（大写匹配）
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();  // 状态值非法，返回 400
        }
        List<RiskRecordResponse> list = riskService.findByStatus(riskStatus).stream()  // 按状态查询记录
                .map(this::toResponse)  // 转换为响应 DTO
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 符合状态的记录列表
    }

    /**
     * 处理风控记录
     * <p>
     * 从 session 获取 AdminUser 的 username 作为 handler，
     * session 中无管理员时使用 "system" 兜底。
     * </p>
     *
     * @param id      风控记录 ID（路径变量，与请求体中 id 应一致）
     * @param req     处理请求（action、handleRemark）
     * @param session HTTP 会话
     * @return 200 处理成功 / 400 参数错误
     */
    @PostMapping("/records/{id}/handle")  // 映射 POST /api/risk/records/{id}/handle 请求
    public ResponseEntity<Map<String, String>> handleRisk(@PathVariable Long id,
                                                          @Valid @RequestBody RiskHandleRequest req,
                                                          HttpSession session) {
        // @PathVariable 绑定风控记录 ID
        // @Valid 触发 RiskHandleRequest 字段校验
        AdminUser admin = (AdminUser) session.getAttribute(AuthController.SESSION_ADMIN_KEY);  // 从 Session 读取当前管理员
        // 管理员未登录时使用 system 兜底，避免处理人字段为空
        String handler = admin != null ? admin.getUsername() : "system";  // 三元运算符兜底处理人
        try {
            riskService.handleRisk(id, req.getAction(), req.getHandleRemark(), handler);  // 调用 Service 处理风控记录
            return ResponseEntity.ok(Map.of("message", "处理成功"));  // 200 + 成功消息
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 参数错误，返回 400
        }
    }

    /**
     * 将 RiskRecord 实体转换为响应 DTO
     *
     * @param r 风控记录实体
     * @return 风控记录响应 DTO
     */
    private RiskRecordResponse toResponse(RiskRecord r) {
        // 使用 DTO 而非直接返回实体：枚举字段转字符串便于前端展示，且可裁剪敏感字段
        RiskRecordResponse resp = new RiskRecordResponse();  // 创建响应 DTO 对象
        resp.setId(r.getId());  // 设置记录 ID
        resp.setTargetType(r.getTargetType());  // 设置目标类型（用户/商户/订单）
        resp.setTargetId(r.getTargetId());  // 设置目标 ID
        resp.setRiskType(r.getRiskType());  // 设置风险类型
        resp.setRiskLevel(r.getRiskLevel().name());  // 设置风险等级（枚举转字符串）
        resp.setDescription(r.getDescription());  // 设置风险描述
        resp.setRelatedOrderNo(r.getRelatedOrderNo());  // 设置关联订单号
        resp.setStatus(r.getStatus().name());  // 设置处理状态（枚举转字符串）
        resp.setHandledBy(r.getHandledBy());  // 设置处理人
        resp.setHandleRemark(r.getHandleRemark());  // 设置处理备注
        resp.setHandledAt(r.getHandledAt());  // 设置处理时间
        resp.setCreatedAt(r.getCreatedAt());  // 设置创建时间
        return resp;  // 返回 DTO
    }
}
