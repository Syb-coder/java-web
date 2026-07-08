// 声明当前类所属的包路径，dto 子包用于存放数据传输对象
package com.example.java3.dto;

/**
 * 平台统计数据响应 DTO
 * <p>
 * 用于数据可视化：用户数、商品数、订单数、月度交易量、热门品类等。
 * </p>
 */
public class StatsResponse {

    // 学生用户总数，统计所有注册用户
    /** 学生用户总数 */
    private long userCount;

    // 封禁用户数，统计 status 为 BANNED 的用户
    /** 封禁用户数 */
    private long bannedUserCount;

    // 商品总数，统计所有商品记录
    /** 商品总数 */
    private long productCount;

    // 待审核商品数，统计 auditStatus 为 PENDING 的商品
    /** 待审核商品数 */
    private long pendingProductCount;

    // 已通过商品数，统计 auditStatus 为 APPROVED 的商品
    /** 已通过商品数 */
    private long approvedProductCount;

    // 订单总数，统计所有订单记录
    /** 订单总数 */
    private long orderCount;

    // 已完成订单数，统计 status 为 COMPLETED 的订单
    /** 已完成订单数 */
    private long completedOrderCount;

    // 未读反馈数，统计未处理（未回复）的用户反馈
    /** 未读反馈数 */
    private long pendingFeedbackCount;

    // 无参构造方法，供 Service 层创建对象后逐项 setter 填充统计数据
    public StatsResponse() {
    }

    // 获取学生用户总数
    public long getUserCount() {
        return userCount;
    }

    // 设置学生用户总数
    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    // 获取封禁用户数
    public long getBannedUserCount() {
        return bannedUserCount;
    }

    // 设置封禁用户数
    public void setBannedUserCount(long bannedUserCount) {
        this.bannedUserCount = bannedUserCount;
    }

    // 获取商品总数
    public long getProductCount() {
        return productCount;
    }

    // 设置商品总数
    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }

    // 获取待审核商品数
    public long getPendingProductCount() {
        return pendingProductCount;
    }

    // 设置待审核商品数
    public void setPendingProductCount(long pendingProductCount) {
        this.pendingProductCount = pendingProductCount;
    }

    // 获取已通过商品数
    public long getApprovedProductCount() {
        return approvedProductCount;
    }

    // 设置已通过商品数
    public void setApprovedProductCount(long approvedProductCount) {
        this.approvedProductCount = approvedProductCount;
    }

    // 获取订单总数
    public long getOrderCount() {
        return orderCount;
    }

    // 设置订单总数
    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }

    // 获取已完成订单数
    public long getCompletedOrderCount() {
        return completedOrderCount;
    }

    // 设置已完成订单数
    public void setCompletedOrderCount(long completedOrderCount) {
        this.completedOrderCount = completedOrderCount;
    }

    // 获取未读反馈数
    public long getPendingFeedbackCount() {
        return pendingFeedbackCount;
    }

    // 设置未读反馈数
    public void setPendingFeedbackCount(long pendingFeedbackCount) {
        this.pendingFeedbackCount = pendingFeedbackCount;
    }
}
