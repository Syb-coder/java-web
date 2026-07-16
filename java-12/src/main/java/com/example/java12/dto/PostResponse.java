package com.example.java12.dto;  // DTO 层包

import com.example.java12.model.Post;  // 帖子实体

import java.time.LocalDateTime;  // 时间类型

/**
 * 帖子响应 DTO
 * <p>
 * 用于帖子列表和帖子详情展示，包含作者昵称、板块名称等关联信息。
 * 不直接暴露 userId，而是展示作者昵称和头像。
 * </p>
 */
public class PostResponse {

    /** 帖子 ID */
    private Long id;

    /** 标题 */
    private String title;

    /** 正文 */
    private String content;

    /** 所属板块 ID */
    private Long plateId;

    /** 所属板块名称 */
    private String plateName;

    /** 作者用户 ID */
    private Long userId;

    /** 作者昵称 */
    private String authorName;

    /** 作者头像 */
    private String authorAvatar;

    /** 点赞数 */
    private Integer likeCount;

    /** 收藏数 */
    private Integer collectCount;

    /** 评论数 */
    private Integer commentCount;

    /** 浏览量 */
    private Integer viewCount;

    /** 是否置顶 */
    private Boolean isTop;

    /** 发布时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 当前登录用户是否已点赞（列表页可省略） */
    private Boolean liked;

    /** 当前登录用户是否已收藏（列表页可省略） */
    private Boolean collected;

    /**
     * 无参构造器
     */
    public PostResponse() {
    }

    /**
     * 从帖子实体构造基础响应（不含关联信息）
     *
     * @param post 帖子实体
     */
    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.plateId = post.getPlateId();
        this.userId = post.getUserId();
        this.likeCount = post.getLikeCount();
        this.collectCount = post.getCollectCount();
        this.commentCount = post.getCommentCount();
        this.viewCount = post.getViewCount();
        this.isTop = post.getIsTop();
        this.createTime = post.getCreateTime();
        this.updateTime = post.getUpdateTime();
    }

    // ===== getter / setter =====

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

    public Long getPlateId() {
        return plateId;
    }

    public void setPlateId(Long plateId) {
        this.plateId = plateId;
    }

    public String getPlateName() {
        return plateName;
    }

    public void setPlateName(String plateName) {
        this.plateName = plateName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
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

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Boolean getCollected() {
        return collected;
    }

    public void setCollected(Boolean collected) {
        this.collected = collected;
    }
}
