package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 意见反馈实体
 * <p>
 * 学生用户可向平台提交意见反馈，管理员可查看处理。
 * </p>
 */
@Entity
@Table(name = "feedbacks")
public class Feedback {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 提交者用户 ID */
    @Column(nullable = false)
    private Long userId;

    /** 反馈标题 */
    @Column(nullable = false, length = 100)
    private String title;

    /** 反馈内容 */
    @Column(nullable = false, length = 1000)
    private String content;

    /** 处理状态：false 未处理，true 已处理 */
    @Column(nullable = false)
    private Boolean handled;

    /** 管理员回复（可选） */
    @Column(length = 500)
    private String reply;

    /** 提交时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    public Feedback() {
    }

    /**
     * 业务构造方法
     *
     * @param userId  提交者 ID
     * @param title   标题
     * @param content 内容
     */
    public Feedback(Long userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.handled = false;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getHandled() {
        return handled;
    }

    public void setHandled(Boolean handled) {
        this.handled = handled;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
