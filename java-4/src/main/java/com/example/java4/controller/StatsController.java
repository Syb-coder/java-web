// 声明包路径，存放控制器层
package com.example.java4.controller;

// 导入 DTO 与 Service
import com.example.java4.dto.StatsResponse;
import com.example.java4.service.StatsService;

// 导入 Spring 工具
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计控制器（后台管理端）
 * <p>
 * 提供数据统计接口，用于管理端首页仪表盘展示。
 * </p>
 */
@RestController
@RequestMapping("/api/admin")
public class StatsController {

    /** 统计服务 */
    private final StatsService statsService;

    /**
     * 构造方法注入依赖
     */
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * 获取统计数据汇总
     *
     * @return 统计响应
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(statsService.getStats());
    }
}
