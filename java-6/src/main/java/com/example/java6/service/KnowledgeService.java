package com.example.java6.service;

import com.example.java6.dto.KnowledgeRequest;
import com.example.java6.dto.KnowledgeResponse;
import com.example.java6.model.KnowledgeArticle;
import com.example.java6.model.KnowledgeCategory;
import com.example.java6.repository.KnowledgeArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 安全知识文章业务服务
 *
 * <p>封装安全知识文章的查询、创建、更新、删除以及阅读量统计等业务逻辑。</p>
 */
@Service
public class KnowledgeService {

    private final KnowledgeArticleRepository repository;

    public KnowledgeService(KnowledgeArticleRepository repository) {
        this.repository = repository;
    }

    /**
     * 查询知识文章列表（可按分类过滤）
     *
     * @param category 分类，为 null 时返回全部
     * @return 文章响应列表
     */
    public List<KnowledgeResponse> list(KnowledgeCategory category) {
        List<KnowledgeArticle> list = (category == null)
                ? repository.findAllByOrderByCreatedAtDesc()
                : repository.findByCategoryOrderByCreatedAtDesc(category);
        return list.stream().map(KnowledgeResponse::from).collect(Collectors.toList());
    }

    /**
     * 查询首页推荐文章
     *
     * @return 最新 6 篇文章
     */
    public List<KnowledgeResponse> latest() {
        return repository.findTop6ByOrderByCreatedAtDesc().stream()
                .map(KnowledgeResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 获取文章详情（同时累加阅读量）
     *
     * @param id 文章 ID
     * @return 文章响应，不存在返回 null
     */
    public KnowledgeResponse get(Long id) {
        return repository.findById(id).map(k -> {
            k.setViewCount(k.getViewCount() + 1);
            repository.save(k);
            return KnowledgeResponse.from(k);
        }).orElse(null);
    }

    /**
     * 创建知识文章
     *
     * @param req 创建请求
     * @return 创建后的文章响应
     */
    public KnowledgeResponse create(KnowledgeRequest req) {
        KnowledgeArticle k = new KnowledgeArticle();
        applyRequest(k, req);
        return KnowledgeResponse.from(repository.save(k));
    }

    /**
     * 更新知识文章
     *
     * @param id  文章 ID
     * @param req 更新请求
     * @return 更新后的文章响应，不存在返回 null
     */
    public KnowledgeResponse update(Long id, KnowledgeRequest req) {
        return repository.findById(id).map(k -> {
            applyRequest(k, req);
            return KnowledgeResponse.from(repository.save(k));
        }).orElse(null);
    }

    /**
     * 删除知识文章
     *
     * @param id 文章 ID
     * @return 是否删除成功
     */
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 将请求字段应用到实体
     *
     * @param k   文章实体
     * @param req 请求对象
     */
    private void applyRequest(KnowledgeArticle k, KnowledgeRequest req) {
        k.setTitle(req.title());
        k.setCategory(req.category());
        k.setSummary(req.summary());
        k.setContent(req.content());
        k.setAuthor(req.author());
    }
}
