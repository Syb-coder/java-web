package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入非空字符串校验注解

/**
 * 活动创建请求 DTO
 * <p>
 * 运营人员创建营销活动，支持 COUPON（优惠券）/INTEREST_RATE（利率加成）/SIGN_IN（签到）三类。
 * startTime 与 endTime 使用 ISO 格式字符串接收，服务端解析为 LocalDateTime。
 * </p>
 */
public class ActivityRequest {  // 活动创建请求 DTO 类定义

    /** 活动标题 */
    @NotBlank(message = "活动标题不能为空")  // 非空字符串校验:活动标题必填
    private String title;  // 活动标题

    /** 活动类型：COUPON/INTEREST_RATE/SIGN_IN */
    @NotBlank(message = "活动类型不能为空")  // 非空字符串校验:活动类型必填
    private String type;  // 活动类型:COUPON(优惠券)/INTEREST_RATE(利率加成)/SIGN_IN(签到)

    /** 活动描述（可选） */
    private String description;  // 活动描述(选填)

    /** 活动开始时间（ISO 格式字符串，如 2026-07-08T00:00:00） */
    @NotBlank(message = "开始时间不能为空")  // 非空字符串校验:开始时间必填
    private String startTime;  // 活动开始时间(ISO 格式字符串,服务端解析为 LocalDateTime)

    /** 活动结束时间（ISO 格式字符串，如 2026-07-31T23:59:59） */
    @NotBlank(message = "结束时间不能为空")  // 非空字符串校验:结束时间必填
    private String endTime;  // 活动结束时间(ISO 格式字符串,服务端解析为 LocalDateTime)

    // —— title 字段的 getter/setter ——
    public String getTitle() {  // 获取活动标题
        return title;  // 返回活动标题
    }

    public void setTitle(String title) {  // 设置活动标题
        this.title = title;  // 赋值活动标题
    }

    // —— type 字段的 getter/setter ——
    public String getType() {  // 获取活动类型
        return type;  // 返回活动类型
    }

    public void setType(String type) {  // 设置活动类型
        this.type = type;  // 赋值活动类型
    }

    // —— description 字段的 getter/setter ——
    public String getDescription() {  // 获取活动描述
        return description;  // 返回活动描述
    }

    public void setDescription(String description) {  // 设置活动描述
        this.description = description;  // 赋值活动描述
    }

    // —— startTime 字段的 getter/setter ——
    public String getStartTime() {  // 获取开始时间
        return startTime;  // 返回开始时间字符串
    }

    public void setStartTime(String startTime) {  // 设置开始时间
        this.startTime = startTime;  // 赋值开始时间字符串
    }

    // —— endTime 字段的 getter/setter ——
    public String getEndTime() {  // 获取结束时间
        return endTime;  // 返回结束时间字符串
    }

    public void setEndTime(String endTime) {  // 设置结束时间
        this.endTime = endTime;  // 赋值结束时间字符串
    }
}
