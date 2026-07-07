// 声明包路径
package com.example.java8.model;

/**
 * 订单状态枚举
 * <p>
 * 状态流转顺序：待确认 → 量体中 → 裁剪中 → 缝制中 → 试衣中 → 已完成。
 * 每次推进只允许前进一格，不允许跳跃或回退，保证生产流程规范。
 * 枚举的 ordinal() 即代表流程顺序，便于 OrderService.advanceStatus() 通过下标运算推进。
 * </p>
 */
public enum OrderStatus {

    /** 待确认：用户刚下单，等待客服/管理员确认订单信息 */
    PENDING,

    /** 量体中：管理员确认后进入量体环节（即使已自助录入尺寸，仍需工坊二次复核） */
    MEASURING,

    /** 裁剪中：依据量体数据裁剪面料 */
    CUTTING,

    /** 缝制中：进入工坊缝制成衣 */
    SEWING,

    /** 试衣中：成衣完成后通知客户试穿，需调整则可能回到缝制中 */
    FITTING,

    /** 已完成：客户确认合身，订单流程结束 */
    COMPLETED
}
