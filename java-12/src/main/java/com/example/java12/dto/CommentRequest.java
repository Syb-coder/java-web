package com.example.java12.dto;  // DTO 层包

import jakarta.validation.constraints.NotBlank;  // 非空校验
import jakarta.validation.constraints.NotNull;  // 非空校验
import jakarta.validation.constraints.Size;  // 长度校验

/**
 * 发表评论请求 DTO
 */
public class CommentRequest {

    /** 帖子 ID */
    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    /** 父评论 ID（回复其他评论时传入，直接评论帖子时为 null） */
    private Long parentId;

    /** 评论内容（1-1000字符） */
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论长度需为1-1000字符")
    private String content;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
