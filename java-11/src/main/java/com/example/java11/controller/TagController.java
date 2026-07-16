package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.dto.TagResponse;
import com.example.java11.service.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 * <p>
 * 提供标签列表与热门标签查询接口（公开访问）。
 * </p>
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    /** 标签服务 */
    private final TagService tagService;

    /**
     * 构造器注入依赖
     *
     * @param tagService 标签服务
     */
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * 获取所有标签
     *
     * @return 标签响应列表
     */
    @GetMapping
    public ApiResponse getAllTags() {
        List<TagResponse> list = tagService.getAllTags();
        return ApiResponse.success(list);
    }

    /**
     * 获取热门标签（使用次数前 10）
     *
     * @return 热门标签列表
     */
    @GetMapping("/hot")
    public ApiResponse getHotTags() {
        List<TagResponse> list = tagService.getHotTags();
        return ApiResponse.success(list);
    }
}
