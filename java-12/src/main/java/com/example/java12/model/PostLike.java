package com.example.java12.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;  // JPA 注解
import java.time.LocalDateTime;  // 时间类型

/**
 * 帖子点赞实体
 * <p>
 * 对应 post_likes 表，记录用户对帖子的点赞关系。
 * 通过 (userId, postId) 联合唯一约束保证同一用户对同一帖子仅可点赞一次（幂等）。
 * 再次点赞则删除该记录，实现取消点赞。
 * </p>
 */
@Entity  // 声明为 JPA 实体
@Table(name = "post_likes",  // 映射到 post_likes 表
        uniqueConstraints = @UniqueConstraint(  // 联合唯一约束
                name = "uk_user_post_like",
                columnNames = {"userId", "postId"}
        ))
public class PostLike {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 点赞者用户 ID（外键） */
    @Column(nullable = false)
    private Long userId;

    /** 被点赞的帖子 ID（外键） */
    @Column(nullable = false)
    private Long postId;

    /** 点赞时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public PostLike() {
    }

    /**
     * 业务构造器：点赞帖子时使用
     *
     * @param userId 点赞者 ID
     * @param postId 帖子 ID
     */
    public PostLike(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
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
