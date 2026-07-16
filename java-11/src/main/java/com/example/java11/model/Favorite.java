package com.example.java11.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 收藏记录实体
 * <p>
 * 对应 favorites 表，记录用户对帖子的收藏行为。
 * 支持按 groupName 分组管理收藏内容。
 * </p>
 */
@Entity
@Table(name = "favorites")
public class Favorite {

    /** 默认收藏分组名 */
    private static final String DEFAULT_GROUP = "默认收藏";

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 收藏用户 ID */
    @Column(nullable = false)
    private Long userId;

    /** 帖子 ID */
    @Column(nullable = false)
    private Long postId;

    /** 收藏分组名 */
    @Column(length = 50)
    private String groupName;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Favorite() {
    }

    /**
     * 业务构造器：用户收藏帖子时使用
     *
     * @param userId 收藏用户 ID
     * @param postId 帖子 ID
     */
    public Favorite(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
        this.groupName = DEFAULT_GROUP;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
