package com.example.java6.model;

/**
 * 举报类型枚举
 *
 * <p>用于公众举报违法和不良信息时标识举报对象类型，
 * 便于举报中心分类处置与统计研判。</p>
 */
public enum ReportType {

    /** 色情低俗：淫秽色情、低俗庸俗内容 */
    PORNOGRAPHY,

    /** 赌博诈骗：网络赌博、电信网络诈骗 */
    GAMBLING_FRAUD,

    /** 网络暴力：人身攻击、侮辱诽谤、人肉搜索 */
    CYBER_VIOLENCE,

    /** 谣言信息：编造传播虚假信息 */
    RUMOR,

    /** 违法违规：其他违法违规内容 */
    ILLEGAL_CONTENT,

    /** 其他：不属于上述分类的举报 */
    OTHER
}
