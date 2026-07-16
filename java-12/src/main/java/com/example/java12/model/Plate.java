package com.example.java12.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;  // JPA 注解
import java.time.LocalDateTime;  // 时间类型

/**
 * 板块实体
 * <p>
 * 对应 plates 表，存储论坛板块信息。每个板块对应一种网文类型（如玄幻、都市、仙侠），
 * 帖子必须归属于某个板块。管理员可增删改板块。
 * </p>
 */
@Entity  // 声明为 JPA 实体
@Table(name = "plates")  // 映射到 plates 表
public class Plate {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 板块名称（如"玄幻"、"都市"），唯一 */
    @Column(nullable = false, unique = true, length = 30)
    private String name;

    /** 板块描述（展示在板块列表中） */
    @Column(length = 500)
    private String description;

    /** 板块图标 URL（可选） */
    @Column(length = 500)
    private String icon;

    /** 帖子数（冗余计数，避免频繁 count 查询） */
    @Column(nullable = false)
    private Integer postCount;

    /** 排序序号（越小越靠前） */
    @Column(nullable = false)
    private Integer sortOrder;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Plate() {
    }

    /**
     * 业务构造器：创建新板块时使用
     *
     * @param name        板块名称
     * @param description 板块描述
     */
    public Plate(String name, String description) {
        this.name = name;
        this.description = description;
        this.postCount = 0;
        this.sortOrder = 0;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新前回调，自动刷新 updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
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
