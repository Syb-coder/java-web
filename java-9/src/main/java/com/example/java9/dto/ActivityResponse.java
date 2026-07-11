package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import java.time.LocalDateTime;  // 导入日期时间类

/**
 * 活动响应 DTO
 * <p>
 * 返回活动的详细信息，包含活动时间区间与启用状态。
 * </p>
 */
public class ActivityResponse {  // 活动响应 DTO 类定义

    /** 活动 ID */
    private Long id;  // 活动唯一标识

    /** 活动标题 */
    private String title;  // 活动标题

    /** 活动类型：COUPON/INTEREST_RATE/SIGN_IN */
    private String type;  // 活动类型:COUPON(优惠券)/INTEREST_RATE(利率加成)/SIGN_IN(签到)

    /** 活动描述 */
    private String description;  // 活动描述

    /** 活动开始时间 */
    private LocalDateTime startTime;  // 活动开始时间(已解析为 LocalDateTime)

    /** 活动结束时间 */
    private LocalDateTime endTime;  // 活动结束时间(已解析为 LocalDateTime)

    /** 是否启用 */
    private Boolean active;  // 启用标志:true=启用,false=停用

    /** 活动创建时间 */
    private LocalDateTime createdAt;  // 活动创建时间戳

    /** 活动最后修改时间 */
    private LocalDateTime updateTime;  // 活动最后修改时间戳

    // —— id 字段的 getter/setter ——
    public Long getId() {  // 获取活动 ID
        return id;  // 返回活动 ID
    }

    public void setId(Long id) {  // 设置活动 ID
        this.id = id;  // 赋值活动 ID
    }

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
    public LocalDateTime getStartTime() {  // 获取开始时间
        return startTime;  // 返回开始时间
    }

    public void setStartTime(LocalDateTime startTime) {  // 设置开始时间
        this.startTime = startTime;  // 赋值开始时间
    }

    // —— endTime 字段的 getter/setter ——
    public LocalDateTime getEndTime() {  // 获取结束时间
        return endTime;  // 返回结束时间
    }

    public void setEndTime(LocalDateTime endTime) {  // 设置结束时间
        this.endTime = endTime;  // 赋值结束时间
    }

    // —— active 字段的 getter/setter ——
    public Boolean getActive() {  // 获取启用状态
        return active;  // 返回是否启用
    }

    public void setActive(Boolean active) {  // 设置启用状态
        this.active = active;  // 赋值启用状态
    }

    // —— createdAt 字段的 getter/setter ——
    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;  // 返回创建时间
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;  // 赋值创建时间
    }

    // —— updateTime 字段的 getter/setter ——
    public LocalDateTime getUpdateTime() {  // 获取最后修改时间
        return updateTime;  // 返回最后修改时间
    }

    public void setUpdateTime(LocalDateTime updateTime) {  // 设置最后修改时间
        this.updateTime = updateTime;  // 赋值最后修改时间
    }
}
