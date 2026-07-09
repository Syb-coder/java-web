package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import java.math.BigDecimal;  // 导入高精度十进制类,用于金额汇总表示

/**
 * 统计响应 DTO
 * <p>
 * 返回平台运营统计数据，用于运营/管理后台首页概览展示。
 * </p>
 */
public class StatsResponse {  // 统计响应 DTO 类定义

    /** 用户总数 */
    private Long userCount;  // 平台注册用户总数

    /** 商户总数 */
    private Long merchantCount;  // 平台入驻商户总数

    /** 理财产品总数 */
    private Long productCount;  // 上架理财产品总数

    /** 投资订单总数 */
    private Long investmentCount;  // 投资订单总笔数

    /** 支付订单总数 */
    private Long paymentCount;  // 支付订单总笔数

    /** 待处理风控记录数 */
    private Long riskPendingCount;  // 待处理(status=PENDING)的风控记录数量

    /** 待处理工单数 */
    private Long ticketOpenCount;  // 待处理(status=OPEN)的工单数量

    /** 投资总金额（元） */
    private BigDecimal totalInvestmentAmount;  // 投资总金额(元),所有投资订单金额之和

    /** 支付总金额（元） */
    private BigDecimal totalPaymentAmount;  // 支付总金额(元),所有支付订单金额之和

    /**
     * 全参构造器，便于 StatsService 一次性组装统计结果
     *
     * @param userCount             用户总数
     * @param merchantCount         商户总数
     * @param productCount          理财产品总数
     * @param investmentCount       投资订单总数
     * @param paymentCount          支付订单总数
     * @param riskPendingCount      待处理风控记录数
     * @param ticketOpenCount       待处理工单数
     * @param totalInvestmentAmount 投资总金额
     * @param totalPaymentAmount    支付总金额
     */
    public StatsResponse(Long userCount, Long merchantCount, Long productCount, Long investmentCount,  // 全参构造函数,接收 9 项统计指标
                         Long paymentCount, Long riskPendingCount, Long ticketOpenCount,
                         BigDecimal totalInvestmentAmount, BigDecimal totalPaymentAmount) {
        this.userCount = userCount;  // 初始化用户总数
        this.merchantCount = merchantCount;  // 初始化商户总数
        this.productCount = productCount;  // 初始化产品总数
        this.investmentCount = investmentCount;  // 初始化投资订单总数
        this.paymentCount = paymentCount;  // 初始化支付订单总数
        this.riskPendingCount = riskPendingCount;  // 初始化待处理风控数
        this.ticketOpenCount = ticketOpenCount;  // 初始化待处理工单数
        this.totalInvestmentAmount = totalInvestmentAmount;  // 初始化投资总金额
        this.totalPaymentAmount = totalPaymentAmount;  // 初始化支付总金额
    }

    // —— userCount 字段的 getter/setter ——
    public Long getUserCount() {  // 获取用户总数
        return userCount;  // 返回用户总数
    }

    public void setUserCount(Long userCount) {  // 设置用户总数
        this.userCount = userCount;  // 赋值用户总数
    }

    // —— merchantCount 字段的 getter/setter ——
    public Long getMerchantCount() {  // 获取商户总数
        return merchantCount;  // 返回商户总数
    }

    public void setMerchantCount(Long merchantCount) {  // 设置商户总数
        this.merchantCount = merchantCount;  // 赋值商户总数
    }

    // —— productCount 字段的 getter/setter ——
    public Long getProductCount() {  // 获取产品总数
        return productCount;  // 返回产品总数
    }

    public void setProductCount(Long productCount) {  // 设置产品总数
        this.productCount = productCount;  // 赋值产品总数
    }

    // —— investmentCount 字段的 getter/setter ——
    public Long getInvestmentCount() {  // 获取投资订单总数
        return investmentCount;  // 返回投资订单总数
    }

    public void setInvestmentCount(Long investmentCount) {  // 设置投资订单总数
        this.investmentCount = investmentCount;  // 赋值投资订单总数
    }

    // —— paymentCount 字段的 getter/setter ——
    public Long getPaymentCount() {  // 获取支付订单总数
        return paymentCount;  // 返回支付订单总数
    }

    public void setPaymentCount(Long paymentCount) {  // 设置支付订单总数
        this.paymentCount = paymentCount;  // 赋值支付订单总数
    }

    // —— riskPendingCount 字段的 getter/setter ——
    public Long getRiskPendingCount() {  // 获取待处理风控数
        return riskPendingCount;  // 返回待处理风控记录数
    }

    public void setRiskPendingCount(Long riskPendingCount) {  // 设置待处理风控数
        this.riskPendingCount = riskPendingCount;  // 赋值待处理风控记录数
    }

    // —— ticketOpenCount 字段的 getter/setter ——
    public Long getTicketOpenCount() {  // 获取待处理工单数
        return ticketOpenCount;  // 返回待处理工单数
    }

    public void setTicketOpenCount(Long ticketOpenCount) {  // 设置待处理工单数
        this.ticketOpenCount = ticketOpenCount;  // 赋值待处理工单数
    }

    // —— totalInvestmentAmount 字段的 getter/setter ——
    public BigDecimal getTotalInvestmentAmount() {  // 获取投资总金额
        return totalInvestmentAmount;  // 返回投资总金额
    }

    public void setTotalInvestmentAmount(BigDecimal totalInvestmentAmount) {  // 设置投资总金额
        this.totalInvestmentAmount = totalInvestmentAmount;  // 赋值投资总金额
    }

    // —— totalPaymentAmount 字段的 getter/setter ——
    public BigDecimal getTotalPaymentAmount() {  // 获取支付总金额
        return totalPaymentAmount;  // 返回支付总金额
    }

    public void setTotalPaymentAmount(BigDecimal totalPaymentAmount) {  // 设置支付总金额
        this.totalPaymentAmount = totalPaymentAmount;  // 赋值支付总金额
    }
}
