package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.dto.WorkResponse;
import com.example.java11.service.WorkService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作品控制器
 * <p>
 * 提供二次元作品库的列表、类型筛选、高分推荐、详情与搜索接口（公开访问）。
 * </p>
 */
@RestController
@RequestMapping("/api/works")
public class WorkController {

    /** 作品服务 */
    private final WorkService workService;

    /**
     * 构造器注入依赖
     *
     * @param workService 作品服务
     */
    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    /**
     * 获取所有作品
     *
     * @return 作品响应列表
     */
    @GetMapping
    public ApiResponse getAllWorks() {
        List<WorkResponse> list = workService.getAllWorks();
        return ApiResponse.success(list);
    }

    /**
     * 按类型筛选作品
     *
     * @param type 作品类型名称
     * @return 匹配类型的作品列表
     */
    @GetMapping("/type/{type}")
    public ApiResponse getWorksByType(@PathVariable String type) {
        List<WorkResponse> list = workService.getWorksByType(type);
        return ApiResponse.success(list);
    }

    /**
     * 获取高分作品（评分前 10）
     *
     * @return 高分作品列表
     */
    @GetMapping("/hot")
    public ApiResponse getHotWorks() {
        List<WorkResponse> list = workService.getHotWorks();
        return ApiResponse.success(list);
    }

    /**
     * 获取作品详情
     *
     * @param id 作品 ID
     * @return 作品响应
     */
    @GetMapping("/{id}")
    public ApiResponse getWork(@PathVariable Long id) {
        WorkResponse response = workService.getWork(id);
        return ApiResponse.success(response);
    }

    /**
     * 搜索作品
     *
     * @param keyword 搜索关键词
     * @return 匹配的作品列表
     */
    @GetMapping("/search")
    public ApiResponse searchWorks(@RequestParam String keyword) {
        List<WorkResponse> list = workService.searchWorks(keyword);
        return ApiResponse.success(list);
    }
}
