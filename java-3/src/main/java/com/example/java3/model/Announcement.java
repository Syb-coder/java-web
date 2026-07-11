// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

// 导入 JPA（Jakarta Persistence API）全部注解，包含 @Entity、@Table、@Id 等，用于将 POJO 映射为数据库实体
import jakarta.persistence.*;

// 导入 LocalDateTime 时间类型，用于记录公告发布时间
import java.time.LocalDateTime;

/**
 * 校园交易公告实体
 * <p>
 * 管理员发布平台公告，如闲置集市活动通知、规则更新等。
 * </p>
 */
// 标识当前类为 JPA 实体，Hibernate 会将其映射为数据库表
@Entity
// 指定表名为 announcements，承载平台公告内容
@Table(name = "announcements")
public class Announcement {

    /** 主键，自增 */
    // 声明该字段为数据库主键
    @Id
    // 主键生成策略：IDENTITY 表示由数据库自增分配
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 公告标题 */
    // 非空，长度上限 100；公告核心展示字段
    @Column(nullable = false, length = 100)
    private String title;

    /** 公告内容 */
    // 非空，长度上限 2000；放大的目的是承载详细公告说明
    @Column(nullable = false, length = 2000)
    private String content;

    /** 是否置顶 */
    // 非空；置顶公告在前台列表中优先展示
    @Column(nullable = false)
    private Boolean pinned;

    /** 发布管理员 ID */
    // 非空；关联 AdminUser 表主键，标识公告发布者
    @Column(nullable = false)
    private Long adminId;

    /** 发布时间 */
    // 非空，公告发布时写入
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，数据更新前由 @PreUpdate 自动填充 */
    private LocalDateTime updateTime;

    /** 每次更新前自动填充修改时间 */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /** 默认构造方法 */
    // JPA 规范要求实体必须提供无参构造方法，便于通过反射实例化
    public Announcement() {
    }

    /**
     * 业务构造方法
     *
     * @param title   标题
     * @param content 内容
     * @param pinned  是否置顶
     * @param adminId 管理员 ID
     */
    public Announcement(String title, String content, Boolean pinned, Long adminId) {
        // 设置公告标题
        this.title = title;
        // 设置公告内容
        this.content = content;
        // 设置是否置顶
        this.pinned = pinned;
        // 设置发布管理员 ID
        this.adminId = adminId;
        // 发布时间取当前系统时间
        this.createdAt = LocalDateTime.now();
    }

    // ===== Getter / Setter =====

    // 获取主键 ID
    public Long getId() {
        return id;
    }

    // 设置主键 ID，通常由 JPA 自动填充
    public void setId(Long id) {
        this.id = id;
    }

    // 获取公告标题
    public String getTitle() {
        return title;
    }

    // 设置公告标题
    public void setTitle(String title) {
        this.title = title;
    }

    // 获取公告内容
    public String getContent() {
        return content;
    }

    // 设置公告内容
    public void setContent(String content) {
        this.content = content;
    }

    // 获取是否置顶标识
    public Boolean getPinned() {
        return pinned;
    }

    // 设置是否置顶标识
    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    // 获取发布管理员 ID
    public Long getAdminId() {
        return adminId;
    }

    // 设置发布管理员 ID
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    // 获取公告发布时间
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 设置公告发布时间
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
