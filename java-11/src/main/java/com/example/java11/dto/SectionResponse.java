package com.example.java11.dto;

import java.time.LocalDateTime;

/**
 * 板块信息响应 DTO
 * <p>
 * 用于返回讨论板块的基本信息，包含名称、描述、图标、排序、帖子数及版主信息。
 * 主要用于首页板块导航及板块管理。
 * </p>
 */
public class SectionResponse {

    /** 板块 ID */
    private Long id;

    /** 板块名称 */
    private String name;

    /** 板块描述 */
    private String description;

    /** 板块图标 */
    private String icon;

    /** 排序序号 */
    private Integer sortOrder;

    /** 帖子数 */
    private Integer postCount;

    /** 版主 ID */
    private Long moderatorId;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 默认无参构造器
     */
    public SectionResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public Long getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(Long moderatorId) {
        this.moderatorId = moderatorId;
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
