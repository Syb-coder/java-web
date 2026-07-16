package com.example.java11.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 通知消息实体
 * <p>
 * 对应 notifications 表，存储推送给用户的通知消息。
 * 支持回复、点赞、@提及、系统公告、举报处理结果等多种通知类型。
 * </p>
 */
@Entity
@Table(name = "notifications")
public class Notification {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 接收通知的用户 ID */
    @Column(nullable = false)
    private Long userId;

    /** 通知类型 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    /** 通知内容 */
    @Column(length = 500)
    private String content;

    /** 关联 ID，如帖子 ID、评论 ID */
    @Column
    private Long relatedId;

    /** 发送者 ID，系统通知为 null */
    @Column
    private Long senderId;

    /** 是否已读 */
    @Column(nullable = false)
    private Boolean isRead;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Notification() {
    }

    /**
     * 业务构造器：发送通知时使用
     *
     * @param userId   接收通知的用户 ID
     * @param type     通知类型
     * @param content  通知内容
     * @param senderId 发送者 ID，系统通知为 null
     */
    public Notification(Long userId, NotificationType type, String content, Long senderId) {
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.senderId = senderId;
        this.isRead = false;
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
