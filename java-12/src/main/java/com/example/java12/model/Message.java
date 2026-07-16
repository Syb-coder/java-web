package com.example.java12.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;  // JPA 注解
import java.time.LocalDateTime;  // 时间类型

/**
 * 站内消息实体
 * <p>
 * 对应 messages 表，存储系统/版主/管理员发送给用户的通知消息。
 * 支持已读/未读状态标记。
 * </p>
 */
@Entity  // 声明为 JPA 实体
@Table(name = "messages")  // 映射到 messages 表
public class Message {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 接收消息的用户 ID（外键） */
    @Column(nullable = false)
    private Long userId;

    /** 消息内容 */
    @Column(nullable = false, length = 500)
    private String content;

    /** 是否已读（false 未读 / true 已读） */
    @Column(nullable = false)
    private Boolean isRead;

    /** 消息发送时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Message() {
    }

    /**
     * 业务构造器：发送消息时使用
     *
     * @param userId  接收者 ID
     * @param content 消息内容
     */
    public Message(Long userId, String content) {
        this.userId = userId;
        this.content = content;
        this.isRead = false;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
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
