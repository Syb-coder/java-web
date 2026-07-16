package com.example.java11.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 举报请求 DTO
 * <p>
 * 用于接收用户提交的举报信息，包含目标类型、目标 ID、举报原因与补充说明。
 * targetType 字段为 POST 或 COMMENT，reason 字段对应举报理由枚举值。
 * </p>
 */
public class ReportRequest {

    /** 目标类型（POST/COMMENT） */
    @NotNull(message = "目标类型不能为空")
    private String targetType;

    /** 目标 ID */
    @NotNull(message = "目标 ID 不能为空")
    private Long targetId;

    /** 举报理由（枚举值） */
    @NotNull(message = "举报原因不能为空")
    private String reason;

    /** 补充说明（最长 500 字符） */
    @Size(max = 500, message = "补充说明长度不能超过 500 字符")
    private String description;

    /**
     * 默认无参构造器
     */
    public ReportRequest() {
    }

    /**
     * 全参构造器
     *
     * @param targetType  目标类型
     * @param targetId    目标 ID
     * @param reason      举报原因
     * @param description 补充说明
     */
    public ReportRequest(String targetType, Long targetId, String reason, String description) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.description = description;
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
}
