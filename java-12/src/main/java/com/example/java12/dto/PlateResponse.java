package com.example.java12.dto;  // DTO 层包

import com.example.java12.model.Plate;  // 板块实体

import java.time.LocalDateTime;  // 时间类型

/**
 * 板块响应 DTO
 */
public class PlateResponse {

    /** 板块 ID */
    private Long id;

    /** 板块名称 */
    private String name;

    /** 板块描述 */
    private String description;

    /** 板块图标 URL */
    private String icon;

    /** 帖子数 */
    private Integer postCount;

    /** 排序序号 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 从实体构造响应 DTO
     *
     * @param plate 板块实体
     */
    public PlateResponse(Plate plate) {
        this.id = plate.getId();
        this.name = plate.getName();
        this.description = plate.getDescription();
        this.icon = plate.getIcon();
        this.postCount = plate.getPostCount();
        this.sortOrder = plate.getSortOrder();
        this.createTime = plate.getCreateTime();
        this.updateTime = plate.getUpdateTime();
    }

    // ===== getter / setter =====

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

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
