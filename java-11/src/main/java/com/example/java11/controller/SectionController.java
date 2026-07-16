package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.dto.SectionResponse;
import com.example.java11.service.SectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 板块控制器
 * <p>
 * 提供讨论板块的列表与详情查询接口（公开访问）。
 * </p>
 */
@RestController
@RequestMapping("/api/sections")
public class SectionController {

    /** 板块服务 */
    private final SectionService sectionService;

    /**
     * 构造器注入依赖
     *
     * @param sectionService 板块服务
     */
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    /**
     * 获取所有板块
     *
     * @return 板块响应列表
     */
    @GetMapping
    public ApiResponse getAllSections() {
        List<SectionResponse> list = sectionService.getAllSections();
        return ApiResponse.success(list);
    }

    /**
     * 获取板块详情
     *
     * @param id 板块 ID
     * @return 板块响应
     */
    @GetMapping("/{id}")
    public ApiResponse getSection(@PathVariable Long id) {
        SectionResponse response = sectionService.getSection(id);
        return ApiResponse.success(response);
    }
}
