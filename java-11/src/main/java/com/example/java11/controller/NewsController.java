package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.dto.NewsResponse;
import com.example.java11.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 新闻资讯控制器
 * <p>
 * 提供站内新闻的列表、最新、分类筛选、详情与搜索接口（公开访问）。
 * </p>
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    /** 新闻服务 */
    private final NewsService newsService;

    /**
     * 构造器注入依赖
     *
     * @param newsService 新闻服务
     */
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * 获取所有新闻
     *
     * @return 新闻响应列表
     */
    @GetMapping
    public ApiResponse getAllNews() {
        List<NewsResponse> list = newsService.getAllNews();
        return ApiResponse.success(list);
    }

    /**
     * 获取最新 5 条新闻
     *
     * @return 最新新闻列表
     */
    @GetMapping("/latest")
    public ApiResponse getLatestNews() {
        List<NewsResponse> list = newsService.getLatestNews();
        return ApiResponse.success(list);
    }

    /**
     * 按分类获取新闻
     *
     * @param category 新闻分类名称
     * @return 匹配分类的新闻列表
     */
    @GetMapping("/category/{category}")
    public ApiResponse getNewsByCategory(@PathVariable String category) {
        List<NewsResponse> list = newsService.getNewsByCategory(category);
        return ApiResponse.success(list);
    }

    /**
     * 获取新闻详情（浏览量 +1）
     *
     * @param id 新闻 ID
     * @return 新闻响应
     */
    @GetMapping("/{id}")
    public ApiResponse getNews(@PathVariable Long id) {
        NewsResponse response = newsService.getNews(id);
        return ApiResponse.success(response);
    }

    /**
     * 搜索新闻
     *
     * @param keyword 搜索关键词
     * @return 匹配的新闻列表
     */
    @GetMapping("/search")
    public ApiResponse searchNews(@RequestParam String keyword) {
        List<NewsResponse> list = newsService.searchNews(keyword);
        return ApiResponse.success(list);
    }
}
