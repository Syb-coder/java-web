package com.example.java6.repository; // 定义 Repository 接口所在包，统一存放数据访问层组件

import com.example.java6.model.KnowledgeArticle; // 导入安全知识文章实体类，对应数据库 knowledge_article 表
import com.example.java6.model.KnowledgeCategory; // 导入知识分类枚举，定义知识文章的业务分类
import org.springframework.data.jpa.repository.JpaRepository; // 导入 JPA 基础 Repository 接口，提供标准 CRUD 能力
import org.springframework.stereotype.Repository; // 导入 @Repository 注解，标记数据访问层组件

import java.util.List; // 导入 List 集合，用于返回多条知识文章记录

/**
 * 安全知识文章数据访问层
 * 负责安全知识文章的持久化访问，支持按分类查询、按创建时间排序、最新文章展示等业务场景
 */
@Repository // 标记为 Spring Repository 组件，由 Spring 容器管理为 Bean，并启用异常转换
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, Long> { // 继承 JpaRepository，主键类型为 Long，自动获得标准 CRUD 方法

    /**
     * 按分类查询知识文章列表（按创建时间倒序）
     * Spring Data JPA 派生查询：SELECT * FROM knowledge_article WHERE category = ? ORDER BY created_at DESC
     * 用于安全知识按分类筛选展示，最新创建的排在最前
     *
     * @param category 文章分类，为 null 时查询全部分类
     * @return 文章列表
     */
    List<KnowledgeArticle> findByCategoryOrderByCreatedAtDesc(KnowledgeCategory category); // 派生查询：findBy Category 按分类过滤，OrderByCreatedAtDesc 按创建时间倒序

    /**
     * 查询全部知识文章（按创建时间倒序）
     * Spring Data JPA 派生查询：SELECT * FROM knowledge_article ORDER BY created_at DESC
     * 用于后台管理展示全部知识文章，最新创建的排在最前
     *
     * @return 全部文章列表
     */
    List<KnowledgeArticle> findAllByOrderByCreatedAtDesc(); // 派生查询：findAllBy 表示查询全部，OrderByCreatedAtDesc 按创建时间倒序

    /**
     * 查询最新发布的若干条知识文章
     * Spring Data JPA 派生查询：SELECT * FROM knowledge_article ORDER BY created_at DESC LIMIT 6
     * 用于首页"安全知识"板块展示，固定取 6 条
     *
     * @return 最新文章列表
     */
    List<KnowledgeArticle> findTop6ByOrderByCreatedAtDesc(); // 派生查询：findTop6 表示取前 6 条，OrderByCreatedAtDesc 按创建时间倒序
}
