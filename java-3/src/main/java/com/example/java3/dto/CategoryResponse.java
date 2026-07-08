package com.example.java3.dto;

import com.example.java3.model.ProductCategory;

/**
 * 分类响应 DTO
 */
public class CategoryResponse {

    private Long id;
    private String name;
    private String icon;
    private Integer sortOrder;

    public CategoryResponse(ProductCategory c) {
        this.id = c.getId();
        this.name = c.getName();
        this.icon = c.getIcon();
        this.sortOrder = c.getSortOrder();
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
}
