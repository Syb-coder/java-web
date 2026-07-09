package com.example.java9.controller;  // 声明控制器包路径

import com.example.java9.dto.ActivityRequest;  // 导入活动请求 DTO
import com.example.java9.dto.ActivityResponse;  // 导入活动响应 DTO
import com.example.java9.model.Activity;  // 导入活动实体
import com.example.java9.model.ActivityType;  // 导入活动类型枚举
import com.example.java9.model.Coupon;  // 导入优惠券实体
import com.example.java9.model.User;  // 导入用户实体
import com.example.java9.service.ActivityService;  // 导入活动服务
import jakarta.servlet.http.HttpSession;  // 导入 Servlet HTTP 会话对象
import jakarta.validation.Valid;  // 导入 Bean Validation 校验注解
import org.springframework.http.ResponseEntity;  // 导入 ResponseEntity，封装响应体与状态码
import org.springframework.web.bind.annotation.GetMapping;  // 导入 GET 请求映射注解
import org.springframework.web.bind.annotation.PathVariable;  // 导入路径变量绑定注解
import org.springframework.web.bind.annotation.PostMapping;  // 导入 POST 请求映射注解
import org.springframework.web.bind.annotation.RequestBody;  // 导入请求体绑定注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入类级路由映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入 REST 控制器注解

import java.time.LocalDateTime;  // 导入日期时间类
import java.util.List;  // 导入 List 集合
import java.util.Map;  // 导入 Map 集合
import java.util.stream.Collectors;  // 导入 Stream 收集器

/**
 * 营销活动与优惠券控制器
 * <p>
 * 提供活动查询、用户领券与优惠券查询接口。为便于测试，额外提供创建活动接口（需管理员登录）。
 * GET 请求放行，POST 创建活动由拦截器要求管理员登录态，POST 领券由拦截器要求 C 端用户登录态。
 * </p>
 * <p>
 * 接口列表：
 * <ul>
 *   <li>GET /api/activities - 查询进行中的活动</li>
 *   <li>GET /api/activities/all - 查询所有活动</li>
 *   <li>POST /api/activities - 创建活动</li>
 *   <li>POST /api/activities/{id}/claim - 用户领取优惠券</li>
 *   <li>GET /api/activities/coupons - 查询当前用户优惠券</li>
 *   <li>GET /api/activities/coupons/unused - 查询当前用户未使用优惠券</li>
 * </ul>
 * </p>
 */
@RestController  // 声明为 REST 控制器，返回值自动序列化为 JSON
@RequestMapping("/api/activities")  // 类级路由前缀，本类所有接口均以 /api/activities 开头
public class ActivityController {

    private final ActivityService activityService;  // 活动服务

    public ActivityController(ActivityService activityService) {  // 构造函数注入活动服务
        this.activityService = activityService;  // 赋值活动服务
    }

    /**
     * 查询进行中的活动
     *
     * @return 进行中的活动响应列表
     */
    @GetMapping  // 映射 GET /api/activities 请求
    public ResponseEntity<List<ActivityResponse>> listActive() {
        List<ActivityResponse> list = activityService.findActive().stream()  // 查询进行中的活动并转为 Stream
                .map(this::toResponse)  // 逐条转换为响应 DTO
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 进行中活动列表
    }

    /**
     * 查询所有活动
     *
     * @return 全部活动响应列表
     */
    @GetMapping("/all")  // 映射 GET /api/activities/all 请求
    public ResponseEntity<List<ActivityResponse>> listAll() {
        List<ActivityResponse> list = activityService.findAll().stream()  // 查询全部活动并转为 Stream
                .map(this::toResponse)  // 逐条转换为响应 DTO
                .collect(Collectors.toList());  // 收集为 List
        return ResponseEntity.ok(list);  // 200 + 全部活动列表
    }

    /**
     * 创建活动
     * <p>
     * 将请求中的 type 字符串转为 ActivityType 枚举，
     * startTime/endTime 字符串解析为 LocalDateTime。
     * </p>
     *
     * @param req 活动创建请求
     * @return 200 创建成功 / 400 参数错误
     */
    @PostMapping  // 映射 POST /api/activities 请求
    public ResponseEntity<Map<String, String>> create(@Valid @RequestBody ActivityRequest req) {
        // @Valid 触发 ActivityRequest 字段校验
        // @RequestBody 绑定请求体为 ActivityRequest 对象
        ActivityType type;  // 声明活动类型枚举变量
        try {
            // 转大写以匹配枚举名，避免大小写敏感问题
            type = ActivityType.valueOf(req.getType().toUpperCase());  // 将字符串转为枚举（大写匹配）
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "无效的活动类型：" + req.getType()));  // 活动类型非法，返回 400
        }
        LocalDateTime startTime;  // 声明开始时间变量
        LocalDateTime endTime;  // 声明结束时间变量
        try {
            startTime = LocalDateTime.parse(req.getStartTime());  // 解析 ISO 格式开始时间
            endTime = LocalDateTime.parse(req.getEndTime());  // 解析 ISO 格式结束时间
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "时间格式错误，需为 ISO 格式如 2026-07-08T00:00:00"));  // 时间格式错误，返回 400
        }
        try {
            activityService.create(req.getTitle(), type, req.getDescription(), startTime, endTime);  // 调用服务创建活动
            return ResponseEntity.ok(Map.of("message", "活动创建成功"));  // 200 + 成功消息
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 参数错误，返回 400
        }
    }

    /**
     * 用户领取优惠券
     *
     * @param id      活动 ID
     * @param session HTTP 会话
     * @return 200 领取成功 / 400 活动不存在 / 401 未登录
     */
    @PostMapping("/{id}/claim")  // 映射 POST /api/activities/{id}/claim 请求
    public ResponseEntity<Map<String, String>> claim(@PathVariable Long id, HttpSession session) {
        // @PathVariable 绑定活动 ID
        User user = (User) session.getAttribute(UserController.SESSION_USER_KEY);  // 从 Session 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        try {
            activityService.claimCoupon(user.getId(), id);  // 调用服务领取优惠券
            return ResponseEntity.ok(Map.of("message", "领取成功"));  // 200 + 成功消息
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 活动不存在或已领取，返回 400
        }
    }

    /**
     * 查询当前用户优惠券
     *
     * @param session HTTP 会话
     * @return 当前用户优惠券列表 / 401 未登录
     */
    @GetMapping("/coupons")  // 映射 GET /api/activities/coupons 请求
    public ResponseEntity<List<Coupon>> myCoupons(HttpSession session) {
        User user = (User) session.getAttribute(UserController.SESSION_USER_KEY);  // 从 Session 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        return ResponseEntity.ok(activityService.findUserCoupons(user.getId()));  // 200 + 用户优惠券列表
    }

    /**
     * 查询当前用户未使用优惠券
     *
     * @param session HTTP 会话
     * @return 当前用户未使用优惠券列表 / 401 未登录
     */
    @GetMapping("/coupons/unused")  // 映射 GET /api/activities/coupons/unused 请求
    public ResponseEntity<List<Coupon>> myUnusedCoupons(HttpSession session) {
        User user = (User) session.getAttribute(UserController.SESSION_USER_KEY);  // 从 Session 读取当前登录用户
        if (user == null) {
            return ResponseEntity.status(401).build();  // 未登录，返回 401
        }
        return ResponseEntity.ok(activityService.findUserUnusedCoupons(user.getId()));  // 200 + 未使用优惠券列表
    }

    /**
     * 将 Activity 实体转换为响应 DTO
     *
     * @param a 活动实体
     * @return 活动响应 DTO
     */
    private ActivityResponse toResponse(Activity a) {
        // 使用 DTO 而非直接返回实体：枚举字段转字符串便于前端展示
        ActivityResponse resp = new ActivityResponse();  // 创建响应 DTO 对象
        resp.setId(a.getId());  // 设置活动 ID
        resp.setTitle(a.getTitle());  // 设置活动标题
        resp.setType(a.getType().name());  // 设置活动类型（枚举转字符串）
        resp.setDescription(a.getDescription());  // 设置活动描述
        resp.setStartTime(a.getStartTime());  // 设置开始时间
        resp.setEndTime(a.getEndTime());  // 设置结束时间
        resp.setActive(a.getActive());  // 设置是否有效
        resp.setCreatedAt(a.getCreatedAt());  // 设置创建时间
        return resp;  // 返回 DTO
    }
}
