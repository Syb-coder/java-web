// 声明当前类所在的包路径，归类为 model（数据模型）层
package com.example.java3.model;

// 导入 JPA（Jakarta Persistence API）全部注解，包含 @Entity、@Table、@Id 等，用于将 POJO 映射为数据库实体
import jakarta.persistence.*;

// 导入 LocalDateTime 时间类型，用于记录反馈提交时间
import java.time.LocalDateTime;

/**
 * 意见反馈实体
 * <p>
 * 学生用户可向平台提交意见反馈，管理员可查看处理。
 * </p>
 */
// 标识当前类为 JPA 实体，Hibernate 会将其映射为数据库表
@Entity
// 指定表名为 feedbacks，承载用户反馈与管理员回复
@Table(name = "feedbacks")
public class Feedback {

    /** 主键，自增 */
    // 声明该字段为数据库主键
    @Id
    // 主键生成策略：IDENTITY 表示由数据库自增分配
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 提交者用户 ID */
    // 非空；关联 User 表主键
    @Column(nullable = false)
    private Long userId;

    /** 反馈标题 */
    // 非空，长度上限 100；反馈核心展示字段
    @Column(nullable = false, length = 100)
    private String title;

    /** 反馈内容 */
    // 非空，长度上限 1000；限定长度避免长文本影响存储与展示
    @Column(nullable = false, length = 1000)
    private String content;

    /** 处理状态：false 未处理，true 已处理 */
    // 非空；用于管理员后台筛选未处理反馈
    @Column(nullable = false)
    private Boolean handled;

    /** 管理员回复（可选） */
    // 可空，长度上限 500；管理员处理反馈后填写回复内容
    @Column(length = 500)
    private String reply;

    /** 提交时间 */
    // 非空，反馈提交时写入
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 默认构造方法 */
    // JPA 规范要求实体必须提供无参构造方法，便于通过反射实例化
    public Feedback() {
    }

    /**
     * 业务构造方法
     *
     * @param userId  提交者 ID
     * @param title   标题
     * @param content 内容
     */
    public Feedback(Long userId, String title, String content) {
        // 设置提交者用户 ID
        this.userId = userId;
        // 设置反馈标题
        this.title = title;
        // 设置反馈内容
        this.content = content;
        // 默认未处理：新提交的反馈等待管理员处理
        this.handled = false;
        // 提交时间取当前系统时间
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

    // 获取提交者用户 ID
    public Long getUserId() {
        return userId;
    }

    // 设置提交者用户 ID
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // 获取反馈标题
    public String getTitle() {
        return title;
    }

    // 设置反馈标题
    public void setTitle(String title) {
        this.title = title;
    }

    // 获取反馈内容
    public String getContent() {
        return content;
    }

    // 设置反馈内容
    public void setContent(String content) {
        this.content = content;
    }

    // 获取处理状态标识
    public Boolean getHandled() {
        return handled;
    }

    // 设置处理状态标识（管理员处理完毕后置为 true）
    public void setHandled(Boolean handled) {
        this.handled = handled;
    }

    // 获取管理员回复内容
    public String getReply() {
        return reply;
    }

    // 设置管理员回复内容
    public void setReply(String reply) {
        this.reply = reply;
    }

    // 获取反馈提交时间
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 设置反馈提交时间
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
