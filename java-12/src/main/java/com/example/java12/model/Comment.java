package com.example.java12.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;  // JPA 注解
import java.time.LocalDateTime;  // 时间类型

/**
 * 评论实体
 * <p>
 * 对应 comments 表，存储用户对帖子的评论。
 * 支持嵌套一层回复（通过 parentId 关联父评论）。
 * 支持软删除（isDeleted）和版主屏蔽（isHidden）。
 * </p>
 */
@Entity  // 声明为 JPA 实体
@Table(name = "comments")  // 映射到 comments 表
public class Comment {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属帖子 ID（外键） */
    @Column(nullable = false)
    private Long postId;

    /** 评论者用户 ID（外键） */
    @Column(nullable = false)
    private Long userId;

    /** 父评论 ID（null 表示直接评论帖子，非 null 表示回复某条评论，仅支持嵌套一层） */
    private Long parentId;

    /** 评论内容（1-1000字符） */
    @Column(nullable = false, length = 1000)
    private String content;

    /** 是否被版主屏蔽（隐藏标记） */
    @Column(nullable = false)
    private Boolean isHidden;

    /** 是否软删除（评论者或版主删除后标记为 true） */
    @Column(nullable = false)
    private Boolean isDeleted;

    /** 评论时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Comment() {
    }

    /**
     * 业务构造器：发表评论时使用
     *
     * @param postId   帖子 ID
     * @param userId   评论者 ID
     * @param parentId 父评论 ID（可为 null）
     * @param content  评论内容
     */
    public Comment(Long postId, Long userId, Long parentId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.parentId = parentId;
        this.content = content;
        this.isHidden = false;
        this.isDeleted = false;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新前回调，自动刷新 updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    // ===== getter / setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
