package com.example.java12.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;  // JPA 注解
import java.time.LocalDateTime;  // 时间类型

/**
 * 帖子实体
 * <p>
 * 对应 posts 表，存储用户发布的网文讨论帖。
 * 包含标题、正文、所属板块、作者及各类计数（点赞、收藏、评论、浏览）。
 * 支持软删除（isDeleted）和置顶推送（isTop）。
 * </p>
 */
@Entity  // 声明为 JPA 实体
@Table(name = "posts")  // 映射到 posts 表
public class Post {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 作者用户 ID（外键） */
    @Column(nullable = false)
    private Long userId;

    /** 所属板块 ID（外键） */
    @Column(nullable = false)
    private Long plateId;

    /** 帖子标题（2-100字符） */
    @Column(nullable = false, length = 100)
    private String title;

    /** 帖子正文（1-20000字符，扩大 length 避免默认 VARCHAR(255) 截断） */
    @Column(nullable = false, length = 20000)
    private String content;

    /** 点赞数（冗余计数） */
    @Column(nullable = false)
    private Integer likeCount;

    /** 收藏数（冗余计数） */
    @Column(nullable = false)
    private Integer collectCount;

    /** 评论数（冗余计数） */
    @Column(nullable = false)
    private Integer commentCount;

    /** 浏览量（冗余计数） */
    @Column(nullable = false)
    private Integer viewCount;

    /** 是否置顶（版主/管理员推送优质帖） */
    @Column(nullable = false)
    private Boolean isTop;

    /** 是否软删除（作者或版主/管理员删除后标记为 true） */
    @Column(nullable = false)
    private Boolean isDeleted;

    /** 发布时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Post() {
    }

    /**
     * 业务构造器：发布新帖时使用
     *
     * @param userId  作者 ID
     * @param plateId 板块 ID
     * @param title   标题
     * @param content 正文
     */
    public Post(Long userId, Long plateId, String title, String content) {
        this.userId = userId;
        this.plateId = plateId;
        this.title = title;
        this.content = content;
        this.likeCount = 0;
        this.collectCount = 0;
        this.commentCount = 0;
        this.viewCount = 0;
        this.isTop = false;
        this.isDeleted = false;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新前回调，自动刷新 updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    // ===== getter / setter =====

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

    public Long getPlateId() {
        return plateId;
    }

    public void setPlateId(Long plateId) {
        this.plateId = plateId;
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

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Boolean getIsTop() {
        return isTop;
    }

    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
