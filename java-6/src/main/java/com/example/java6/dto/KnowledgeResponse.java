package com.example.java6.dto;

import com.example.java6.model.KnowledgeArticle;
import com.example.java6.model.KnowledgeCategory;

import java.time.LocalDateTime;

/**
 * 安全知识文章展示响应
 *
 * @param id        主键 ID
 * @param title     文章标题
 * @param category  文章分类
 * @param summary   文章摘要
 * @param content   文章正文
 * @param author    作者
 * @param viewCount 阅读量
 * @param createdAt 创建时间
 */
public record KnowledgeResponse(
        Long id,
        String title,
        KnowledgeCategory category,
        String summary,
        String content,
        String author,
        Integer viewCount,
        LocalDateTime createdAt
) {

    /**
     * 从实体构造响应对象
     *
     * @param k 知识文章实体
     * @return 响应对象
     */
    public static KnowledgeResponse from(KnowledgeArticle k) {
        return new KnowledgeResponse(
                k.getId(),
                k.getTitle(),
                k.getCategory(),
                k.getSummary(),
                k.getContent(),
                k.getAuthor(),
                k.getViewCount(),
                k.getCreatedAt()
        );
    }
}
