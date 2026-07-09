package com.example.java9.controller;  // 声明控制器包路径

import com.example.java9.dto.StatsResponse;  // 导入统计响应 DTO
import com.example.java9.service.StatsService;  // 导入统计服务
import org.springframework.http.ResponseEntity;  // 导入 ResponseEntity，封装响应体与状态码
import org.springframework.web.bind.annotation.GetMapping;  // 导入 GET 请求映射注解
import org.springframework.web.bind.annotation.RequestMapping;  // 导入类级路由映射注解
import org.springframework.web.bind.annotation.RestController;  // 导入 REST 控制器注解

/**
 * 平台统计控制器
 * <p>
 * 提供平台总览统计数据接口，供运营/管理后台首页概览展示。
 * GET 请求放行。
 * </p>
 * <p>
 * 接口列表：
 * <ul>
 *   <li>GET /api/stats - 获取平台总览统计</li>
 * </ul>
 * </p>
 */
@RestController  // 声明为 REST 控制器，返回值自动序列化为 JSON
@RequestMapping("/api/stats")  // 类级路由前缀，本类所有接口均以 /api/stats 开头
public class StatsController {

    private final StatsService statsService;  // 统计服务

    public StatsController(StatsService statsService) {  // 构造函数注入统计服务
        this.statsService = statsService;  // 赋值统计服务
    }

    /**
     * 获取平台总览统计
     *
     * @return 平台统计数据
     */
    @GetMapping  // 映射 GET /api/stats 请求
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(statsService.getStats());  // 200 + 平台统计 DTO
    }
}
