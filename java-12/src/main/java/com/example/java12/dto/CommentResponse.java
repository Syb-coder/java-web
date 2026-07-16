package com.example.java12.dto;  // DTO 层包

import com.example.java12.model.Comment;  // 评论实体

import java.time.LocalDateTime;  // 时间类型

/**
 * 评论响应 DTO
 * <p>
 * 包含评论者昵称和头像，用于帖子详情页评论列表展示。
 * </p>
 */
public class CommentResponse {

    /** 评论 ID */
    private Long id;

    /** 帖子 ID */
    private Long postId;

    /** 评论者用户 ID */
    private Long userId;

    /** 评论者昵称 */
    private String authorName;

    /** 评论者头像 */
    private String authorAvatar;

    /** 父评论 ID（null 表示直接评论帖子） */
    private Long parentId;

    /** 评论内容 */
    private String content;

    /** 是否被版主屏蔽 */
    private Boolean isHidden;

    /** 是否已删除 */
    private Boolean isDeleted;

    /** 评论时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 从实体构造响应 DTO（不含关联信息）
     *
     * @param comment 评论实体
     */
    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getPostId();
        this.userId = comment.getUserId();
        this.parentId = comment.getParentId();
        this.content = comment.getContent();
        this.isHidden = comment.getIsHidden();
        this.isDeleted = comment.getIsDeleted();
        this.createTime = comment.getCreateTime();
        this.updateTime = comment.getUpdateTime();
    }

    // ===== getter / setter =====

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
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
