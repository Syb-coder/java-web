package com.example.java11.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 评论实体
 * <p>
 * 对应 comments 表，存储用户对帖子的评论及楼中楼回复。
 * 通过 parentId 实现评论树结构，支持楼层号与点赞统计。
 * </p>
 */
@Entity
@Table(name = "comments")
public class Comment {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 评论内容 */
    @Column(nullable = false, length = 2000)
    private String content;

    /** 评论用户 ID */
    @Column(nullable = false)
    private Long userId;

    /** 所属帖子 ID */
    @Column(nullable = false)
    private Long postId;

    /** 父评论 ID，null 表示对帖子的直接评论 */
    @Column
    private Long parentId;

    /** 楼层号 */
    @Column(nullable = false)
    private Integer floor;

    /** 点赞数 */
    @Column(nullable = false)
    private Integer likeCount;

    /** 评论状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Comment() {
    }

    /**
     * 业务构造器：用户评论时使用
     *
     * @param content  评论内容
     * @param userId   评论用户 ID
     * @param postId   所属帖子 ID
     * @param parentId 父评论 ID，null 表示对帖子的直接评论
     * @param floor    楼层号
     */
    public Comment(String content, Long userId, Long postId, Long parentId, Integer floor) {
        this.content = content;
        this.userId = userId;
        this.postId = postId;
        this.parentId = parentId;
        this.floor = floor;
        this.likeCount = 0;
        this.status = CommentStatus.NORMAL;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public CommentStatus getStatus() {
        return status;
    }

    public void setStatus(CommentStatus status) {
        this.status = status;
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
