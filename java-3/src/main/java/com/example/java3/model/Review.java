package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 交易评价实体
 * <p>
 * 订单完成后买家可对卖家评价，构建校园交易信任体系。
 * (orderId) 唯一约束，一单只能评价一次。
 * </p>
 */
@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"orderId"})
})
public class Review {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联订单 ID */
    @Column(nullable = false, unique = true)
    private Long orderId;

    /** 评价者（买家）用户 ID */
    @Column(nullable = false)
    private Long reviewerId;

    /** 被评价者（卖家）用户 ID */
    @Column(nullable = false)
    private Long revieweeId;

    /** 评分 1-5 */
    @Column(nullable = false)
    private Integer rating;

    /** 评价内容 */
    @Column(length = 500)
    private String content;

    /** 评价时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    public Review() {
    }

    /**
     * 业务构造方法
     *
     * @param orderId    订单 ID
     * @param reviewerId 评价者 ID
     * @param revieweeId 被评价者 ID
     * @param rating     评分
     * @param content    评价内容
     */
    public Review(Long orderId, Long reviewerId, Long revieweeId, Integer rating, String content) {
        this.orderId = orderId;
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.rating = rating;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Long getRevieweeId() {
        return revieweeId;
    }

    public void setRevieweeId(Long revieweeId) {
        this.revieweeId = revieweeId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
