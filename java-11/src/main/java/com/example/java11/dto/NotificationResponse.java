package com.example.java11.dto;

import java.time.LocalDateTime;

/**
 * 通知信息响应 DTO
 * <p>
 * 用于返回站内通知的完整信息，包含通知类型、内容、关联 ID、发送人及已读状态。
 * 主要用于用户消息中心的通知列表展示。
 * </p>
 */
public class NotificationResponse {

    /** 通知 ID */
    private Long id;

    /** 通知类型 */
    private String type;

    /** 通知内容 */
    private String content;

    /** 关联 ID（如帖子 ID、评论 ID 等） */
    private Long relatedId;

    /** 发送人 ID */
    private Long senderId;

    /** 发送人用户名 */
    private String senderName;

    /** 是否已读 */
    private Boolean isRead;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /**
     * 默认无参构造器
     */
    public NotificationResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
