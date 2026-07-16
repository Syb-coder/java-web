package com.example.java11.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 评论请求 DTO
 * <p>
 * 用于接收用户发评论时提交的内容，支持对帖子的直接评论与对评论的楼中楼回复。
 * parentId 为 null 表示对帖子的直接评论。
 * </p>
 */
public class CommentRequest {

    /** 评论内容（1-2000 字符） */
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 2000, message = "评论内容长度需在 1-2000 字符之间")
    private String content;

    /** 所属帖子 ID */
    @NotNull(message = "帖子 ID 不能为空")
    private Long postId;

    /** 父评论 ID，null 表示对帖子的直接评论 */
    private Long parentId;

    /**
     * 默认无参构造器
     */
    public CommentRequest() {
    }

    /**
     * 全参构造器
     *
     * @param content  评论内容
     * @param postId   所属帖子 ID
     * @param parentId 父评论 ID
     */
    public CommentRequest(String content, Long postId, Long parentId) {
        this.content = content;
        this.postId = postId;
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

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
}
