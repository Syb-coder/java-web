package com.example.java6.model;

/**
 * 新闻资讯分类枚举
 *
 * <p>用于区分网络安全官网发布的各类新闻资讯类型，
 * 便于前台分类展示与后台分类筛选。</p>
 */
public enum NewsCategory {

    /** 安全新闻：网络安全领域重大事件报道 */
    SECURITY_NEWS,

    /** 行业动态：网络安全行业发展趋势、企业动向 */
    INDUSTRY_TREND,

    /** 政策解读：国家网络安全政策法规权威解读 */
    POLICY_INTERPRETATION,

    /** 漏洞预警：重大漏洞通报与安全预警 */
    VULNERABILITY_ALERT
}
