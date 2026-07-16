package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 考勤实体：记录学生课程考勤信息
 * <p>
 * 为什么用 courseId 而非外键关联：简化查询，避免联表性能损耗，业务层自行关联
 * 为什么用 studentNo 而非 studentId：学号是业务标识，便于跨表追溯
 * 为什么 date 用 String：前端传入日期格式不固定，统一用字符串存储
 * 为什么手写 getter/setter：项目未引入 Lombok，避免额外依赖
 * </p>
 */
@Entity
@Table(name = "attendances")
public class Attendance {

    /** 主键 ID，自增策略，数据库层面保证唯一性 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 学号，关联 Student 表的业务标识 */
    @Column(nullable = false, length = 20)
    private String studentNo;

    /** 课程 ID，关联 Course 表 */
    @Column(name = "course_id")
    private Long courseId;

    /** 考勤日期（字符串存储，兼容多种日期格式） */
    @Column(length = 20)
    private String date;

    /** 考勤状态：出勤/迟到/旷课等 */
    @Column(length = 20)
    private String status;

    /** 创建时间：由 Hibernate 自动填充，不可更新 */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间：每次修改自动刷新 */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 无参构造：JPA 规范要求，Hibernate 实例化实体时调用 */
    public Attendance() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
