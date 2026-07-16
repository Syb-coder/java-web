package com.example.java11.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 帖子实体
 * <p>
 * 对应 posts 表，存储用户发布的讨论帖正文及统计计数。
 * 支持审核状态、置顶、加精等运营功能。
 * </p>
 */
@Entity
@Table(name = "posts")
public class Post {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 帖子标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 帖子正文 */
    @Column(nullable = false, length = 10000)
    private String content;

    /** 发帖用户 ID */
    @Column(nullable = false)
    private Long userId;

    /** 所属板块 ID */
    @Column(nullable = false)
    private Long sectionId;

    /** 浏览量 */
    @Column(nullable = false)
    private Integer viewCount;

    /** 点赞数 */
    @Column(nullable = false)
    private Integer likeCount;

    /** 评论数 */
    @Column(nullable = false)
    private Integer commentCount;

    /** 收藏数 */
    @Column(nullable = false)
    private Integer favoriteCount;

    /** 帖子状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;

    /** 是否置顶 */
    @Column(nullable = false)
    private Boolean isTop;

    /** 是否加精 */
    @Column(nullable = false)
    private Boolean isEssence;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Post() {
    }

    /**
     * 业务构造器：用户发帖时使用
     *
     * @param title     帖子标题
     * @param content   帖子正文
     * @param userId    发帖用户 ID
     * @param sectionId 所属板块 ID
     */
    public Post(String title, String content, Long userId, Long sectionId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.sectionId = sectionId;
        this.viewCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
        this.favoriteCount = 0;
        this.status = PostStatus.PENDING;
        this.isTop = false;
        this.isEssence = false;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public Boolean getIsTop() {
        return isTop;
    }

    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    public Boolean getIsEssence() {
        return isEssence;
    }

    public void setIsEssence(Boolean isEssence) {
        this.isEssence = isEssence;
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
