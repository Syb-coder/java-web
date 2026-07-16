package com.example.java11.dto;

import java.time.LocalDateTime;

/**
 * 标签信息响应 DTO
 * <p>
 * 用于返回标签的基本信息，包含标签名称、使用次数及创建时间。
 * 主要用于标签云、热门标签及帖子标签展示。
 * </p>
 */
public class TagResponse {

    /** 标签 ID */
    private Long id;

    /** 标签名称 */
    private String name;

    /** 使用次数 */
    private Integer usageCount;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /**
     * 默认无参构造器
     */
    public TagResponse() {
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

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
