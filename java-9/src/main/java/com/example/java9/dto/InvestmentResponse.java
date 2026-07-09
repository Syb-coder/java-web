package com.example.java9.dto;  // 声明该 DTO 类所属的包路径

import java.math.BigDecimal;  // 导入高精度十进制类,用于金额表示
import java.time.LocalDateTime;  // 导入日期时间类

/**
 * 投资订单响应 DTO
 * <p>
 * 返回投资订单的详情，包含订单号、产品信息、预期收益等。
 * </p>
 */
public class InvestmentResponse {  // 投资订单响应 DTO 类定义

    /** 订单 ID */
    private Long id;  // 订单唯一标识(主键)

    /** 订单号（业务唯一） */
    private String orderNo;  // 业务订单号(对用户可见,唯一)

    /** 投资用户 ID */
    private Long userId;  // 发起申购的用户 ID

    /** 理财产品 ID */
    private Long productId;  // 关联的理财产品 ID

    /** 理财产品名称（冗余，便于展示） */
    private String productName;  // 产品名称(冗余字段,避免再次查询产品表)

    /** 申购金额（元） */
    private BigDecimal amount;  // 申购金额(元)

    /** 预期收益（元，按年化收益与期限计算） */
    private BigDecimal expectedReturn;  // 预期收益(元),按 年化收益率×金额×期限/365 估算

    /** 订单状态：PENDING/SUCCESS/FAILED/SETTLED */
    private String status;  // 订单状态:PENDING(处理中)/SUCCESS(成功)/FAILED(失败)/SETTLED(已结算)

    /** 订单创建时间 */
    private LocalDateTime createdAt;  // 订单创建时间戳

    // —— id 字段的 getter/setter ——
    public Long getId() {  // 获取订单 ID
        return id;  // 返回订单 ID
    }

    public void setId(Long id) {  // 设置订单 ID
        this.id = id;  // 赋值订单 ID
    }

    // —— orderNo 字段的 getter/setter ——
    public String getOrderNo() {  // 获取订单号
        return orderNo;  // 返回订单号
    }

    public void setOrderNo(String orderNo) {  // 设置订单号
        this.orderNo = orderNo;  // 赋值订单号
    }

    // —— userId 字段的 getter/setter ——
    public Long getUserId() {  // 获取用户 ID
        return userId;  // 返回用户 ID
    }

    public void setUserId(Long userId) {  // 设置用户 ID
        this.userId = userId;  // 赋值用户 ID
    }

    // —— productId 字段的 getter/setter ——
    public Long getProductId() {  // 获取产品 ID
        return productId;  // 返回产品 ID
    }

    public void setProductId(Long productId) {  // 设置产品 ID
        this.productId = productId;  // 赋值产品 ID
    }

    // —— productName 字段的 getter/setter ——
    public String getProductName() {  // 获取产品名称
        return productName;  // 返回产品名称
    }

    public void setProductName(String productName) {  // 设置产品名称
        this.productName = productName;  // 赋值产品名称
    }

    // —— amount 字段的 getter/setter ——
    public BigDecimal getAmount() {  // 获取申购金额
        return amount;  // 返回申购金额
    }

    public void setAmount(BigDecimal amount) {  // 设置申购金额
        this.amount = amount;  // 赋值申购金额
    }

    // —— expectedReturn 字段的 getter/setter ——
    public BigDecimal getExpectedReturn() {  // 获取预期收益
        return expectedReturn;  // 返回预期收益
    }

    public void setExpectedReturn(BigDecimal expectedReturn) {  // 设置预期收益
        this.expectedReturn = expectedReturn;  // 赋值预期收益
    }

    // —— status 字段的 getter/setter ——
    public String getStatus() {  // 获取订单状态
        return status;  // 返回订单状态
    }

    public void setStatus(String status) {  // 设置订单状态
        this.status = status;  // 赋值订单状态
    }

    // —— createdAt 字段的 getter/setter ——
    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;  // 返回创建时间
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;  // 赋值创建时间
    }
}
