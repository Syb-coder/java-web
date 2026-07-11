package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import java.math.BigDecimal;  // 导入高精度十进制类,用于金额表示
import java.time.LocalDateTime;  // 导入日期时间类

/**
 * 理财产品响应 DTO
 * <p>
 * 返回理财产品的详细信息，包含募集进度相关字段。
 * remainAmount 为计算字段，等于 totalAmount - investedAmount。
 * </p>
 */
public class ProductResponse {  // 理财产品响应 DTO 类定义

    /** 产品 ID */
    private Long id;  // 产品唯一标识

    /** 产品名称 */
    private String name;  // 产品名称

    /** 产品类型：DEPOSIT/FUND/INSURANCE */
    private String type;  // 产品类型:DEPOSIT(存款)/FUND(基金)/INSURANCE(保险)

    /** 年化收益率（百分比） */
    private BigDecimal annualRate;  // 年化收益率(百分比)

    /** 起投金额（元） */
    private BigDecimal minAmount;  // 起投金额(元)

    /** 产品期限（天） */
    private Integer duration;  // 产品期限(天)

    /** 风险等级（1-5） */
    private Integer riskLevel;  // 风险等级(1~5)

    /** 募集总金额（元） */
    private BigDecimal totalAmount;  // 募集总金额(元)

    /** 已募集金额（元） */
    private BigDecimal investedAmount;  // 已募集金额(元),反映募集进度

    /** 剩余可投金额（计算字段 = totalAmount - investedAmount） */
    private BigDecimal remainAmount;  // 剩余可投金额(计算字段,由 getter 实时计算)

    /** 产品状态：RAISING/RAISED/SETTLED/CANCELLED */
    private String status;  // 产品状态:RAISING(募集期)/RAISED(募集完成)/SETTLED(已结算)/CANCELLED(已取消)

    /** 产品描述 */
    private String description;  // 产品描述

    /** 产品创建时间 */
    private LocalDateTime createdAt;  // 产品创建时间戳

    /** 产品最后修改时间 */
    private LocalDateTime updateTime;  // 产品最后修改时间戳

    // —— id 字段的 getter/setter ——
    public Long getId() {  // 获取产品 ID
        return id;  // 返回产品 ID
    }

    public void setId(Long id) {  // 设置产品 ID
        this.id = id;  // 赋值产品 ID
    }

    // —— name 字段的 getter/setter ——
    public String getName() {  // 获取产品名称
        return name;  // 返回产品名称
    }

    public void setName(String name) {  // 设置产品名称
        this.name = name;  // 赋值产品名称
    }

    // —— type 字段的 getter/setter ——
    public String getType() {  // 获取产品类型
        return type;  // 返回产品类型
    }

    public void setType(String type) {  // 设置产品类型
        this.type = type;  // 赋值产品类型
    }

    // —— annualRate 字段的 getter/setter ——
    public BigDecimal getAnnualRate() {  // 获取年化收益率
        return annualRate;  // 返回年化收益率
    }

    public void setAnnualRate(BigDecimal annualRate) {  // 设置年化收益率
        this.annualRate = annualRate;  // 赋值年化收益率
    }

    // —— minAmount 字段的 getter/setter ——
    public BigDecimal getMinAmount() {  // 获取起投金额
        return minAmount;  // 返回起投金额
    }

    public void setMinAmount(BigDecimal minAmount) {  // 设置起投金额
        this.minAmount = minAmount;  // 赋值起投金额
    }

    // —— duration 字段的 getter/setter ——
    public Integer getDuration() {  // 获取产品期限
        return duration;  // 返回产品期限
    }

    public void setDuration(Integer duration) {  // 设置产品期限
        this.duration = duration;  // 赋值产品期限
    }

    // —— riskLevel 字段的 getter/setter ——
    public Integer getRiskLevel() {  // 获取风险等级
        return riskLevel;  // 返回风险等级
    }

    public void setRiskLevel(Integer riskLevel) {  // 设置风险等级
        this.riskLevel = riskLevel;  // 赋值风险等级
    }

    // —— totalAmount 字段的 getter/setter ——
    public BigDecimal getTotalAmount() {  // 获取募集总金额
        return totalAmount;  // 返回募集总金额
    }

    public void setTotalAmount(BigDecimal totalAmount) {  // 设置募集总金额
        this.totalAmount = totalAmount;  // 赋值募集总金额
    }

    // —— investedAmount 字段的 getter/setter ——
    public BigDecimal getInvestedAmount() {  // 获取已募集金额
        return investedAmount;  // 返回已募集金额
    }

    public void setInvestedAmount(BigDecimal investedAmount) {  // 设置已募集金额
        this.investedAmount = investedAmount;  // 赋值已募集金额
    }

    /**
     * 获取剩余可投金额（计算字段）
     * <p>
     * 当 totalAmount 与 investedAmount 均不为空时，
     * 返回 totalAmount - investedAmount；任一为空则返回 null。
     * </p>
     *
     * @return 剩余可投金额，字段缺失时返回 null
     */
    public BigDecimal getRemainAmount() {  // 获取剩余可投金额(动态计算)
        if (totalAmount == null || investedAmount == null) {  // 卫语句:任一字段为 null 则无法计算
            return null;  // 字段缺失时返回 null,避免 NPE
        }
        return totalAmount.subtract(investedAmount);  // 使用 BigDecimal.subtract 精确计算差值
    }

    public void setRemainAmount(BigDecimal remainAmount) {  // 设置剩余可投金额(仅用于序列化框架兼容)
        // 计算字段，setter 保留以兼容序列化框架
        this.remainAmount = remainAmount;  // 赋值(实际值由 getter 计算,此 setter 主要供 Jackson 等框架反序列化使用)
    }

    // —— status 字段的 getter/setter ——
    public String getStatus() {  // 获取产品状态
        return status;  // 返回产品状态
    }

    public void setStatus(String status) {  // 设置产品状态
        this.status = status;  // 赋值产品状态
    }

    // —— description 字段的 getter/setter ——
    public String getDescription() {  // 获取产品描述
        return description;  // 返回产品描述
    }

    public void setDescription(String description) {  // 设置产品描述
        this.description = description;  // 赋值产品描述
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
