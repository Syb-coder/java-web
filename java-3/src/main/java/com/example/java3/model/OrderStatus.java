// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

/**
 * 订单状态枚举
 * <p>
 * 状态流转：
 * <pre>
 * PENDING（待付款） -> PAID（已付款） -> COMPLETED（已完成）
 *      |                  |
 *      +--> CANCELLED     +--> CANCELLED
 * </pre>
 * 已完成订单可由买家发起评价。
 * </p>
 */
public enum OrderStatus {
    // 待付款：买家下单后初始状态
    PENDING,
    // 已付款：买家确认付款（线下自提场景下表示达成交易意向）
    PAID,
    // 已完成：买卖双方线下完成自提，订单结束
    COMPLETED,
    // 已取消：买家主动取消或纠纷介入后取消
    CANCELLED
}
