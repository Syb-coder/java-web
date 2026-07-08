// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 NotBlank 校验注解，确保字符串非 null 且非空白
import jakarta.validation.constraints.NotBlank;
// 导入 Size 校验注解，限制字符串长度范围
import jakarta.validation.constraints.Size;

/**
 * 评论请求 DTO
 * <p>
 * 用户对商品发表评论或回复他人评论时提交的数据。
 * </p>
 */
public class CommentRequest {

    // 评论内容字段，必填，限制最长 500 字防止滥用
    /** 评论内容 */
    @NotBlank(message = "评论内容不能为空")
    // Size：限定评论内容最长 500 字符
    @Size(max = 500, message = "评论内容不能超过 500 字")
    private String content;

    // 父评论 ID 字段，可选；回复某条评论时填写其 ID，顶级评论为 null
    /** 父评论 ID（回复时填写） */
    private Long parentId;

    // ===== Getter / Setter =====
    // 以下为 JavaBean 访问器方法

    // 获取评论内容
    public String getContent() {
        return content;
    }

    // 设置评论内容
    public void setContent(String content) {
        this.content = content;
    }

    // 获取父评论 ID
    public Long getParentId() {
        return parentId;
    }

    // 设置父评论 ID
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
