package com.example.java6.repository;

import com.example.java6.model.News;
import com.example.java6.model.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 新闻资讯数据访问层
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * 按分类查询新闻列表（按发布时间倒序）
     *
     * @param category 新闻分类，为 null 时查询全部分类
     * @return 新闻列表
     */
    List<News> findByCategoryOrderByPublishTimeDesc(NewsCategory category);

    /**
     * 查询全部新闻（按发布时间倒序）
     *
     * @return 全部新闻列表
     */
    List<News> findAllByOrderByPublishTimeDesc();

    /**
     * 查询最新发布的若干条新闻
     *
     * @param limit 数量上限
     * @return 最新新闻列表
     */
    List<News> findTop8ByOrderByPublishTimeDesc();

    /**
     * 查询置顶新闻列表
     *
     * @return 置顶新闻列表
     */
    List<News> findByTopTrueOrderByPublishTimeDesc();
}
