package com.example.java12.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;  // JPA 注解
import java.time.LocalDateTime;  // 时间类型

/**
 * 操作日志实体
 * <p>
 * 对应 operate_logs 表，记录管理员/版主的后台操作行为。
 * 包括操作人、操作类型、操作对象、IP 地址等信息，用于审计追溯。
 * 操作日志不可删除，保留 180 天。
 * </p>
 */
@Entity  // 声明为 JPA 实体
@Table(name = "operate_logs")  // 映射到 operate_logs 表
public class OperateLog {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 操作人用户 ID（外键，管理员或版主） */
    @Column(nullable = false)
    private Long adminId;

    /** 操作人昵称（冗余存储，便于日志展示） */
    @Column(nullable = false, length = 15)
    private String adminName;

    /** 操作类型（如"删除帖子"、"封禁用户"、"分配版主"等） */
    @Column(nullable = false, length = 50)
    private String action;

    /** 操作对象描述（如"帖子ID:123"、"用户:张三"等） */
    @Column(length = 200)
    private String target;

    /** 操作 IP 地址 */
    @Column(length = 50)
    private String ip;

    /** 操作时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public OperateLog() {
    }

    /**
     * 业务构造器：记录操作日志时使用
     *
     * @param adminId   操作人 ID
     * @param adminName 操作人昵称
     * @param action    操作类型
     * @param target    操作对象描述
     * @param ip        操作 IP
     */
    public OperateLog(Long adminId, String adminName, String action, String target, String ip) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.action = action;
        this.target = target;
        this.ip = ip;
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

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
