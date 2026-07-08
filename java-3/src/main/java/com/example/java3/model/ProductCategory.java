package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 闲置商品分类实体
 * <p>
 * 用于商品归类（教材、数码、家具、运动器材等），管理员可维护。
 * </p>
 */
@Entity
@Table(name = "categories")
public class ProductCategory {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 分类名称（唯一） */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** 分类图标（可选，CSS 类名或 emoji） */
    @Column(length = 50)
    private String icon;

    /** 排序序号，越小越靠前 */
    @Column(nullable = false)
    private Integer sortOrder;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    public ProductCategory() {
    }

    /**
     * 业务构造方法
     *
     * @param name      分类名称
     * @param icon      图标
     * @param sortOrder 排序序号
     */
    public ProductCategory(String name, String icon, Integer sortOrder) {
        this.name = name;
        this.icon = icon;
        this.sortOrder = sortOrder;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
