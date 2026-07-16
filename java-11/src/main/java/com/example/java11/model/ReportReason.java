package com.example.java11.model;  // 模型层包，存放实体与枚举

/**
 * 举报理由枚举
 * <p>
 * 预定义的举报理由分类，规范用户举报行为，便于管理员快速分类处理。
 * SPAM: 垃圾广告，包含商业推广、引流等内容；
 * PORNOGRAPHY: 色情低俗，包含色情、低俗、擦边等内容；
 * VIOLENCE: 暴力血腥，包含血腥、暴力、自残等内容；
 * ILLEGAL: 违法犯罪，包含违法、违规、有害社会秩序等内容；
 * OFF_TOPIC: 不相关内容，与板块主题严重不符的水帖；
 * OTHER: 其他，需在 description 字段补充说明。
 * </p>
 */
public enum ReportReason {  // 举报理由枚举
    SPAM,          // 垃圾广告
    PORNOGRAPHY,   // 色情低俗
    VIOLENCE,      // 暴力血腥
    ILLEGAL,       // 违法犯罪
    OFF_TOPIC,     // 不相关内容
    OTHER          // 其他
}
