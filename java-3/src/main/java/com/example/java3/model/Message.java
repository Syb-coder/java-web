package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 私信消息实体
 * <p>
 * 买卖双方基于商品进行一对一私信沟通。
 * 消息永久留存，用于交易纠纷取证。
 * </p>
 */
@Entity
@Table(name = "messages")
public class Message {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 发送者用户 ID */
    @Column(nullable = false)
    private Long senderId;

    /** 接收者用户 ID */
    @Column(nullable = false)
    private Long receiverId;

    /** 关联商品 ID（可为 null，表示非商品相关的私信） */
    private Long productId;

    /** 消息内容 */
    @Column(nullable = false, length = 1000)
    private String content;

    /** 是否已读（接收者已查看） */
    @Column(nullable = false)
    private Boolean read;

    /** 发送时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    public Message() {
    }

    /**
     * 业务构造方法
     *
     * @param senderId   发送者 ID
     * @param receiverId 接收者 ID
     * @param productId  关联商品 ID（可为 null）
     * @param content    消息内容
     */
    public Message(Long senderId, Long receiverId, Long productId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.productId = productId;
        this.content = content;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
