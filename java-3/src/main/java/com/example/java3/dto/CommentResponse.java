package com.example.java3.dto;

import com.example.java3.model.Comment;

import java.time.format.DateTimeFormatter;

/**
 * 评论响应 DTO
 */
public class CommentResponse {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private Long productId;
    private Long userId;
    private String username;
    private String avatar;
    private String content;
    private Long parentId;
    private String createdAt;

    public CommentResponse(Comment c, String username, String avatar) {
        this.id = c.getId();
        this.productId = c.getProductId();
        this.userId = c.getUserId();
        this.username = username;
        this.avatar = avatar;
        this.content = c.getContent();
        this.parentId = c.getParentId();
        this.createdAt = c.getCreatedAt() != null ? c.getCreatedAt().format(FMT) : null;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
