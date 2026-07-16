package com.example.java12.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;  // JPA 注解
import java.time.LocalDateTime;  // 时间类型

/**
 * 收藏实体
 * <p>
 * 对应 collects 表，记录用户对帖子的收藏关系。
 * 通过 (userId, postId) 联合唯一约束保证同一用户不会重复收藏同一帖子。
 * </p>
 */
@Entity  // 声明为 JPA 实体
@Table(name = "collects",  // 映射到 collects 表
        uniqueConstraints = @UniqueConstraint(  // 联合唯一约束
                name = "uk_user_post_collect",
                columnNames = {"userId", "postId"}
        ))
public class Collect {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 收藏者用户 ID（外键） */
    @Column(nullable = false)
    private Long userId;

    /** 被收藏的帖子 ID（外键） */
    @Column(nullable = false)
    private Long postId;

    /** 收藏时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Collect() {
    }

    /**
     * 业务构造器：收藏帖子时使用
     *
     * @param userId 收藏者 ID
     * @param postId 帖子 ID
     */
    public Collect(Long userId, Long postId) {
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
