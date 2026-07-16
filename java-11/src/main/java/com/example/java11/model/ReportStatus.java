package com.example.java11.model;  // 模型层包，存放实体与枚举

/**
 * 举报处理状态枚举
 * <p>
 * 标识举报记录在处理流程中的当前状态，便于追踪处理进度。
 * PENDING: 待处理，新提交举报的默认状态；
 * RESOLVED: 已处理（成立），举报成立并已执行相应处置；
 * REJECTED: 已驳回（不成立），举报不成立，未执行处置。
 * </p>
 */
public enum ReportStatus {  // 举报处理状态枚举
    PENDING,   // 待处理
    RESOLVED,  // 已处理（成立）
    REJECTED   // 已驳回（不成立）
}
