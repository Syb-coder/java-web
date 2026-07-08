package com.example.java3.dto;

/**
 * 平台统计数据响应 DTO
 * <p>
 * 用于数据可视化：用户数、商品数、订单数、月度交易量、热门品类等。
 * </p>
 */
public class StatsResponse {

    /** 学生用户总数 */
    private long userCount;

    /** 封禁用户数 */
    private long bannedUserCount;

    /** 商品总数 */
    private long productCount;

    /** 待审核商品数 */
    private long pendingProductCount;

    /** 已通过商品数 */
    private long approvedProductCount;

    /** 订单总数 */
    private long orderCount;

    /** 已完成订单数 */
    private long completedOrderCount;

    /** 未读反馈数 */
    private long pendingFeedbackCount;

    public StatsResponse() {
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getBannedUserCount() {
        return bannedUserCount;
    }

    public void setBannedUserCount(long bannedUserCount) {
        this.bannedUserCount = bannedUserCount;
    }

    public long getProductCount() {
        return productCount;
    }

    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }

    public long getPendingProductCount() {
        return pendingProductCount;
    }

    public void setPendingProductCount(long pendingProductCount) {
        this.pendingProductCount = pendingProductCount;
    }

    public long getApprovedProductCount() {
        return approvedProductCount;
    }

    public void setApprovedProductCount(long approvedProductCount) {
        this.approvedProductCount = approvedProductCount;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }

    public long getCompletedOrderCount() {
        return completedOrderCount;
    }

    public void setCompletedOrderCount(long completedOrderCount) {
        this.completedOrderCount = completedOrderCount;
    }

    public long getPendingFeedbackCount() {
        return pendingFeedbackCount;
    }

    public void setPendingFeedbackCount(long pendingFeedbackCount) {
        this.pendingFeedbackCount = pendingFeedbackCount;
    }
}
