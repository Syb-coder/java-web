package com.example.java12.dto;  // DTO 层包

/**
 * 统计数据响应 DTO
 * <p>
 * 用于首页和后台仪表盘展示站点概览数据。
 * </p>
 */
public class StatsResponse {

    /** 总用户数 */
    private long totalUsers;

    /** 总帖子数 */
    private long totalPosts;

    /** 总评论数 */
    private long totalComments;

    /** 总板块数 */
    private long totalPlates;

    /** 今日新帖数 */
    private long todayPosts;

    /**
     * 全参构造器
     */
    public StatsResponse(long totalUsers, long totalPosts, long totalComments, long totalPlates, long todayPosts) {
        this.totalUsers = totalUsers;
        this.totalPosts = totalPosts;
        this.totalComments = totalComments;
        this.totalPlates = totalPlates;
        this.todayPosts = todayPosts;
    }

    // ===== getter / setter =====

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(long totalPosts) {
        this.totalPosts = totalPosts;
    }

    public long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }

    public long getTotalPlates() {
        return totalPlates;
    }

    public void setTotalPlates(long totalPlates) {
        this.totalPlates = totalPlates;
    }

    public long getTodayPosts() {
        return todayPosts;
    }

    public void setTodayPosts(long todayPosts) {
        this.todayPosts = todayPosts;
    }
}
