// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 Message 实体类，用于构造响应 DTO
import com.example.java3.model.Message;

// 导入 DateTimeFormatter，用于将 LocalDateTime 格式化为前端可读字符串
import java.time.format.DateTimeFormatter;

/**
 * 私信消息响应 DTO
 * <p>
 * 返回给前端的私信数据，补齐发送者与接收者昵称，便于消息列表展示。
 * </p>
 */
public class MessageResponse {

    // 时间格式化器，统一格式为 yyyy-MM-dd HH:mm:ss
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 消息主键 ID
    private Long id;
    // 发送者用户 ID
    private Long senderId;
    // 发送者昵称（冗余字段）
    private String senderName;
    // 接收者用户 ID
    private Long receiverId;
    // 接收者昵称（冗余字段）
    private String receiverName;
    // 关联商品 ID
    private Long productId;
    // 消息内容
    private String content;
    // 是否已读标识
    private Boolean read;
    // 发送时间字符串（已格式化）
    private String createdAt;

    /**
     * 由 Message 实体构造响应
     * <p>
     * Service 层查询发送者与接收者昵称后传入，避免前端额外请求用户信息。
     * </p>
     *
     * @param m            消息实体
     * @param senderName   发送者昵称
     * @param receiverName 接收者昵称
     */
    public MessageResponse(Message m, String senderName, String receiverName) {
        this.id = m.getId();                                                // 赋值消息 ID
        this.senderId = m.getSenderId();                                    // 赋值发送者 ID
        this.senderName = senderName;                                       // 赋值发送者昵称
        this.receiverId = m.getReceiverId();                                // 赋值接收者 ID
        this.receiverName = receiverName;                                   // 赋值接收者昵称
        this.productId = m.getProductId();                                  // 赋值关联商品 ID
        this.content = m.getContent();                                      // 赋值消息内容
        this.read = m.getRead();                                            // 赋值已读标识
        // 创建时间非空时格式化为字符串，否则置 null，避免 NPE
        this.createdAt = m.getCreatedAt() != null ? m.getCreatedAt().format(FMT) : null;
    }

    // 获取消息 ID
    public Long getId() {
        return id;
    }

    // 设置消息 ID
    public void setId(Long id) {
        this.id = id;
    }

    // 获取发送者用户 ID
    public Long getSenderId() {
        return senderId;
    }

    // 设置发送者用户 ID
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    // 获取发送者昵称
    public String getSenderName() {
        return senderName;
    }

    // 设置发送者昵称
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    // 获取接收者用户 ID
    public Long getReceiverId() {
        return receiverId;
    }

    // 设置接收者用户 ID
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    // 获取接收者昵称
    public String getReceiverName() {
        return receiverName;
    }

    // 设置接收者昵称
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    // 获取关联商品 ID
    public Long getProductId() {
        return productId;
    }

    // 设置关联商品 ID
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 获取消息内容
    public String getContent() {
        return content;
    }

    // 设置消息内容
    public void setContent(String content) {
        this.content = content;
    }

    // 获取是否已读标识
    public Boolean getRead() {
        return read;
    }

    // 设置是否已读标识
    public void setRead(Boolean read) {
        this.read = read;
    }

    // 获取发送时间字符串
    public String getCreatedAt() {
        return createdAt;
    }

    // 设置发送时间字符串
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
