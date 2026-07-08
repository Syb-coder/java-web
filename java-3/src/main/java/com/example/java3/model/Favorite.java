package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 商品收藏实体
 * <p>
 * 学生用户对感兴趣的商品进行收藏，便于后续查看。
 * (userId, productId) 组合唯一，避免重复收藏。
 * </p>
 */
@Entity
@Table(name = "favorites", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "productId"})
})
public class Favorite {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 收藏者用户 ID */
    @Column(nullable = false)
    private Long userId;

    /** 商品 ID */
    @Column(nullable = false)
    private Long productId;

    /** 收藏时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    public Favorite() {
    }

    /**
     * 业务构造方法
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     */
    public Favorite(Long userId, Long productId) {
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
