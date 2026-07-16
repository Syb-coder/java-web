package com.example.java11.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 * <p>
 * 对应 operation_logs 表，记录用户与管理员的关键操作行为，用于审计追踪。
 * 支持多操作人类型（USER/ADMIN）与多目标类型，便于关联定位。
 * </p>
 */
@Entity
@Table(name = "operation_logs")
public class OperationLog {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 操作人 ID */
    @Column
    private Long operatorId;

    /** 操作人类型，USER 或 ADMIN */
    @Column(length = 20)
    private String operatorType;

    /** 操作动作，如 LOGIN、POST_CREATE、POST_DELETE 等 */
    @Column(nullable = false, length = 50)
    private String action;

    /** 操作目标类型，如 POST、COMMENT 等 */
    @Column(length = 20)
    private String targetType;

    /** 操作目标 ID */
    @Column
    private Long targetId;

    /** 操作详情，JSON 或文本描述 */
    @Column(length = 1000)
    private String detail;

    /** 操作者 IP 地址 */
    @Column(length = 50)
    private String ip;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public OperationLog() {
    }

    /**
     * 业务构造器：记录操作日志时使用
     *
     * @param operatorId   操作人 ID
     * @param operatorType 操作人类型
     * @param action       操作动作
     */
    public OperationLog(Long operatorId, String operatorType, String action) {
        this.operatorId = operatorId;
        this.operatorType = operatorType;
        this.action = action;
        this.createdAt = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新前回调，自动刷新 updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
