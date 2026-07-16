package com.example.java11.dto;

import java.time.LocalDateTime;

/**
 * 作品信息响应 DTO
 * <p>
 * 用于返回二次元作品的基本信息，包含标题、封面图、描述、类型、评分及标签。
 * 主要用于作品库列表及作品详情页的数据展示。
 * </p>
 */
public class WorkResponse {

    /** 作品 ID */
    private Long id;

    /** 标题 */
    private String title;

    /** 封面图 URL */
    private String coverImage;

    /** 描述 */
    private String description;

    /** 作品类型 */
    private String type;

    /** 评分 */
    private Double rating;

    /** 标签（逗号分隔） */
    private String tags;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 默认无参构造器
     */
    public WorkResponse() {
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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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
