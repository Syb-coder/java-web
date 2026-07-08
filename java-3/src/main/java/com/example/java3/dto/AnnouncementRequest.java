package com.example.java3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 公告请求 DTO
 */
public class AnnouncementRequest {

    /** 标题 */
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过 100")
    private String title;

    /** 内容 */
    @NotBlank(message = "内容不能为空")
    @Size(max = 2000, message = "内容长度不能超过 2000")
    private String content;

    /** 是否置顶 */
    private Boolean pinned;

    // ===== Getter / Setter =====

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
}
