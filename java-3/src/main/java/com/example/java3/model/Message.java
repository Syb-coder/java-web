// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

// 导入 JPA（Jakarta Persistence API）全部注解，包含 @Entity、@Table、@Id 等，用于将 POJO 映射为数据库实体
import jakarta.persistence.*;

// 导入 LocalDateTime 时间类型，用于记录消息发送时间
import java.time.LocalDateTime;

/**
 * 私信消息实体
 * <p>
 * 买卖双方基于商品进行一对一私信沟通。
 * 消息永久留存，用于交易纠纷取证。
 * </p>
 */
// 标识当前类为 JPA 实体，Hibernate 会将其映射为数据库表
@Entity
// 指定表名为 messages，承载用户私信内容
@Table(name = "messages")
public class Message {

    /** 主键，自增 */
    // 声明该字段为数据库主键
    @Id
    // 主键生成策略：IDENTITY 表示由数据库自增分配
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 发送者用户 ID */
    // 非空；关联 User 表主键
    @Column(nullable = false)
    private Long senderId;

    /** 接收者用户 ID */
    // 非空；关联 User 表主键
    @Column(nullable = false)
    private Long receiverId;

    /** 关联商品 ID（可为 null，表示非商品相关的私信） */
    // 可空；为 null 表示纯用户间私信，非 null 表示基于某商品的咨询沟通
    private Long productId;

    /** 消息内容 */
    // 非空，长度上限 1000；限定消息长度，避免长文本影响存储与展示
    @Column(nullable = false, length = 1000)
    private String content;

    /** 是否已读（接收者已查看） */
    // 非空；用于未读消息计数与红点提示
    @Column(nullable = false)
    private Boolean read;

    /** 发送时间 */
    // 非空，发送时写入
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    // JPA 规范要求实体必须提供无参构造方法，便于通过反射实例化
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
        // 设置发送者用户 ID
        this.senderId = senderId;
        // 设置接收者用户 ID
        this.receiverId = receiverId;
        // 设置关联商品 ID（可为 null）
        this.productId = productId;
        // 设置消息内容
        this.content = content;
        // 默认未读：新消息发送时接收者尚未查看
        this.read = false;
        // 发送时间取当前系统时间
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    // 获取主键 ID
    public Long getId() {
        return id;
    }

    // 设置主键 ID，通常由 JPA 自动填充
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

    // 获取接收者用户 ID
    public Long getReceiverId() {
        return receiverId;
    }

    // 设置接收者用户 ID
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    // 获取关联商品 ID（非商品私信返回 null）
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

    // 设置是否已读标识（接收者查看后置为 true）
    public void setRead(Boolean read) {
        this.read = read;
    }

    // 获取消息发送时间
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 设置消息发送时间
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
