package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 商品点赞实体
 * <p>
 * 学生用户对商品点赞，用于热门商品排序参考。
 * (userId, productId) 组合唯一，每人只能点赞一次。
 * </p>
 */
@Entity
@Table(name = "product_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "productId"})
})
public class ProductLike {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 点赞用户 ID */
    @Column(nullable = false)
    private Long userId;

    /** 商品 ID */
    @Column(nullable = false)
    private Long productId;

    /** 点赞时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    public ProductLike() {
    }

    /**
     * 业务构造方法
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     */
    public ProductLike(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
