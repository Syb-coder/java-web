package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 操作记录实体：记录用户在系统中的关键操作行为
 * <p>
 * 为什么只有 createdAt 没有 updatedAt：操作记录一经生成即不可修改，
 * 属于审计日志性质，保证记录的真实性和不可篡改性。
 * 为什么 time 用 String：与项目中其他实体（如 Notice.date）保持一致，
 * 便于前端直接展示，避免时区转换问题。
 * 为什么手写 getter/setter：项目未引入 Lombok，避免额外依赖
 * </p>
 */
@Entity
@Table(name = "operation_logs")
public class OperationLog {

    /** 主键 ID，自增策略，数据库层面保证唯一性 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 操作动作：如"登录系统"、"新增学生"，长度 100 */
    @Column(nullable = false, length = 100)
    private String action;

    /** 操作详情：补充描述操作内容，长度 500 */
    @Column(length = 500)
    private String detail;

    /** 操作时间（字符串存储，格式 yyyy-MM-dd HH:mm:ss） */
    @Column(length = 50)
    private String time;

    /** 创建时间：由 Hibernate 自动填充，不可更新，保证日志不可篡改 */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 无参构造：JPA 规范要求，Hibernate 实例化实体时调用 */
    public OperationLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
