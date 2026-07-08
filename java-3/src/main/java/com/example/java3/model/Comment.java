package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 商品留言评论实体
 * <p>
 * 买家可在商品详情页留言提问，卖家或其他用户可回复。
 * 支持 parentId 实现二级回复结构（顶层评论 parentId=null）。
 * </p>
 */
@Entity
@Table(name = "comments")
public class Comment {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联商品 ID */
    @Column(nullable = false)
    private Long productId;

    /** 评论者用户 ID */
    @Column(nullable = false)
    private Long userId;

    /** 评论内容 */
    @Column(nullable = false, length = 500)
    private String content;

    /** 父评论 ID（顶层评论为 null） */
    private Long parentId;

    /** 评论时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    public Comment() {
    }

    /**
     * 业务构造方法
     *
     * @param productId 商品 ID
     * @param userId    评论者 ID
     * @param content   评论内容
     * @param parentId  父评论 ID（可为 null）
     */
    public Comment(Long productId, Long userId, String content, Long parentId) {
        this.productId = productId;
        this.userId = userId;
        this.content = content;
        this.parentId = parentId;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
