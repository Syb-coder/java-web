// 声明包路径
package com.example.java2.dto;

// 导入实体类
import com.example.java2.model.Article;

/**
 * 文章详情响应 DTO
 * <p>
 * 包含文章全部字段，用于文章详情页展示。同时附带分类名、当前用户收藏/点赞状态等上下文。
 * </p>
 *
 * @param id            文章 ID
 * @param categoryId    所属分类 ID
 * @param categoryName  所属分类名称
 * @param title         标题
 * @param summary       摘要
 * @param content       正文
 * @param viewCount     阅读量
 * @param likeCount     点赞数
 * @param favoriteCount 收藏数
 * @param published     发布状态
 * @param publishTime   发布时间
 * @param createTime    创建时间
 * @param favorited     当前用户是否已收藏（未登录为 false）
 * @param liked         当前用户是否已点赞（未登录为 false）
 */
// 采用 record 声明：不可变响应 DTO，自动生成 accessor/equals/hashCode/toString；详情页专用，包含正文与用户交互状态
public record ArticleResponse(
        Long id,            // 文章主键 ID
        Long categoryId,    // 所属分类 ID
        String categoryName,// 所属分类名称，由 Service 层关联查询后传入
        String title,       // 文章标题
        String summary,     // 文章摘要
        String content,     // 文章正文，详情页展示（列表 DTO 中刻意省略该字段）
        int viewCount,      // 阅读量
        int likeCount,      // 点赞数
        int favoriteCount,  // 收藏数
        boolean published,  // 发布状态：true 已发布，false 草稿
        String publishTime, // 发布时间字符串
        String createTime,  // 创建时间字符串
        boolean favorited,  // 当前登录用户是否已收藏该文章，未登录时为 false
        boolean liked       // 当前登录用户是否已点赞该文章，未登录时为 false
) {
    /**
     * 由文章实体构造详情响应
     *
     * @param article      文章实体
     * @param categoryName 分类名称
     * @param favorited    当前用户是否已收藏
     * @param liked        当前用户是否已点赞
     * @return 详情响应
     */
    // 使用静态工厂方法 from()：参数较多（4 个），工厂方法封装实体到 DTO 的映射，避免调用方手动拼装字段
    public static ArticleResponse from(Article article, String categoryName,
                                       boolean favorited, boolean liked) {
        return new ArticleResponse(
                article.getId(),
                article.getCategoryId(),
                categoryName,
                article.getTitle(),
                article.getSummary(),
                article.getContent(),
                article.getViewCount(),
                article.getLikeCount(),
                article.getFavoriteCount(),
                article.isPublished(),
                article.getPublishTime() != null ? article.getPublishTime().toString() : null,
                article.getCreateTime() != null ? article.getCreateTime().toString() : null,
                favorited,
                liked
            );
    }
}
