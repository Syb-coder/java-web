// 声明包路径
package com.example.java2.dto;

// 导入实体类
import com.example.java2.model.Comment;
import com.example.java2.model.User;

/**
 * 评论响应 DTO
 * <p>
 * 包含评论内容与发表人昵称，便于前台列表展示。
 * </p>
 *
 * @param id          评论 ID
 * @param articleId   文章 ID
 * @param userId      用户 ID
 * @param username    用户名
 * @param nickname    昵称
 * @param content     评论内容
 * @param createTime  评论时间
 */
// 采用 record 声明：不可变响应 DTO，自动生成 accessor/equals/hashCode/toString；附带用户昵称便于前台直接展示
public record CommentResponse(
        Long id,          // 评论主键 ID
        Long articleId,   // 所属文章 ID，用于关联文章
        Long userId,      // 发表用户 ID
        String username,  // 发表用户名，用户被删除时回退为"未知用户"
        String nickname,  // 发表用户昵称，缺失时回退为用户名，保证前台总有可显示文本
        String content,   // 评论正文内容
        String createTime // 评论发表时间字符串
) {
    /**
     * 由评论实体与用户实体构造响应
     *
     * @param comment 评论实体
     * @param user    发表用户实体
     * @return 评论响应
     */
    // 使用静态工厂方法 from()：需同时传入评论实体与用户实体，工厂方法集中处理用户为空的兜底逻辑，避免调用方重复判空
    public static CommentResponse from(Comment comment, User user) {
        return new CommentResponse(
                comment.getId(),
                comment.getArticleId(),
                comment.getUserId(),
                // 用户可能已被删除（外键未强约束），为空时显示"未知用户"
                user != null ? user.getUsername() : "未知用户",
                // 昵称缺失时回退到用户名，用户也被删除时回退到"未知用户"
                user != null && user.getNickname() != null ? user.getNickname() : (user != null ? user.getUsername() : "未知用户"),
                comment.getContent(),
                comment.getCreateTime() != null ? comment.getCreateTime().toString() : null
        );
    }
}
