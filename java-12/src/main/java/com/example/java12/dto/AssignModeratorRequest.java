package com.example.java12.dto;  // DTO 层包

import jakarta.validation.constraints.NotNull;  // 非空校验

/**
 * 分配版主请求 DTO
 * <p>
 * 管理员将指定用户分配为指定板块的版主。
 * </p>
 */
public class AssignModeratorRequest {

    /** 被分配版主的用户 ID */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 管理的板块 ID */
    @NotNull(message = "板块ID不能为空")
    private Long plateId;

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
}
