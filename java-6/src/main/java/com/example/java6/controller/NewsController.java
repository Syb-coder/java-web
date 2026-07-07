package com.example.java6.controller;

import com.example.java6.dto.NewsRequest;
import com.example.java6.dto.NewsResponse;
import com.example.java6.model.NewsCategory;
import com.example.java6.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 新闻资讯 REST 控制器
 *
 * <p>提供新闻资讯的查询、详情、增删改接口，供前台官网与后台管理共用。</p>
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * 查询新闻列表（可按分类过滤）
     *
     * @param category 分类（可选）
     * @return 新闻列表
     */
    @GetMapping
    public List<NewsResponse> list(@RequestParam(required = false) NewsCategory category) {
        return newsService.list(category);
    }

    /**
     * 查询最新新闻（首页用）
     *
     * @return 最新新闻列表
     */
    @GetMapping("/latest")
    public List<NewsResponse> latest() {
        return newsService.latest();
    }

    /**
     * 查询置顶新闻
     *
     * @return 置顶新闻列表
     */
    @GetMapping("/top")
    public List<NewsResponse> top() {
        return newsService.topNews();
    }

    /**
     * 获取新闻详情
     *
     * @param id 新闻 ID
     * @return 新闻详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> get(@PathVariable Long id) {
        NewsResponse resp = newsService.get(id);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    /**
     * 创建新闻
     *
     * @param req 创建请求
     * @return 创建后的新闻
     */
    @PostMapping
    public NewsResponse create(@RequestBody NewsRequest req) {
        return newsService.create(req);
    }

    /**
     * 更新新闻
     *
     * @param id  新闻 ID
     * @param req 更新请求
     * @return 更新后的新闻
     */
    @PutMapping("/{id}")
    public ResponseEntity<NewsResponse> update(@PathVariable Long id, @RequestBody NewsRequest req) {
        NewsResponse resp = newsService.update(id, req);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    /**
     * 删除新闻
     *
     * @param id 新闻 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (newsService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
