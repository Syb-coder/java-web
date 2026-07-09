package com.example.java9.model;  // 当前枚举所在的包路径，归属于 model 实体/枚举层

/**
 * 商户入驻状态枚举
 * <p>
 * 标识 B 端商户入驻审核进度：
 * - PENDING：待审核（刚提交入驻申请）；
 * - APPROVED：已通过（运营审核通过，可正常收款）；
 * - REJECTED：已拒绝（资质不符或信息有误）。
 * </p>
 */
public enum MerchantStatus {  // 商户入驻状态枚举，控制商户能否进入收款流程
    /** 待审核 */
    PENDING,   // 待审核：商户刚提交入驻申请，运营尚未审核，禁止收款
    /** 已通过 */
    APPROVED,  // 已通过：资质审核通过，可正常收款与对账
    /** 已拒绝 */
    REJECTED   // 已拒绝：资质不符或信息有误，需重新提交资料
}
