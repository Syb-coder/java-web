package com.example.java9.controller;  // 声明控制器包路径

import com.example.java9.dto.TicketResponse;  // 导入工单响应 DTO
import com.example.java9.model.SupportTicket;  // 导入工单实体
import com.example.java9.model.TicketStatus;  // 导入工单状态枚举
import com.example.java9.repository.UserRepository;  // 导入用户仓库
import com.example.java9.service.TicketService;  // 导入工单服务
import org.springframework.http.ResponseEntity;  // 导入 ResponseEntity，封装响应体与状态码
import org.springframework.web.bind.annotation.GetMapping;  // 导入 GET 请求映射注解
import org.springframework.web.bind.annotation.PathVariable;  // 导入路径变量绑定注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入类级路由映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入 REST 控制器注解

import java.util.List;  // 导入 List 集合
import java.util.stream.Collectors;  // 导入 Stream 收集器

/**
 * 工单查询控制器（管理端）
 * <p>
 * 提供管理端的工单查询接口。用户端工单提交在 UserController，此处仅做查询。
 * GET 请求放行。
 * </p>
 * <p>
 * 接口列表：
 * <ul>
 *   <li>GET /api/tickets - 查询所有工单</li>
 *   <li>GET /api/tickets/{id} - 查询工单详情</li>
 *   <li>GET /api/tickets/status/{status} - 根据状态查询</li>
 * </ul>
 * </p>
 */
@RestController  // 声明为 REST 控制器，返回值自动序列化为 JSON
@RequestMapping("/api/tickets")  // 类级路由前缀，本类所有接口均以 /api/tickets 开头
public class TicketController {

    private final TicketService ticketService;  // 工单服务
    private final UserRepository userRepository;  // 用户仓库（用于冗余用户名）

    public TicketController(TicketService ticketService, UserRepository userRepository) {  // 构造函数注入依赖
        this.ticketService = ticketService;  // 赋值工单服务
        this.userRepository = userRepository;  // 赋值用户仓库
    }

    /**
     * 查询所有工单
     *
     * @return 工单响应列表
     */
    @GetMapping  // 映射 GET /api/tickets 请求
    public ResponseEntity<List<TicketResponse>> listAll() {
        List<TicketResponse> list = ticketService.findAll().stream()  // 查询全部工单并转为 Stream
                .map(this::toResponse)  // 逐条转换为响应 DTO
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 工单列表
    }

    /**
     * 查询工单详情
     *
     * @param id 工单 ID
     * @return 工单详情 / 400 工单不存在
     */
    @GetMapping("/{id}")  // 映射 GET /api/tickets/{id} 请求
    public ResponseEntity<TicketResponse> detail(@PathVariable Long id) {
        // @PathVariable 绑定工单 ID
        try {
            SupportTicket ticket = ticketService.findById(id);  // 按 ID 查询工单
            return ResponseEntity.ok(toResponse(ticket));  // 200 + 工单详情
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();  // 工单不存在，返回 400
        }
    }

    /**
     * 根据状态查询工单
     *
     * @param status 工单状态字符串（OPEN/REPLIED/CLOSED）
     * @return 符合状态的工单响应列表
     */
    @GetMapping("/status/{status}")  // 映射 GET /api/tickets/status/{status} 请求
    public ResponseEntity<List<TicketResponse>> listByStatus(@PathVariable String status) {
        // @PathVariable 绑定工单状态字符串
        TicketStatus ticketStatus;  // 声明工单状态枚举变量
        try {
            // 转大写以匹配枚举名，避免大小写敏感问题
            ticketStatus = TicketStatus.valueOf(status.toUpperCase());  // 将字符串转为枚举（大写匹配）
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();  // 状态值非法，返回 400
        }
        List<TicketResponse> list = ticketService.findByStatus(ticketStatus).stream()  // 按状态查询工单
                .map(this::toResponse)  // 逐条转换为响应 DTO
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 符合状态的工单列表
    }

    /**
     * 将 SupportTicket 实体转换为响应 DTO
     * <p>
     * 通过 UserRepository 查询提交用户名，冗余至响应中便于展示。
     * 用户不存在时 username 留空。
     * </p>
     *
     * @param t 工单实体
     * @return 工单响应 DTO
     */
    private TicketResponse toResponse(SupportTicket t) {
        // 使用 DTO 而非直接返回实体：冗余用户名便于前端展示，且可裁剪敏感字段
        TicketResponse resp = new TicketResponse();  // 创建响应 DTO 对象
        resp.setId(t.getId());  // 设置工单 ID
        resp.setUserId(t.getUserId());  // 设置提交用户 ID
        // 冗余用户名，用户被删除时留空
        userRepository.findById(t.getUserId())  // 按用户 ID 查询用户
                .ifPresent(u -> resp.setUsername(u.getUsername()));  // 用户存在时设置用户名（Optional 链式调用）
        resp.setTitle(t.getTitle());  // 设置工单标题
        resp.setDescription(t.getDescription());  // 设置工单描述
        resp.setStatus(t.getStatus().name());  // 设置工单状态（枚举转字符串）
        resp.setReply(t.getReply());  // 设置回复内容
        resp.setRepliedBy(t.getRepliedBy());  // 设置回复人
        resp.setRepliedAt(t.getRepliedAt());  // 设置回复时间
        resp.setCreatedAt(t.getCreatedAt());  // 设置创建时间
        return resp;  // 返回 DTO
    }
}
