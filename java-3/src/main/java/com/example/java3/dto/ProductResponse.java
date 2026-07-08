package com.example.java3.dto;

import com.example.java3.model.Product;

import java.time.format.DateTimeFormatter;

/**
 * 商品响应 DTO
 * <p>
 * 在 Product 实体基础上补充分类名称、卖家昵称、点赞/收藏数等冗余字段，便于前端展示。
 * </p>
 */
public class ProductResponse {

    /** 时间格式化器 */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private String title;
    private String description;
    private Double price;
    private Double originalPrice;
    private String images;
    private String conditionLevel;
    private Long categoryId;
    private String categoryName;
    private Long sellerId;
    private String sellerName;
    private String auditStatus;
    private String auditRemark;
    private Boolean sold;
    private Long likeCount;
    private Long favoriteCount;
    private String createdAt;

    /**
     * 由实体构造基础响应（不含分类名/卖家名/点赞数，由 Service 层补充）
     *
     * @param p 商品实体
     */
    public ProductResponse(Product p) {
        this.id = p.getId();
        this.title = p.getTitle();
        this.description = p.getDescription();
        this.price = p.getPrice();
        this.originalPrice = p.getOriginalPrice();
        this.images = p.getImages();
        this.conditionLevel = p.getConditionLevel();
        this.categoryId = p.getCategoryId();
        this.sellerId = p.getSellerId();
        this.auditStatus = p.getAuditStatus().name();
        this.auditRemark = p.getAuditRemark();
        this.sold = p.getSold();
        this.likeCount = 0L;
        this.favoriteCount = 0L;
        this.createdAt = p.getCreatedAt() != null ? p.getCreatedAt().format(FMT) : null;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public Boolean getSold() {
        return sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
