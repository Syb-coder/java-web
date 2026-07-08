package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 闲置商品实体
 * <p>
 * 商品发布后需经管理员审核（auditStatus=APPROVED）方可在前台展示。
 * 价格、描述等字段使用较大长度以承载详细商品信息。
 * </p>
 */
@Entity
@Table(name = "products")
public class Product {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 商品标题 */
    @Column(nullable = false, length = 100)
    private String title;

    /** 商品描述（详细说明），长度放大到 2000 */
    @Column(length = 2000)
    private String description;

    /** 期望价格（元） */
    @Column(nullable = false)
    private Double price;

    /** 原价（可选，用于展示性价比） */
    private Double originalPrice;

    /** 商品图片 URL（多张用分号分隔） */
    @Column(length = 1000)
    private String images;

    /** 新旧程度描述（如 9 成新） */
    @Column(length = 50)
    private String conditionLevel;

    /** 所属分类 ID */
    @Column(nullable = false)
    private Long categoryId;

    /** 发布者（学生用户）ID */
    @Column(nullable = false)
    private Long sellerId;

    /** 审核状态：PENDING/APPROVED/REJECTED */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductAuditStatus auditStatus;

    /** 审核备注（驳回原因等） */
    @Column(length = 500)
    private String auditRemark;

    /** 是否已售出（售出后前台不再展示，但保留记录） */
    @Column(nullable = false)
    private Boolean sold;

    /** 发布时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    public Product() {
    }

    /**
     * 业务构造方法：学生发布商品时使用
     *
     * @param title          标题
     * @param description    描述
     * @param price          价格
     * @param originalPrice  原价
     * @param images         图片 URL
     * @param conditionLevel 新旧程度
     * @param categoryId     分类 ID
     * @param sellerId       发布者 ID
     */
    public Product(String title, String description, Double price, Double originalPrice,
                   String images, String conditionLevel, Long categoryId, Long sellerId) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.images = images;
        this.conditionLevel = conditionLevel;
        this.categoryId = categoryId;
        this.sellerId = sellerId;
        this.auditStatus = ProductAuditStatus.PENDING;
        this.sold = false;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

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

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public ProductAuditStatus getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(ProductAuditStatus auditStatus) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
