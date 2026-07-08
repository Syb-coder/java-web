package com.example.java3.dto;

import com.example.java3.model.Announcement;

import java.time.format.DateTimeFormatter;

/**
 * 公告响应 DTO
 */
public class AnnouncementResponse {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private String title;
    private String content;
    private Boolean pinned;
    private Long adminId;
    private String createdAt;

    public AnnouncementResponse(Announcement a) {
        this.id = a.getId();
        this.title = a.getTitle();
        this.content = a.getContent();
        this.pinned = a.getPinned();
        this.adminId = a.getAdminId();
        this.createdAt = a.getCreatedAt() != null ? a.getCreatedAt().format(FMT) : null;
    }

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
