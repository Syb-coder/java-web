package com.example.java6.dto;

import com.example.java6.model.KnowledgeCategory;

/**
 * 安全知识文章创建/更新请求
 *
 * @param title    文章标题
 * @param category 文章分类
 * @param summary  文章摘要
 * @param content  文章正文
 * @param author   作者
 */
public record KnowledgeRequest(
        String title,
        KnowledgeCategory category,
        String summary,
        String content,
        String author
) {
}
