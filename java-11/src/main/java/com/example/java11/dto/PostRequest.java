package com.example.java11.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 发帖/编辑帖子请求 DTO
 * <p>
 * 用于接收用户发帖或编辑帖子时提交的内容，包含标题、正文、所属板块及标签列表。
 * </p>
 */
public class PostRequest {

    /** 帖子标题（1-200 字符） */
    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 200, message = "标题长度需在 1-200 字符之间")
    private String title;

    /** 帖子正文（1-10000 字符） */
    @NotBlank(message = "正文不能为空")
    @Size(min = 1, max = 10000, message = "正文长度需在 1-10000 字符之间")
    private String content;

    /** 所属板块 ID */
    @NotNull(message = "板块 ID 不能为空")
    private Long sectionId;

    /** 标签名称列表 */
    private List<String> tags;

    /**
     * 默认无参构造器
     */
    public PostRequest() {
    }

    /**
     * 全参构造器
     *
     * @param title     帖子标题
     * @param content   帖子正文
     * @param sectionId 所属板块 ID
     * @param tags      标签名称列表
     */
    public PostRequest(String title, String content, Long sectionId, List<String> tags) {
        this.title = title;
        this.content = content;
        this.sectionId = sectionId;
        this.tags = tags;
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

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
