// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

// 导入 Comment 实体类，用于构造响应 DTO
import com.example.java3.model.Comment;

// 导入 DateTimeFormatter，用于将 LocalDateTime 格式化为前端可读字符串
import java.time.format.DateTimeFormatter;

/**
 * 评论响应 DTO
 * <p>
 * 返回给前端的评论数据，补齐用户昵称与头像等冗余字段，支持层级展示。
 * </p>
 */
public class CommentResponse {

    // 时间格式化器，统一格式为 yyyy-MM-dd HH:mm:ss
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 评论主键 ID
    private Long id;
    // 关联商品 ID
    private Long productId;
    // 评论者用户 ID
    private Long userId;
    // 评论者昵称（冗余字段）
    private String username;
    // 评论者头像 URL（冗余字段）
    private String avatar;
    // 评论内容
    private String content;
    // 父评论 ID，用于构建回复层级
    private Long parentId;
    // 评论创建时间字符串（已格式化）
    private String createdAt;

    /**
     * 由 Comment 实体构造响应
     * <p>
     * Service 层查询用户昵称与头像后传入，避免前端二次请求用户信息。
     * </p>
     *
     * @param c        评论实体
     * @param username 评论者昵称
     * @param avatar   评论者头像 URL
     */
    public CommentResponse(Comment c, String username, String avatar) {
        this.id = c.getId();                                                // 赋值评论 ID
        this.productId = c.getProductId();                                  // 赋值商品 ID
        this.userId = c.getUserId();                                        // 赋值评论者 ID
        this.username = username;                                           // 赋值评论者昵称
        this.avatar = avatar;                                               // 赋值评论者头像
        this.content = c.getContent();                                      // 赋值评论内容
        this.parentId = c.getParentId();                                    // 赋值父评论 ID
        // 创建时间非空时格式化为字符串，否则置 null，避免 NPE
        this.createdAt = c.getCreatedAt() != null ? c.getCreatedAt().format(FMT) : null;
    }

    // 获取评论 ID
    public Long getId() {
        return id;
    }

    // 设置评论 ID
    public void setId(Long id) {
        this.id = id;
    }

    // 获取商品 ID
    public Long getProductId() {
        return productId;
    }

    // 设置商品 ID
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 获取评论者用户 ID
    public Long getUserId() {
        return userId;
    }

    // 设置评论者用户 ID
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // 获取评论者昵称
    public String getUsername() {
        return username;
    }

    // 设置评论者昵称
    public void setUsername(String username) {
        this.username = username;
    }

    // 获取评论者头像 URL
    public String getAvatar() {
        return avatar;
    }

    // 设置评论者头像 URL
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

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

    // 获取创建时间字符串
    public String getCreatedAt() {
        return createdAt;
    }

    // 设置创建时间字符串
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
