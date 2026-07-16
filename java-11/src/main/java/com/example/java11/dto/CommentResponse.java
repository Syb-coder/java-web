package com.example.java11.dto;

import java.time.LocalDateTime;

/**
 * 评论详情响应 DTO
 * <p>
 * 用于返回评论的完整信息，包含评论内容、评论人信息、所属帖子、
 * 楼层号、点赞数、状态及当前用户是否已点赞。
 * </p>
 */
public class CommentResponse {

    /** 评论 ID */
    private Long id;

    /** 评论内容 */
    private String content;

    /** 评论人 ID */
    private Long userId;

    /** 评论人用户名 */
    private String username;

    /** 评论人头像 */
    private String userAvatar;

    /** 所属帖子 ID */
    private Long postId;

    /** 父评论 ID，null 表示对帖子的直接评论 */
    private Long parentId;

    /** 楼层号 */
    private Integer floor;

    /** 点赞数 */
    private Integer likeCount;

    /** 评论状态 */
    private String status;

    /** 当前用户是否已点赞 */
    private Boolean isLiked;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 默认无参构造器
     */
    public CommentResponse() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
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
