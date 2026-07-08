package com.example.java3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 商品发布/编辑请求 DTO
 */
public class ProductRequest {

    /** 商品标题 */
    @NotBlank(message = "标题不能为空")
    private String title;

    /** 商品描述 */
    private String description;

    /** 期望价格 */
    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于 0")
    private Double price;

    /** 原价（可选） */
    private Double originalPrice;

    /** 图片 URL（多张分号分隔） */
    private String images;

    /** 新旧程度 */
    private String conditionLevel;

    /** 分类 ID */
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    // ===== Getter / Setter =====

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getConditionLevel() {
        return conditionLevel;
    }

    public void setConditionLevel(String conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
