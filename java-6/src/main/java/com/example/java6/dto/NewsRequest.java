package com.example.java6.dto;

import com.example.java6.model.NewsCategory;

import java.time.LocalDateTime;

/**
 * 新闻资讯创建/更新请求
 *
 * @param title       新闻标题
 * @param category    新闻分类
 * @param summary     新闻摘要
 * @param content     新闻正文
 * @param source      信息来源
 * @param publishTime 发布时间（为 null 时取当前时间）
 * @param top         是否置顶
 */
public record NewsRequest(
        String title,
        NewsCategory category,
        String summary,
        String content,
        String source,
        LocalDateTime publishTime,
        Boolean top
) {
}
