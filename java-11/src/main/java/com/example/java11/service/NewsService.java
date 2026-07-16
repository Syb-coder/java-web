package com.example.java11.service;  // 服务层包，存放业务逻辑

import com.example.java11.dto.NewsResponse;
import com.example.java11.model.News;
import com.example.java11.model.NewsCategory;
import com.example.java11.repository.NewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 新闻资讯服务
 * <p>
 * 负责站内新闻资讯的发布、查询、搜索与浏览量统计。
 * 支持按分类筛选与关键词搜索。
 * </p>
 */
@Service
public class NewsService {

    /** 新闻数据访问层 */
    private final NewsRepository newsRepository;

    /**
     * 构造器注入依赖
     *
     * @param newsRepository 新闻数据访问层
     */
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    /**
     * 获取所有新闻
     *
     * @return 新闻响应列表
     */
    public List<NewsResponse> getAllNews() {
        return newsRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取首页最新 5 条新闻
     *
     * @return 最新新闻列表
     */
    public List<NewsResponse> getLatestNews() {
        return newsRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 按分类获取新闻
     *
     * @param category 新闻分类名称
     * @return 匹配分类的新闻列表
     */
    public List<NewsResponse> getNewsByCategory(String category) {
        NewsCategory newsCategory = parseCategory(category);
        return newsRepository.findByCategoryOrderByCreatedAtDesc(newsCategory).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取新闻详情（增加浏览量）
     *
     * @param id 新闻 ID
     * @return 新闻响应 DTO
     */
    @Transactional
    public NewsResponse getNews(Long id) {
        Optional<News> optional = newsRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("新闻不存在");
        }
        News news = optional.get();
        // 增加浏览量
        news.setViewCount(news.getViewCount() + 1);
        newsRepository.save(news);
        return toResponse(news);
    }

    /**
     * 搜索新闻
     *
     * @param keyword 搜索关键词
     * @return 匹配的新闻列表
     */
    public List<NewsResponse> searchNews(String keyword) {
        return newsRepository.findByTitleContainingOrderByCreatedAtDesc(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 创建新闻
     *
     * @param title       新闻标题
     * @param content     新闻正文
     * @param summary     摘要
     * @param category    新闻分类
     * @param coverImage  封面图 URL
     * @return 创建后的新闻响应 DTO
     */
    @Transactional
    public NewsResponse createNews(String title, String content, String summary, String category, String coverImage) {
        NewsCategory newsCategory = parseCategory(category);
        News news = new News(title, content, newsCategory);
        news.setSummary(summary);
        news.setCoverImage(coverImage);
        News saved = newsRepository.save(news);
        return toResponse(saved);
    }

    /**
     * 更新新闻
     *
     * @param id          新闻 ID
     * @param title       新闻标题
     * @param content     新闻正文
     * @param summary     摘要
     * @param category    新闻分类
     * @param coverImage  封面图 URL
     */
    @Transactional
    public void updateNews(Long id, String title, String content, String summary, String category, String coverImage) {
        Optional<News> optional = newsRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("新闻不存在");
        }
        News news = optional.get();
        news.setTitle(title);
        news.setContent(content);
        news.setSummary(summary);
        news.setCategory(parseCategory(category));
        news.setCoverImage(coverImage);
        newsRepository.save(news);
    }

    /**
     * 删除新闻
     *
     * @param id 新闻 ID
     */
    @Transactional
    public void deleteNews(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new RuntimeException("新闻不存在");
        }
        newsRepository.deleteById(id);
    }

    /**
     * 解析新闻分类字符串为枚举
     *
     * @param category 分类字符串
     * @return 新闻分类枚举
     */
    private NewsCategory parseCategory(String category) {
        try {
            return NewsCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的新闻分类: " + category);
        }
    }

    /**
     * 实体转 DTO
     *
     * @param news 新闻实体
     * @return 新闻响应 DTO，实体为 null 时返回 null
     */
    private NewsResponse toResponse(News news) {
        if (news == null) {
            return null;
        }
        NewsResponse response = new NewsResponse();
        response.setId(news.getId());
        response.setTitle(news.getTitle());
        response.setContent(news.getContent());
        response.setSummary(news.getSummary());
        response.setCategory(news.getCategory() != null ? news.getCategory().name() : null);
        response.setCoverImage(news.getCoverImage());
        response.setViewCount(news.getViewCount());
        response.setCreatedAt(news.getCreatedAt());
        response.setUpdateTime(news.getUpdateTime());
        return response;
    }
}
