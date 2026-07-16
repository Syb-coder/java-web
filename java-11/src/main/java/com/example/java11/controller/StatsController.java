package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.dto.StatsResponse;
import com.example.java11.service.AdminService;
import org.springframework.web.bind.annotation.*;

/**
 * 统计数据控制器
 * <p>
 * 提供公开的站点统计数据接口，供首页数据看板等匿名场景使用。
 * </p>
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    /** 管理员服务，复用其统计数据聚合逻辑 */
    private final AdminService adminService;

    /**
     * 构造器注入依赖
     *
     * @param adminService 管理员服务
     */
    public StatsController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 获取公开统计数据
     * <p>
     * 返回用户数、帖子数、热门帖子、热门标签等聚合数据。
     * </p>
     *
     * @return 统计数据响应
     */
    @GetMapping
    public ApiResponse getStats() {
        StatsResponse stats = adminService.getStats();
        return ApiResponse.success(stats);
    }
}
