package com.example.java6.controller;

import com.example.java6.dto.KnowledgeRequest;
import com.example.java6.dto.KnowledgeResponse;
import com.example.java6.model.KnowledgeCategory;
import com.example.java6.service.KnowledgeService;
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
 * 安全知识文章 REST 控制器
 *
 * <p>提供安全知识文章的查询、详情、增删改接口。</p>
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    /**
     * 查询知识文章列表（可按分类过滤）
     *
     * @param category 分类（可选）
     * @return 文章列表
     */
    @GetMapping
    public List<KnowledgeResponse> list(@RequestParam(required = false) KnowledgeCategory category) {
        return knowledgeService.list(category);
    }

    /**
     * 查询最新文章（首页用）
     *
     * @return 最新文章列表
     */
    @GetMapping("/latest")
    public List<KnowledgeResponse> latest() {
        return knowledgeService.latest();
    }

    /**
     * 获取文章详情
     *
     * @param id 文章 ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeResponse> get(@PathVariable Long id) {
        KnowledgeResponse resp = knowledgeService.get(id);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    /**
     * 创建知识文章
     *
     * @param req 创建请求
     * @return 创建后的文章
     */
    @PostMapping
    public KnowledgeResponse create(@RequestBody KnowledgeRequest req) {
        return knowledgeService.create(req);
    }

    /**
     * 更新知识文章
     *
     * @param id  文章 ID
     * @param req 更新请求
     * @return 更新后的文章
     */
    @PutMapping("/{id}")
    public ResponseEntity<KnowledgeResponse> update(@PathVariable Long id, @RequestBody KnowledgeRequest req) {
        KnowledgeResponse resp = knowledgeService.update(id, req);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    /**
     * 删除知识文章
     *
     * @param id 文章 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (knowledgeService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
