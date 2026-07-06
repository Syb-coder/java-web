package com.example.java3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

/**
 * 抽签记录实体（用户每次求签的记录）
 */
@Entity
@Table(name = "draw_records")
public class DrawRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联的签 id */
    private Long fortuneId;

    /** 签号（冗余，便于展示） */
    private Integer fortuneNumber;

    /** 签等级（冗余） */
    @Enumerated(EnumType.STRING)
    private FortuneLevel level;

    /** 求签时所问之事 */
    private String question;

    private LocalDateTime drawnAt;

    public DrawRecord() {
    }

    @PrePersist
    void onCreate() {
        if (this.drawnAt == null) {
            this.drawnAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFortuneId() {
        return fortuneId;
    }

    public void setFortuneId(Long fortuneId) {
        this.fortuneId = fortuneId;
    }

    public Integer getFortuneNumber() {
        return fortuneNumber;
    }

    public void setFortuneNumber(Integer fortuneNumber) {
        this.fortuneNumber = fortuneNumber;
    }

    public FortuneLevel getLevel() {
        return level;
    }

    public void setLevel(FortuneLevel level) {
        this.level = level;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public LocalDateTime getDrawnAt() {
        return drawnAt;
    }

    public void setDrawnAt(LocalDateTime drawnAt) {
        this.drawnAt = drawnAt;
    }
}
