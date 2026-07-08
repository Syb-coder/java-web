package com.example.java3.dto;

import com.example.java3.model.Message;

import java.time.format.DateTimeFormatter;

/**
 * 私信消息响应 DTO
 */
public class MessageResponse {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private Long productId;
    private String content;
    private Boolean read;
    private String createdAt;

    public MessageResponse(Message m, String senderName, String receiverName) {
        this.id = m.getId();
        this.senderId = m.getSenderId();
        this.senderName = senderName;
        this.receiverId = m.getReceiverId();
        this.receiverName = receiverName;
        this.productId = m.getProductId();
        this.content = m.getContent();
        this.read = m.getRead();
        this.createdAt = m.getCreatedAt() != null ? m.getCreatedAt().format(FMT) : null;
    }

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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
