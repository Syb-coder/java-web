package com.example.java11.dto;

import java.time.LocalDateTime;

/**
 * 举报信息响应 DTO
 * <p>
 * 用于返回举报记录的完整信息，包含举报人、目标、原因、描述、状态及处理备注。
 * 主要用于管理后台的举报列表及举报详情展示。
 * </p>
 */
public class ReportResponse {

    /** 举报 ID */
    private Long id;

    /** 举报人 ID */
    private Long reporterId;

    /** 举报人用户名 */
    private String reporterName;

    /** 目标类型（POST/COMMENT） */
    private String targetType;

    /** 目标 ID */
    private Long targetId;

    /** 举报理由 */
    private String reason;

    /** 补充说明 */
    private String description;

    /** 举报状态 */
    private String status;

    /** 处理备注 */
    private String handleRemark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 默认无参构造器
     */
    public ReportResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHandleRemark() {
        return handleRemark;
    }

    public void setHandleRemark(String handleRemark) {
        this.handleRemark = handleRemark;
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
