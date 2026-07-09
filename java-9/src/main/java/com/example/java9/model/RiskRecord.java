package com.example.java9.model;  // 实体类所在包，归属于 model 层

// JPA 持久化相关注解导入
import jakarta.persistence.Column;  // 字段列映射注解
import jakarta.persistence.Entity;  // 实体标识注解
import jakarta.persistence.EnumType;  // 枚举映射类型
import jakarta.persistence.Enumerated;  // 枚举存储方式注解
import jakarta.persistence.GeneratedValue;  // 主键生成策略注解
import jakarta.persistence.GenerationType;  // 主键生成策略枚举
import jakarta.persistence.Id;  // 主键标识注解
import jakarta.persistence.Table;  // 表名映射注解

// JDK 通用类型导入
import java.time.LocalDateTime;  // 时间戳类型

/**
 * 风控记录实体
 * <p>
 * 对应 risk_records 表，风控规则引擎识别的异常行为记录。
 * 风控专员根据 riskLevel 进行处置，HIGH 级别会自动冻结相关账户。
 * </p>
 */
@Entity  // JPA 实体标识
@Table(name = "risk_records")  // 映射到 risk_records 表
public class RiskRecord {  // 风控记录实体，承载风险识别结果与处置流程

    /** 主键 ID，自增 */
    @Id  // JPA 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略
    private Long id;  // 主键 ID，自增长

    /** 风控目标类型：USER/MERCHANT/ORDER */
    @Column(nullable = false, length = 10)  // 非空，长度10
    private String targetType;  // 风控目标类型，区分用户/商户/订单

    /** 风控目标 ID */
    @Column(nullable = false)  // 非空
    private Long targetId;  // 风控目标 ID，关联对应表

    /** 风险类型描述（如 LARGE_AMOUNT、HIGH_FREQUENCY、ANOMALY_LOCATION） */
    @Column(nullable = false, length = 30)  // 非空，长度30
    private String riskType;  // 风险类型，规则引擎输出的命中规则标识

    /** 风险等级：LOW/MEDIUM/HIGH */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储
    @Column(nullable = false, length = 10)  // 非空，长度10
    private RiskLevel riskLevel;  // 风险等级，决定处置优先级

    /** 风险描述 */
    @Column(length = 500)  // 长度500
    private String description;  // 风险描述，风控专员处置参考

    /** 关联订单号（可为空） */
    @Column(length = 32)  // 长度32
    private String relatedOrderNo;  // 关联订单号，便于追溯触发风险的订单

    /** 处理状态：PENDING/HANDLED/IGNORED */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储
    @Column(nullable = false, length = 10)  // 非空，长度10
    private RiskStatus status = RiskStatus.PENDING;  // 处理状态，默认待处理

    /** 处理人（管理员用户名） */
    @Column(length = 50)  // 长度50
    private String handledBy;  // 处理人，风控专员用户名，用于审计

    /** 处理意见 */
    @Column(length = 500)  // 长度500
    private String handleRemark;  // 处理意见，记录处置决策依据

    /** 处理时间 */
    private LocalDateTime handledAt;  // 处理时间，HANDLED 状态时填写，默认空

    /** 创建时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime createdAt;  // 创建时间戳，风控识别时间

    public RiskRecord() {  // JPA 无参构造器
    }

    public RiskRecord(String targetType, Long targetId, String riskType, RiskLevel riskLevel,
                      String description, String relatedOrderNo) {  // 风险识别构造器
        this.targetType = targetType;  // 赋值目标类型
        this.targetId = targetId;  // 赋值目标 ID
        this.riskType = riskType;  // 赋值风险类型
        this.riskLevel = riskLevel;  // 赋值风险等级
        this.description = description;  // 赋值风险描述
        this.relatedOrderNo = relatedOrderNo;  // 赋值关联订单号
        this.status = RiskStatus.PENDING;  // 新记录默认待处理
        this.createdAt = LocalDateTime.now();  // 服务端生成识别时间
    }

    public Long getId() {  // 获取主键 ID
        return id;
    }

    public void setId(Long id) {  // 设置主键 ID
        this.id = id;
    }

    public String getTargetType() {  // 获取目标类型
        return targetType;
    }

    public void setTargetType(String targetType) {  // 设置目标类型
        this.targetType = targetType;
    }

    public Long getTargetId() {  // 获取目标 ID
        return targetId;
    }

    public void setTargetId(Long targetId) {  // 设置目标 ID
        this.targetId = targetId;
    }

    public String getRiskType() {  // 获取风险类型
        return riskType;
    }

    public void setRiskType(String riskType) {  // 设置风险类型
        this.riskType = riskType;
    }

    public RiskLevel getRiskLevel() {  // 获取风险等级
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {  // 设置风险等级
        this.riskLevel = riskLevel;
    }

    public String getDescription() {  // 获取风险描述
        return description;
    }

    public void setDescription(String description) {  // 设置风险描述
        this.description = description;
    }

    public String getRelatedOrderNo() {  // 获取关联订单号
        return relatedOrderNo;
    }

    public void setRelatedOrderNo(String relatedOrderNo) {  // 设置关联订单号
        this.relatedOrderNo = relatedOrderNo;
    }

    public RiskStatus getStatus() {  // 获取处理状态
        return status;
    }

    public void setStatus(RiskStatus status) {  // 设置处理状态
        this.status = status;
    }

    public String getHandledBy() {  // 获取处理人
        return handledBy;
    }

    public void setHandledBy(String handledBy) {  // 设置处理人
        this.handledBy = handledBy;
    }

    public String getHandleRemark() {  // 获取处理意见
        return handleRemark;
    }

    public void setHandleRemark(String handleRemark) {  // 设置处理意见
        this.handleRemark = handleRemark;
    }

    public LocalDateTime getHandledAt() {  // 获取处理时间
        return handledAt;
    }

    public void setHandledAt(LocalDateTime handledAt) {  // 设置处理时间
        this.handledAt = handledAt;
    }

    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;
    }
}
