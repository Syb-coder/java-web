package com.example.java11.dto;

import java.time.LocalDateTime;

/**
 * 新闻资讯响应 DTO
 * <p>
 * 用于返回站内新闻资讯的完整信息，包含标题、正文、摘要、分类、封面图及浏览数。
 * 主要用于新闻列表及新闻详情页的数据展示。
 * </p>
 */
public class NewsResponse {

    /** 新闻 ID */
    private Long id;

    /** 标题 */
    private String title;

    /** 正文 */
    private String content;

    /** 摘要 */
    private String summary;

    /** 新闻分类 */
    private String category;

    /** 封面图 URL */
    private String coverImage;

    /** 浏览数 */
    private Integer viewCount;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 默认无参构造器
     */
    public NewsResponse() {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
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
