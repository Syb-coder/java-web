package com.example.java3.model;

/**
 * 商品审核状态枚举
 * <p>
 * 用于商品人工审核流程的状态机管理：
 * <ul>
 *   <li>PENDING：待审核（学生刚发布，等待管理员审核）</li>
 *   <li>APPROVED：已通过（审核通过，前台可展示）</li>
 *   <li>REJECTED：已驳回（违规商品，禁止展示）</li>
 * </ul>
 * </p>
 */
public enum ProductAuditStatus {
    // 待审核：新发布商品的初始状态
    PENDING,
    // 已通过：管理员审核通过，可前台展示
    APPROVED,
    // 已驳回：违规或虚假商品，禁止展示
    REJECTED
}
