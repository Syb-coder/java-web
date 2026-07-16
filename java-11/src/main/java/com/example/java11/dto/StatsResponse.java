package com.example.java11.dto;

import java.util.List;

/**
 * 统计数据响应 DTO
 * <p>
 * 用于返回站点的运营统计数据，包含用户、帖子、评论等总数，
 * 待审核帖子数、待处理举报数、今日新增数据，以及热门帖子与热门标签列表。
 * 主要用于管理后台首页的数据看板展示。
 * </p>
 */
public class StatsResponse {

    /** 用户总数 */
    private Long totalUsers;

    /** 帖子总数 */
    private Long totalPosts;

    /** 评论总数 */
    private Long totalComments;

    /** 待审核帖子数 */
    private Long pendingPosts;

    /** 待处理举报数 */
    private Long pendingReports;

    /** 今日新增用户数 */
    private Long todayNewUsers;

    /** 今日新增帖子数 */
    private Long todayNewPosts;

    /** 热门帖子列表 */
    private List<PostResponse> hotPosts;

    /** 热门标签列表 */
    private List<TagResponse> hotTags;

    /**
     * 默认无参构造器
     */
    public StatsResponse() {
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(Long totalPosts) {
        this.totalPosts = totalPosts;
    }

    public Long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }

    public Long getPendingPosts() {
        return pendingPosts;
    }

    public void setPendingPosts(Long pendingPosts) {
        this.pendingPosts = pendingPosts;
    }

    public Long getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(Long pendingReports) {
        this.pendingReports = pendingReports;
    }

    public Long getTodayNewUsers() {
        return todayNewUsers;
    }

    public void setTodayNewUsers(Long todayNewUsers) {
        this.todayNewUsers = todayNewUsers;
    }

    public Long getTodayNewPosts() {
        return todayNewPosts;
    }

    public void setTodayNewPosts(Long todayNewPosts) {
        this.todayNewPosts = todayNewPosts;
    }

    public List<PostResponse> getHotPosts() {
        return hotPosts;
    }

    public void setHotPosts(List<PostResponse> hotPosts) {
        this.hotPosts = hotPosts;
    }

    public List<TagResponse> getHotTags() {
        return hotTags;
    }

    public void setHotTags(List<TagResponse> hotTags) {
        this.hotTags = hotTags;
    }
}
