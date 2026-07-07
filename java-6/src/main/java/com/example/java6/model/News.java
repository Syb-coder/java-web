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
 * 新闻资讯实体
 *
 * <p>承载网络安全官网发布的新闻资讯数据，包括标题、分类、
 * 摘要、正文、来源、发布时间、阅读量等核心字段。</p>
 */
@Entity
@Table(name = "news")
public class News {

    /** 主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 新闻标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 新闻分类 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NewsCategory category;

    /** 新闻摘要 */
    @Column(length = 500)
    private String summary;

    /** 新闻正文内容 */
    @Column(nullable = false, length = 8000)
    private String content;

    /** 信息来源 */
    @Column(length = 100)
    private String source;

    /** 发布时间 */
    @Column(nullable = false)
    private LocalDateTime publishTime;

    /** 阅读量 */
    @Column(nullable = false)
    private Integer viewCount = 0;

    /** 是否置顶 */
    @Column(nullable = false)
    private Boolean top = false;

    /** 记录创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public News() {
    }

    public News(String title, NewsCategory category, String summary, String content,
                String source, LocalDateTime publishTime) {
        this.title = title;
        this.category = category;
        this.summary = summary;
        this.content = content;
        this.source = source;
        this.publishTime = publishTime;
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

    public NewsCategory getCategory() {
        return category;
    }

    public void setCategory(NewsCategory category) {
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Boolean getTop() {
        return top;
    }

    public void setTop(Boolean top) {
        this.top = top;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
