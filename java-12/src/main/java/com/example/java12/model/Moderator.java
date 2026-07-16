package com.example.java12.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;  // JPA 注解
import java.time.LocalDateTime;  // 时间类型

/**
 * 版主-板块关联实体
 * <p>
 * 对应 moderators 表，记录版主与板块的管理关系。
 * 版主不是独立角色，而是普通用户被分配管理特定板块的权限。
 * 通过 (userId, plateId) 联合唯一约束保证同一用户不会被重复分配同一板块。
 * </p>
 */
@Entity  // 声明为 JPA 实体
@Table(name = "moderators",  // 映射到 moderators 表
        uniqueConstraints = @UniqueConstraint(  // 联合唯一约束
                name = "uk_user_plate_moderator",
                columnNames = {"userId", "plateId"}
        ))
public class Moderator {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 版主用户 ID（外键） */
    @Column(nullable = false)
    private Long userId;

    /** 管理的板块 ID（外键） */
    @Column(nullable = false)
    private Long plateId;

    /** 分配时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public Moderator() {
    }

    /**
     * 业务构造器：分配版主时使用
     *
     * @param userId  版主用户 ID
     * @param plateId 板块 ID
     */
    public Moderator(Long userId, Long plateId) {
        this.userId = userId;
        this.plateId = plateId;
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

    public Long getPlateId() {
        return plateId;
    }

    public void setPlateId(Long plateId) {
        this.plateId = plateId;
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
