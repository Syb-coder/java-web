package com.example.java12.dto;  // DTO 层包

import jakarta.validation.constraints.NotBlank;  // 非空校验
import jakarta.validation.constraints.NotNull;  // 非空校验
import jakarta.validation.constraints.Size;  // 长度校验

/**
 * 发帖/编辑帖子请求 DTO
 */
public class PostRequest {

    /** 所属板块 ID */
    @NotNull(message = "请选择板块")
    private Long plateId;

    /** 帖子标题（2-100字符） */
    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 100, message = "标题长度需为2-100字符")
    private String title;

    /** 帖子正文（1-20000字符） */
    @NotBlank(message = "正文不能为空")
    @Size(min = 1, max = 20000, message = "正文长度需为1-20000字符")
    private String content;

    public Long getPlateId() {
        return plateId;
    }

    public void setPlateId(Long plateId) {
        this.plateId = plateId;
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
}
