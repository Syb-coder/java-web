package com.example.java6.repository;

import com.example.java6.model.KnowledgeArticle;
import com.example.java6.model.KnowledgeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 安全知识文章数据访问层
 */
@Repository
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, Long> {

    /**
     * 按分类查询知识文章列表（按创建时间倒序）
     *
     * @param category 文章分类，为 null 时查询全部分类
     * @return 文章列表
     */
    List<KnowledgeArticle> findByCategoryOrderByCreatedAtDesc(KnowledgeCategory category);

    /**
     * 查询全部知识文章（按创建时间倒序）
     *
     * @return 全部文章列表
     */
    List<KnowledgeArticle> findAllByOrderByCreatedAtDesc();

    /**
     * 查询最新发布的若干条知识文章
     *
     * @return 最新文章列表
     */
    List<KnowledgeArticle> findTop6ByOrderByCreatedAtDesc();
}
