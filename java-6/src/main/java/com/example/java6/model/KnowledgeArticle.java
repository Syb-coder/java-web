package com.example.java6.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 安全知识文章实体
 *
 * <p>承载网络安全科普知识文章数据，包括防护技巧、
 * 科普文章、案例分析、应急响应四大分类。</p>
 */
@Entity
@Table(name = "knowledge_article")
public class KnowledgeArticle {

    /** 主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 文章标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 文章分类 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private KnowledgeCategory category;

    /** 文章摘要 */
    @Column(length = 500)
    private String summary;

    /** 文章正文内容 */
    @Column(nullable = false, length = 8000)
    private String content;

    /** 作者/编者 */
    @Column(length = 100)
    private String author;

    /** 阅读量 */
    @Column(nullable = false)
    private Integer viewCount = 0;

    /** 记录创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public KnowledgeArticle() {
    }

    public KnowledgeArticle(String title, KnowledgeCategory category, String summary,
                            String content, String author) {
        this.title = title;
        this.category = category;
        this.summary = summary;
        this.content = content;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public KnowledgeCategory getCategory() {
        return category;
    }

    public void setCategory(KnowledgeCategory category) {
        this.category = category;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
