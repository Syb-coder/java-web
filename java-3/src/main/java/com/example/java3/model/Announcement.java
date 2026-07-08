package com.example.java3.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 校园交易公告实体
 * <p>
 * 管理员发布平台公告，如闲置集市活动通知、规则更新等。
 * </p>
 */
@Entity
@Table(name = "announcements")
public class Announcement {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 公告标题 */
    @Column(nullable = false, length = 100)
    private String title;

    /** 公告内容 */
    @Column(nullable = false, length = 2000)
    private String content;

    /** 是否置顶 */
    @Column(nullable = false)
    private Boolean pinned;

    /** 发布管理员 ID */
    @Column(nullable = false)
    private Long adminId;

    /** 发布时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    public Announcement() {
    }

    /**
     * 业务构造方法
     *
     * @param title   标题
     * @param content 内容
     * @param pinned  是否置顶
     * @param adminId 管理员 ID
     */
    public Announcement(String title, String content, Boolean pinned, Long adminId) {
        this.title = title;
        this.content = content;
        this.pinned = pinned;
        this.adminId = adminId;
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
