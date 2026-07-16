package com.example.java11.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 帖子-标签关联实体
 * <p>
 * 对应 post_tags 表，维护帖子与标签的多对多关联关系。
 * 采用独立实体而非 @ManyToMany，便于扩展关联属性与查询。
 * </p>
 */
@Entity
@Table(name = "post_tags")
public class PostTag {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 帖子 ID */
    @Column(nullable = false)
    private Long postId;

    /** 标签 ID */
    @Column(nullable = false)
    private Long tagId;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public PostTag() {
    }

    /**
     * 业务构造器：建立帖子与标签关联时使用
     *
     * @param postId 帖子 ID
     * @param tagId  标签 ID
     */
    public PostTag(Long postId, Long tagId) {
        this.postId = postId;
        this.tagId = tagId;
        this.createdAt = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新前回调，自动刷新 updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
