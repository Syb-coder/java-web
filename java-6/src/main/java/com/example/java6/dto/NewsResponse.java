package com.example.java6.dto;

import com.example.java6.model.News;
import com.example.java6.model.NewsCategory;

import java.time.LocalDateTime;

/**
 * 新闻资讯展示响应
 *
 * @param id          主键 ID
 * @param title       新闻标题
 * @param category    新闻分类
 * @param summary     新闻摘要
 * @param content     新闻正文
 * @param source      信息来源
 * @param publishTime 发布时间
 * @param viewCount   阅读量
 * @param top         是否置顶
 * @param createdAt   记录创建时间
 */
public record NewsResponse(
        Long id,
        String title,
        NewsCategory category,
        String summary,
        String content,
        String source,
        LocalDateTime publishTime,
        Integer viewCount,
        Boolean top,
        LocalDateTime createdAt
) {

    /**
     * 从实体构造响应对象
     *
     * @param n 新闻实体
     * @return 响应对象
     */
    public static NewsResponse from(News n) {
        return new NewsResponse(
                n.getId(),
                n.getTitle(),
                n.getCategory(),
                n.getSummary(),
                n.getContent(),
                n.getSource(),
                n.getPublishTime(),
                n.getViewCount(),
                n.getTop(),
                n.getCreatedAt()
        );
    }
}
