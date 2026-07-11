package com.example.java9.model;  // 实体类所在包，归属于 model 层

// JPA 持久化相关注解导入
import jakarta.persistence.Column;  // 字段列映射注解
import jakarta.persistence.Entity;  // 实体标识注解
import jakarta.persistence.EnumType;  // 枚举映射类型
import jakarta.persistence.Enumerated;  // 枚举存储方式注解
import jakarta.persistence.GeneratedValue;  // 主键生成策略注解
import jakarta.persistence.GenerationType;  // 主键生成策略枚举
import jakarta.persistence.Id;  // 主键标识注解
import jakarta.persistence.PreUpdate;  // 更新前回调注解
import jakarta.persistence.Table;  // 表名映射注解

// JDK 通用类型导入
import java.time.LocalDateTime;  // 时间戳类型

/**
 * 营销活动实体
 * <p>
 * 对应 activities 表，平台营销活动配置。
 * 运营管理员创建活动，C端用户可参与领取权益。
 * </p>
 */
@Entity  // JPA 实体标识
@Table(name = "activities")  // 映射到 activities 表
public class Activity {  // 营销活动实体，承载活动配置与生效周期

    /** 主键 ID，自增 */
    @Id  // JPA 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略
    private Long id;  // 主键 ID，自增长

    /** 活动标题 */
    @Column(nullable = false, length = 100)  // 非空，长度100
    private String title;  // 活动标题，C 端展示

    /** 活动类型：COUPON/INTEREST_RATE/SIGN_IN */
    @Enumerated(EnumType.STRING)  // 枚举按字符串存储
    @Column(nullable = false, length = 20)  // 非空，长度20
    private ActivityType type;  // 活动类型，决定权益发放逻辑

    /** 活动描述 */
    @Column(length = 500)  // 长度500
    private String description;  // 活动描述，C 端详情展示

    /** 活动开始时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime startTime;  // 活动开始时间，决定权益生效起点

    /** 活动结束时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime endTime;  // 活动结束时间，决定权益失效终点

    /** 活动状态：true 进行中，false 已结束 */
    @Column(nullable = false)  // 非空
    private Boolean active = true;  // 活动开关，运营手动下线或到期自动置 false

    /** 创建时间 */
    @Column(nullable = false)  // 非空
    private LocalDateTime createdAt;  // 创建时间戳

    /** 最后修改时间 */
    private LocalDateTime updateTime;  // 最后修改时间戳

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public Activity() {  // JPA 无参构造器
    }

    public Activity(String title, ActivityType type, String description,
                    LocalDateTime startTime, LocalDateTime endTime) {  // 活动创建构造器
        this.title = title;  // 赋值活动标题
        this.type = type;  // 赋值活动类型
        this.description = description;  // 赋值活动描述
        this.startTime = startTime;  // 赋值开始时间
        this.endTime = endTime;  // 赋值结束时间
        this.active = true;  // 新活动默认进行中
        this.createdAt = LocalDateTime.now();  // 服务端生成创建时间
        this.updateTime = LocalDateTime.now();  // 初始化修改时间
    }

    public Long getId() {  // 获取主键 ID
        return id;
    }

    public void setId(Long id) {  // 设置主键 ID
        this.id = id;
    }

    public String getTitle() {  // 获取活动标题
        return title;
    }

    public void setTitle(String title) {  // 设置活动标题
        this.title = title;
    }

    public ActivityType getType() {  // 获取活动类型
        return type;
    }

    public void setType(ActivityType type) {  // 设置活动类型
        this.type = type;
    }

    public String getDescription() {  // 获取活动描述
        return description;
    }

    public void setDescription(String description) {  // 设置活动描述
        this.description = description;
    }

    public LocalDateTime getStartTime() {  // 获取活动开始时间
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {  // 设置活动开始时间
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {  // 获取活动结束时间
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {  // 设置活动结束时间
        this.endTime = endTime;
    }

    public Boolean getActive() {  // 获取活动状态
        return active;
    }

    public void setActive(Boolean active) {  // 设置活动状态
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {  // 获取创建时间
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {  // 设置创建时间
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
