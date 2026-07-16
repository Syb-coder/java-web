package com.example.java11.repository;

import com.example.java11.model.News;
import com.example.java11.model.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 新闻资讯数据访问层
 * <p>
 * 提供对 news 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * 按分类获取新闻列表，按创建时间倒序
     *
     * @param category 新闻分类
     * @return 新闻列表
     */
    List<News> findByCategoryOrderByCreatedAtDesc(NewsCategory category);

    /**
     * 获取最新的 5 条新闻（首页资讯展示用）
     *
     * @return 最新新闻列表
     */
    List<News> findTop5ByOrderByCreatedAtDesc();

    /**
     * 按标题关键词搜索新闻，按创建时间倒序
     *
     * @param keyword 搜索关键词
     * @return 匹配的新闻列表
     */
    List<News> findByTitleContainingOrderByCreatedAtDesc(String keyword);
}
