package com.example.java6.model;

/**
 * 举报处理状态枚举
 *
 * <p>标识举报记录从受理到处置完成的处理阶段，
 * 用于举报中心跟踪处置进度与公众查询反馈。</p>
 */
public enum ReportStatus {

    /** 待处理：举报已受理，尚未开始处置 */
    PENDING,

    /** 处理中：举报正在核查处置中 */
    PROCESSING,

    /** 已处理：举报处置完成 */
    RESOLVED,

    /** 已忽略：经核实属于无效举报，不予处置 */
    IGNORED
}
