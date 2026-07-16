package com.example.java11.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 板块/分区实体
 * <p>
 * 对应 sections 表，存储讨论板块的元数据，包括名称、描述、排序与版主归属。
 * 用于对帖子进行一级分类管理。
 * </p>
 */
@Entity
@Table(name = "sections")
public class Section {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 板块名称（唯一） */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** 板块描述 */
    @Column(length = 500)
    private String description;

    /** 图标标识，用于前端渲染图标 */
    @Column(length = 100)
    private String icon;

    /** 排序权重，数值越小越靠前 */
    @Column(nullable = false)
    private Integer sortOrder;

    /** 帖子数 */
    @Column(nullable = false)
    private Integer postCount;

    /** 版主 ID，关联 admin_users 表 */
    @Column
    private Long moderatorId;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Section() {
    }

    /**
     * 业务构造器：创建新板块时使用
     *
     * @param name        板块名称
     * @param description 板块描述
     */
    public Section(String name, String description) {
        this.name = name;
        this.description = description;
        this.sortOrder = 0;
        this.postCount = 0;
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
