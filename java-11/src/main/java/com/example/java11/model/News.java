package com.example.java11.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 新闻资讯实体
 * <p>
 * 对应 news 表，存储站内发布的二次元相关新闻资讯。
 * 支持按分类检索，包含封面图、摘要等展示字段。
 * </p>
 */
@Entity
@Table(name = "news")
public class News {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 新闻标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 新闻正文 */
    @Column(nullable = false, length = 10000)
    private String content;

    /** 摘要，用于列表展示 */
    @Column(length = 500)
    private String summary;

    /** 新闻分类 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NewsCategory category;

    /** 封面图 URL */
    @Column(length = 500)
    private String coverImage;

    /** 浏览量 */
    @Column(nullable = false)
    private Integer viewCount;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public News() {
    }

    /**
     * 业务构造器：发布新闻时使用
     *
     * @param title    新闻标题
     * @param content  新闻正文
     * @param category 新闻分类
     */
    public News(String title, String content, NewsCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.viewCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新前回调，自动刷新 updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public NewsCategory getCategory() {
        return category;
    }

    public void setCategory(NewsCategory category) {
        this.category = category;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
