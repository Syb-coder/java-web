package com.example.java12.controller;  // 控制器层包

import com.example.java12.dto.ApiResponse;  // 统一响应
import com.example.java12.dto.PlateResponse;  // 板块响应
import com.example.java12.service.PlateService;  // 板块服务
import org.springframework.web.bind.annotation.*;  // Web 注解

import java.util.List;  // 列表

/**
 * 板块控制器
 * <p>
 * 提供板块列表和详情的公开查询接口。
 * 板块的增删改在 AdminController 中（需要管理员权限）。
 * </p>
 */
@RestController
@RequestMapping("/api/plates")
public class PlateController {

    /** 板块服务 */
    private final PlateService plateService;

    /**
     * 构造器注入
     */
    public PlateController(PlateService plateService) {
        this.plateService = plateService;
    }

    /**
     * 查询全部板块（公开接口）
     *
     * @return 板块列表
     */
    @GetMapping
    public ApiResponse<List<PlateResponse>> findAll() {
        return ApiResponse.success(plateService.findAll());
    }

    /**
     * 查询板块详情（公开接口）
     *
     * @param id 板块 ID
     * @return 板块详情
     */
    @GetMapping("/{id}")
    public ApiResponse<PlateResponse> findById(@PathVariable Long id) {
        return ApiResponse.success(new PlateResponse(plateService.findById(id)));
    }
}
