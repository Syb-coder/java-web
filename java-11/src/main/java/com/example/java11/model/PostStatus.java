package com.example.java11.model;  // 模型层包，存放实体与枚举

/**
 * 帖子状态枚举
 * <p>
 * 标识帖子在审核流程中的当前状态，控制帖子可见性与可交互性。
 * PENDING: 待审核，新发帖默认状态，仅发帖人与管理员可见；
 * APPROVED: 已通过审核，对所有用户可见；
 * REJECTED: 已拒绝，审核未通过，仅发帖人可见并提示拒绝原因；
 * DELETED: 已删除，逻辑删除状态，前台不可见。
 * </p>
 */
public enum PostStatus {  // 帖子状态枚举
    PENDING,   // 待审核
    APPROVED,  // 已通过
    REJECTED,  // 已拒绝
    DELETED    // 已删除
}
