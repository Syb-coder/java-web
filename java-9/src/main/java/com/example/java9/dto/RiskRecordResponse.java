package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import java.time.LocalDateTime;  // 导入日期时间类

/**
 * 风控记录响应 DTO
 * <p>
 * 返回风控记录的详情，包含触发对象、风险类型、处理状态与处理人信息。
 * </p>
 */
public class RiskRecordResponse {  // 风控记录响应 DTO 类定义

    /** 风控记录 ID */
    private Long id;  // 风控记录唯一标识

    /** 触发对象类型：USER/MERCHANT/ORDER/PAYMENT */
    private String targetType;  // 触发对象类型:USER(用户)/MERCHANT(商户)/ORDER(订单)/PAYMENT(支付)

    /** 触发对象 ID */
    private Long targetId;  // 触发对象的具体 ID

    /** 风险类型：FREQUENCY/AMOUNT/BEHAVIOR */
    private String riskType;  // 风险类型:FREQUENCY(频次)/AMOUNT(金额)/BEHAVIOR(行为)

    /** 风险等级：LOW/MEDIUM/HIGH */
    private String riskLevel;  // 风险等级:LOW(低)/MEDIUM(中)/HIGH(高)

    /** 风险描述 */
    private String description;  // 风险描述,说明触发规则的具体情况

    /** 关联订单号（可为空） */
    private String relatedOrderNo;  // 关联订单号,无关联订单时为 null

    /** 处理状态：PENDING/HANDLED/IGNORED */
    private String status;  // 处理状态:PENDING(待处理)/HANDLED(已处理)/IGNORED(已忽略)

    /** 处理人用户名（处理后填充） */
    private String handledBy;  // 处理人用户名,处理后填充

    /** 处理备注 */
    private String handleRemark;  // 处理备注,记录处理依据

    /** 处理时间 */
    private LocalDateTime handledAt;  // 处理时间戳

    /** 记录创建时间 */
    private LocalDateTime createdAt;  // 风控记录创建时间戳

    /** 记录最后修改时间 */
    private LocalDateTime updateTime;  // 风控记录最后修改时间戳

    // —— id 字段的 getter/setter ——
    public Long getId() {  // 获取风控记录 ID
        return id;  // 返回风控记录 ID
    }

    public void setId(Long id) {  // 设置风控记录 ID
        this.id = id;  // 赋值风控记录 ID
    }

    // —— targetType 字段的 getter/setter ——
    public String getTargetType() {  // 获取触发对象类型
        return targetType;  // 返回触发对象类型
    }

    public void setTargetType(String targetType) {  // 设置触发对象类型
        this.targetType = targetType;  // 赋值触发对象类型
    }

    // —— targetId 字段的 getter/setter ——
    public Long getTargetId() {  // 获取触发对象 ID
        return targetId;  // 返回触发对象 ID
    }

    public void setTargetId(Long targetId) {  // 设置触发对象 ID
        this.targetId = targetId;  // 赋值触发对象 ID
    }

    // —— riskType 字段的 getter/setter ——
    public String getRiskType() {  // 获取风险类型
        return riskType;  // 返回风险类型
    }

    public void setRiskType(String riskType) {  // 设置风险类型
        this.riskType = riskType;  // 赋值风险类型
    }

    // —— riskLevel 字段的 getter/setter ——
    public String getRiskLevel() {  // 获取风险等级
        return riskLevel;  // 返回风险等级
    }

    public void setRiskLevel(String riskLevel) {  // 设置风险等级
        this.riskLevel = riskLevel;  // 赋值风险等级
    }

    // —— description 字段的 getter/setter ——
    public String getDescription() {  // 获取风险描述
        return description;  // 返回风险描述
    }

    public void setDescription(String description) {  // 设置风险描述
        this.description = description;  // 赋值风险描述
    }

    // —— relatedOrderNo 字段的 getter/setter ——
    public String getRelatedOrderNo() {  // 获取关联订单号
        return relatedOrderNo;  // 返回关联订单号
    }

    public void setRelatedOrderNo(String relatedOrderNo) {  // 设置关联订单号
        this.relatedOrderNo = relatedOrderNo;  // 赋值关联订单号
    }

    // —— status 字段的 getter/setter ——
    public String getStatus() {  // 获取处理状态
        return status;  // 返回处理状态
    }

    public void setStatus(String status) {  // 设置处理状态
        this.status = status;  // 赋值处理状态
    }

    // —— handledBy 字段的 getter/setter ——
    public String getHandledBy() {  // 获取处理人
        return handledBy;  // 返回处理人用户名
    }

    public void setHandledBy(String handledBy) {  // 设置处理人
        this.handledBy = handledBy;  // 赋值处理人用户名
    }

    // —— handleRemark 字段的 getter/setter ——
    public String getHandleRemark() {  // 获取处理备注
        return handleRemark;  // 返回处理备注
    }

    public void setHandleRemark(String handleRemark) {  // 设置处理备注
        this.handleRemark = handleRemark;  // 赋值处理备注
    }

    // —— handledAt 字段的 getter/setter ——
    public LocalDateTime getHandledAt() {  // 获取处理时间
        return handledAt;  // 返回处理时间
    }

    public void setHandledAt(LocalDateTime handledAt) {  // 设置处理时间
        this.handledAt = handledAt;  // 赋值处理时间
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
