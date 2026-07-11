package com.example.java9.model;  // 实体类所在包，归属于 model 层

// JPA 持久化相关注解导入
import jakarta.persistence.Column;  // 字段列映射注解
import jakarta.persistence.Entity;  // 实体标识注解
import jakarta.persistence.EnumType;  // 枚举映射类型
import jakarta.persistence.Enumerated;  // 枚举存储方式注解
import jakarta.persistence.GeneratedValue;  // 主键生成策略注解
import jakarta.persistence.GenerationType;  // 主键生成策略枚举
import jakarta.persistence.Id;  // 主键标识注解
import jakarta.persistence.PreUpdate;  // 更新前回调注解
import jakarta.persistence.Table;  // 表名映射注解

// JDK 通用类型导入
import java.time.LocalDateTime;  // 时间戳类型

/**
 * 客服工单实体
 * <p>
 * 对应 support_tickets 表，用户提交的咨询/问题反馈工单。
 * 客服回复后状态变为 REPLIED，用户确认解决后关闭。
 * </p>
 */
@Entity  // JPA 实体标识
@Table(name = "support_tickets")  // 映射到 support_tickets 表
public class SupportTicket {  // 客服工单实体，承载用户咨询与客服回复

    /** 主键 ID，自增 */
    @Id  // JPA 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略
    private Long id;  // 主键 ID，自增长

    /** 提交用户 ID */
    @Column(nullable = false)  // 非空
    private Long userId;  // 提交用户 ID，关联 users 表

    /** 工单标题 */
    @Column(nullable = false, length = 100)  // 非空，长度100
    private String title;  // 工单标题，概括问题

    /** 问题描述 */
    @Column(nullable = false, length = 1000)  // 非空，长度1000，覆盖详细描述
    private String description;  // 问题描述，用户提交的详细内容

    /** 工单状态：OPEN/REPLIED/CLOSED */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储
    @Column(nullable = false, length = 10)  // 非空，长度10
    private TicketStatus status = TicketStatus.OPEN;  // 工单状态，默认待处理

    /** 客服回复内容 */
    @Column(length = 1000)  // 长度1000
    private String reply;  // 客服回复内容，REPLIED 状态时填写

    /** 回复人（管理员用户名） */
    @Column(length = 50)  // 长度50
    private String repliedBy;  // 回复人，客服用户名，用于审计

    /** 回复时间 */
    private LocalDateTime repliedAt;  // 回复时间，REPLIED 状态时填写，默认空

    /** 创建时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime createdAt;  // 创建时间戳

    /** 最后修改时间 */
    private LocalDateTime updateTime;  // 最后修改时间戳

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public SupportTicket() {  // JPA 无参构造器
    }

    public SupportTicket(Long userId, String title, String description) {  // 提单构造器
        this.userId = userId;  // 赋值用户 ID
        this.title = title;  // 赋值工单标题
        this.description = description;  // 赋值问题描述
        this.status = TicketStatus.OPEN;  // 新工单默认待处理
        this.createdAt = LocalDateTime.now();  // 服务端生成提单时间
        this.updateTime = LocalDateTime.now();  // 初始化修改时间
    }

    public Long getId() {  // 获取主键 ID
        return id;
    }

    public void setId(Long id) {  // 设置主键 ID
        this.id = id;
    }

    public Long getUserId() {  // 获取提交用户 ID
        return userId;
    }

    public void setUserId(Long userId) {  // 设置提交用户 ID
        this.userId = userId;
    }

    public String getTitle() {  // 获取工单标题
        return title;
    }

    public void setTitle(String title) {  // 设置工单标题
        this.title = title;
    }

    public String getDescription() {  // 获取问题描述
        return description;
    }

    public void setDescription(String description) {  // 设置问题描述
        this.description = description;
    }

    public TicketStatus getStatus() {  // 获取工单状态
        return status;
    }

    public void setStatus(TicketStatus status) {  // 设置工单状态
        this.status = status;
    }

    public String getReply() {  // 获取客服回复内容
        return reply;
    }

    public void setReply(String reply) {  // 设置客服回复内容
        this.reply = reply;
    }

    public String getRepliedBy() {  // 获取回复人
        return repliedBy;
    }

    public void setRepliedBy(String repliedBy) {  // 设置回复人
        this.repliedBy = repliedBy;
    }

    public LocalDateTime getRepliedAt() {  // 获取回复时间
        return repliedAt;
    }

    public void setRepliedAt(LocalDateTime repliedAt) {  // 设置回复时间
        this.repliedAt = repliedAt;
    }

    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
