// 声明包路径
package com.example.java2.dto;

// 导入校验注解
import jakarta.validation.constraints.NotBlank;

/**
 * 评论发表请求 DTO
 * <p>
 * 前台用户在文章详情页提交评论时使用。
 * </p>
 *
 * @param content 评论内容
 */
// 采用 record 声明：不可变请求 DTO，自动生成 accessor/equals/hashCode/toString，作为评论发表入参
public record CommentRequest(
        // @NotBlank 拒绝 null 与纯空白串，防止提交空评论；message 为校验失败时返回前端的提示语
        @NotBlank(message = "评论内容不能为空") String content
) {
}
