package com.example.java3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 签文库实体（每条记录是一支签的定义）
 */
@Entity
@Table(name = "fortunes")
public class Fortune {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 签号（第几签） */
    private Integer number;

    /** 四字吉语标题 */
    private String title;

    /** 签文诗句 */
    private String poem;

    /** 解签 */
    private String interpretation;

    @Enumerated(EnumType.STRING)
    private FortuneLevel level;

    public Fortune() {
    }

    public Fortune(Integer number, String title, String poem, String interpretation, FortuneLevel level) {
        this.number = number;
        this.title = title;
        this.poem = poem;
        this.interpretation = interpretation;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoem() {
        return poem;
    }

    public void setPoem(String poem) {
        this.poem = poem;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }

    public FortuneLevel getLevel() {
        return level;
    }

    public void setLevel(FortuneLevel level) {
        this.level = level;
    }
}
