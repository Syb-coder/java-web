// 声明包路径
package com.example.java2.dto;

// 导入实体类
import com.example.java2.model.Article;

/**
 * 文章列表项响应 DTO（精简版，不含正文）
 * <p>
 * 用于文章列表、搜索结果、收藏列表等场景，避免传输大段正文。
 * </p>
 *
 * @param id            文章 ID
 * @param categoryId    所属分类 ID
 * @param categoryName  所属分类名称
 * @param title         标题
 * @param summary       摘要
 * @param viewCount     阅读量
 * @param likeCount     点赞数
 * @param favoriteCount 收藏数
 * @param publishTime   发布时间
 * @param createTime    创建时间
 * @param updateTime    最后修改时间
 */
// 采用 record 声明：不可变响应 DTO，自动生成 accessor/equals/hashCode/toString；刻意省略 content 字段，列表场景避免传输大段正文
public record ArticleListItem(
        Long id,            // 文章主键 ID
        Long categoryId,    // 所属分类 ID
        String categoryName,// 所属分类名称，由 Service 层关联查询后传入，避免前端二次请求
        String title,       // 文章标题
        String summary,     // 文章摘要，列表展示用
        int viewCount,      // 阅读量
        int likeCount,      // 点赞数
        int favoriteCount,  // 收藏数
        String publishTime,  // 发布时间字符串
        String createTime,  // 创建时间字符串
        String updateTime   // 最后修改时间字符串
) {
    /**
     * 由文章实体构造列表项（分类名由 Service 层查询后传入）
     *
     * @param article      文章实体
     * @param categoryName 分类名称
     * @return 列表项
     */
    // 使用静态工厂方法 from()：分类名需外部传入（避免 DAO 层耦合关联查询），工厂方法集中处理字段映射与判空
    public static ArticleListItem from(Article article, String categoryName) {
        return new ArticleListItem(
                article.getId(),
                article.getCategoryId(),
                categoryName,
                article.getTitle(),
                article.getSummary(),
                article.getViewCount(),
                article.getLikeCount(),
                article.getFavoriteCount(),
                article.getPublishTime() != null ? article.getPublishTime().toString() : null,
                article.getCreateTime() != null ? article.getCreateTime().toString() : null,
                article.getUpdateTime() != null ? article.getUpdateTime().toString() : null
        );
    }
}
