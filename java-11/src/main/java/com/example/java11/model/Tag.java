package com.example.java11.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 标签实体
 * <p>
 * 对应 tags 表，存储站内可复用的标签元数据。
 * 通过 usageCount 统计标签被引用次数，用于热门标签推荐。
 * </p>
 */
@Entity
@Table(name = "tags")
public class Tag {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 标签名（唯一） */
    @Column(nullable = false, unique = true, length = 20)
    private String name;

    /** 使用次数 */
    @Column(nullable = false)
    private Integer usageCount;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Tag() {
    }

    /**
     * 业务构造器：创建新标签时使用
     *
     * @param name 标签名
     */
    public Tag(String name) {
        this.name = name;
        this.usageCount = 0;
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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
