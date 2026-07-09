package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import jakarta.validation.constraints.NotBlank;  // 导入非空字符串校验注解
import jakarta.validation.constraints.NotNull;  // 导入非 null 校验注解

import java.math.BigDecimal;  // 导入高精度十进制类,用于金额与收益率表示

/**
 * 理财产品创建请求 DTO
 * <p>
 * 运营人员创建理财产品时提交，
 * 支持 DEPOSIT（存款）/FUND（基金）/INSURANCE（保险）三类。
 * </p>
 */
public class ProductRequest {  // 理财产品创建请求 DTO 类定义

    /** 产品名称 */
    @NotBlank(message = "产品名称不能为空")  // 非空字符串校验:产品名称必填
    private String name;  // 产品名称

    /** 产品类型：DEPOSIT/FUND/INSURANCE */
    @NotBlank(message = "产品类型不能为空")  // 非空字符串校验:产品类型必填
    private String type;  // 产品类型:DEPOSIT(存款)/FUND(基金)/INSURANCE(保险)

    /** 年化收益率（百分比，如 5.0 表示 5%） */
    @NotNull(message = "年化收益率不能为空")  // 非 null 校验:收益率不能为 null
    private BigDecimal annualRate;  // 年化收益率(百分比,5.0 表示 5%)

    /** 起投金额（元） */
    @NotNull(message = "起投金额不能为空")  // 非 null 校验:起投金额必填
    private BigDecimal minAmount;  // 起投金额(元),低于此金额不可购买

    /** 产品期限（天） */
    @NotNull(message = "产品期限不能为空")  // 非 null 校验:期限必填
    private Integer duration;  // 产品期限(天),使用 Integer 包装类型支持 null

    /** 风险等级（1-5，1 为最低） */
    @NotNull(message = "风险等级不能为空")  // 非 null 校验:风险等级必填
    private Integer riskLevel;  // 风险等级(1~5,1 为最低风险)

    /** 募集总金额（元） */
    @NotNull(message = "募集总金额不能为空")  // 非 null 校验:募集总金额必填
    private BigDecimal totalAmount;  // 募集总金额(元),达到即停止募集

    /** 产品描述（可选） */
    private String description;  // 产品描述(选填),介绍产品特点

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

    // —— description 字段的 getter/setter ——
    public String getDescription() {  // 获取产品描述
        return description;  // 返回产品描述
    }

    public void setDescription(String description) {  // 设置产品描述
        this.description = description;  // 赋值产品描述
    }
}
