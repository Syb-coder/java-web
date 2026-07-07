package com.example.java6.service;

import com.example.java6.dto.NewsRequest;
import com.example.java6.dto.NewsResponse;
import com.example.java6.model.News;
import com.example.java6.model.NewsCategory;
import com.example.java6.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 新闻资讯业务服务
 *
 * <p>封装新闻资讯的查询、创建、更新、删除以及阅读量统计等业务逻辑。</p>
 */
@Service
public class NewsService {

    private final NewsRepository repository;

    public NewsService(NewsRepository repository) {
        this.repository = repository;
    }

    /**
     * 查询新闻列表（可按分类过滤）
     *
     * @param category 分类，为 null 时返回全部
     * @return 新闻响应列表
     */
    public List<NewsResponse> list(NewsCategory category) {
        List<News> list = (category == null)
                ? repository.findAllByOrderByPublishTimeDesc()
                : repository.findByCategoryOrderByPublishTimeDesc(category);
        return list.stream().map(NewsResponse::from).collect(Collectors.toList());
    }

    /**
     * 查询首页最新新闻
     *
     * @return 最新 8 条新闻
     */
    public List<NewsResponse> latest() {
        return repository.findTop8ByOrderByPublishTimeDesc().stream()
                .map(NewsResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 查询置顶新闻
     *
     * @return 置顶新闻列表
     */
    public List<NewsResponse> topNews() {
        return repository.findByTopTrueOrderByPublishTimeDesc().stream()
                .map(NewsResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 获取新闻详情（同时累加阅读量）
     *
     * @param id 新闻 ID
     * @return 新闻响应，不存在返回 null
     */
    public NewsResponse get(Long id) {
        return repository.findById(id).map(n -> {
            n.setViewCount(n.getViewCount() + 1);
            repository.save(n);
            return NewsResponse.from(n);
        }).orElse(null);
    }

    /**
     * 创建新闻
     *
     * @param req 创建请求
     * @return 创建后的新闻响应
     */
    public NewsResponse create(NewsRequest req) {
        News n = new News();
        applyRequest(n, req);
        if (n.getPublishTime() == null) {
            n.setPublishTime(LocalDateTime.now());
        }
        return NewsResponse.from(repository.save(n));
    }

    /**
     * 更新新闻
     *
     * @param id  新闻 ID
     * @param req 更新请求
     * @return 更新后的新闻响应，不存在返回 null
     */
    public NewsResponse update(Long id, NewsRequest req) {
        return repository.findById(id).map(n -> {
            applyRequest(n, req);
            return NewsResponse.from(repository.save(n));
        }).orElse(null);
    }

    /**
     * 删除新闻
     *
     * @param id 新闻 ID
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
     * 将请求字段应用到实体（避免重复赋值代码）
     *
     * @param n   新闻实体
     * @param req 请求对象
     */
    private void applyRequest(News n, NewsRequest req) {
        n.setTitle(req.title());
        n.setCategory(req.category());
        n.setSummary(req.summary());
        n.setContent(req.content());
        n.setSource(req.source());
        n.setPublishTime(req.publishTime());
        n.setTop(Boolean.TRUE.equals(req.top()));
    }
}
