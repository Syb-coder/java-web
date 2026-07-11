package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import java.time.LocalDateTime;  // 导入日期时间类

/**
 * 工单响应 DTO
 * <p>
 * 返回工单的详细信息，包含提交人、工单状态与回复内容。
 * </p>
 */
public class TicketResponse {  // 工单响应 DTO 类定义

    /** 工单 ID */
    private Long id;  // 工单唯一标识

    /** 提交用户 ID */
    private Long userId;  // 提交该工单的用户 ID

    /** 提交用户名（冗余，便于展示） */
    private String username;  // 提交用户名(冗余字段,便于前端展示)

    /** 工单标题 */
    private String title;  // 工单标题

    /** 工单详细描述 */
    private String description;  // 工单详细描述

    /** 工单状态：OPEN/REPLIED/CLOSED */
    private String status;  // 工单状态:OPEN(待处理)/REPLIED(已回复)/CLOSED(已关闭)

    /** 回复内容（已回复时填充） */
    private String reply;  // 回复内容,已回复时填充

    /** 回复人用户名 */
    private String repliedBy;  // 回复人用户名(客服/运营)

    /** 回复时间 */
    private LocalDateTime repliedAt;  // 回复时间戳

    /** 工单创建时间 */
    private LocalDateTime createdAt;  // 工单创建时间戳

    /** 工单最后修改时间 */
    private LocalDateTime updateTime;  // 工单最后修改时间戳

    // —— id 字段的 getter/setter ——
    public Long getId() {  // 获取工单 ID
        return id;  // 返回工单 ID
    }

    public void setId(Long id) {  // 设置工单 ID
        this.id = id;  // 赋值工单 ID
    }

    // —— userId 字段的 getter/setter ——
    public Long getUserId() {  // 获取用户 ID
        return userId;  // 返回用户 ID
    }

    public void setUserId(Long userId) {  // 设置用户 ID
        this.userId = userId;  // 赋值用户 ID
    }

    // —— username 字段的 getter/setter ——
    public String getUsername() {  // 获取用户名
        return username;  // 返回用户名
    }

    public void setUsername(String username) {  // 设置用户名
        this.username = username;  // 赋值用户名
    }

    // —— title 字段的 getter/setter ——
    public String getTitle() {  // 获取工单标题
        return title;  // 返回工单标题
    }

    public void setTitle(String title) {  // 设置工单标题
        this.title = title;  // 赋值工单标题
    }

    // —— description 字段的 getter/setter ——
    public String getDescription() {  // 获取工单描述
        return description;  // 返回工单描述
    }

    public void setDescription(String description) {  // 设置工单描述
        this.description = description;  // 赋值工单描述
    }

    // —— status 字段的 getter/setter ——
    public String getStatus() {  // 获取工单状态
        return status;  // 返回工单状态
    }

    public void setStatus(String status) {  // 设置工单状态
        this.status = status;  // 赋值工单状态
    }

    // —— reply 字段的 getter/setter ——
    public String getReply() {  // 获取回复内容
        return reply;  // 返回回复内容
    }

    public void setReply(String reply) {  // 设置回复内容
        this.reply = reply;  // 赋值回复内容
    }

    // —— repliedBy 字段的 getter/setter ——
    public String getRepliedBy() {  // 获取回复人
        return repliedBy;  // 返回回复人用户名
    }

    public void setRepliedBy(String repliedBy) {  // 设置回复人
        this.repliedBy = repliedBy;  // 赋值回复人用户名
    }

    // —— repliedAt 字段的 getter/setter ——
    public LocalDateTime getRepliedAt() {  // 获取回复时间
        return repliedAt;  // 返回回复时间
    }

    public void setRepliedAt(LocalDateTime repliedAt) {  // 设置回复时间
        this.repliedAt = repliedAt;  // 赋值回复时间
    }

    // —— createdAt 字段的 getter/setter ——
    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;  // 返回创建时间
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;  // 赋值创建时间
    }

    // —— updateTime 字段的 getter/setter ——
    public LocalDateTime getUpdateTime() {  // 获取最后修改时间
        return updateTime;  // 返回最后修改时间
    }

    public void setUpdateTime(LocalDateTime updateTime) {  // 设置最后修改时间
        this.updateTime = updateTime;  // 赋值最后修改时间
    }
}
