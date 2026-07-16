package com.example.java11.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 作品实体
 * <p>
 * 对应 works 表，存储二次元作品库的元数据，包括动画、漫画、游戏、轻小说等。
 * 支持评分、标签、类型筛选等功能。
 * </p>
 */
@Entity
@Table(name = "works")
public class Work {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 作品名 */
    @Column(nullable = false, length = 100)
    private String title;

    /** 封面 URL */
    @Column(length = 500)
    private String coverImage;

    /** 作品简介 */
    @Column(length = 2000)
    private String description;

    /** 作品类型 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType type;

    /** 评分，范围 0-10 */
    @Column
    private Double rating;

    /** 作品标签，逗号分隔 */
    @Column(length = 200)
    private String tags;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Work() {
    }

    /**
     * 业务构造器：创建新作品时使用
     *
     * @param title       作品名
     * @param type        作品类型
     * @param description 作品简介
     */
    public Work(String title, WorkType type, String description) {
        this.title = title;
        this.type = type;
        this.description = description;
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

    public WorkType getType() {
        return type;
    }

    public void setType(WorkType type) {
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
