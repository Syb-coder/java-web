package com.example.java11.model;  // 模型层包，存放实体与枚举

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 敏感词实体
 * <p>
 * 对应 sensitive_words 表，存储需要过滤的敏感词库。
 * 用于发帖、评论等场景的内容审核与过滤。
 * </p>
 */
@Entity
@Table(name = "sensitive_words")
public class SensitiveWord {

    /** 主键 ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 敏感词（唯一） */
    @Column(nullable = false, unique = true, length = 100)
    private String word;

    /** 分类，如政治、色情、广告等 */
    @Column(length = 50)
    private String category;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后修改时间，由 @PreUpdate 自动维护 */
    private LocalDateTime updateTime;

    /**
     * JPA 要求的无参构造器
     */
    public SensitiveWord() {
    }

    /**
     * 业务构造器：新增敏感词时使用
     *
     * @param word 敏感词
     */
    public SensitiveWord(String word) {
        this.word = word;
        this.createdAt = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新前回调，自动刷新 updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

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
