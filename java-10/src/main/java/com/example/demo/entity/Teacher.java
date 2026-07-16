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
 * 教师实体：记录教师基本信息
 * <p>
 * 为什么 teacherNo 设置唯一约束：工号是教师业务唯一标识，数据库层面保证不重复
 * 为什么年龄用 String 而非 int：前端传入可能是空字符串，统一用字符串避免类型转换异常
 * 为什么手写 getter/setter：项目未引入 Lombok，避免额外依赖
 * </p>
 */
@Entity
@Table(name = "teachers")
public class Teacher {

    /** 主键 ID，自增策略，数据库层面保证唯一性 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 教师姓名 */
    @Column(nullable = false, length = 20)
    private String name;

    /** 工号，全局唯一 */
    @Column(nullable = false, unique = true, length = 20)
    private String teacherNo;

    /** 性别 */
    @Column(length = 10)
    private String gender;

    /** 年龄（字符串存储，兼容前端空值传入） */
    @Column(length = 10)
    private String age;

    /** 联系电话，11 位手机号 */
    @Column(length = 20)
    private String phone;

    /** 创建时间：由 Hibernate 自动填充，不可更新 */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间：每次修改自动刷新 */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 无参构造：JPA 规范要求，Hibernate 实例化实体时调用 */
    public Teacher() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacherNo() {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
