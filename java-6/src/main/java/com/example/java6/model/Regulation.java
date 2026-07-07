package com.example.java6.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * 政策法规实体
 *
 * <p>承载国家网络安全相关法律法规数据，按效力层级分类，
 * 包括法律、行政法规、部门规章、规范性文件四类。</p>
 */
@Entity
@Table(name = "regulation")
public class Regulation {

    /** 主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 法规标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 颁布机构 */
    @Column(nullable = false, length = 100)
    private String issuingAuthority;

    /** 法规分类 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RegulationCategory category;

    /** 发布日期 */
    @Column(nullable = false)
    private LocalDate publishDate;

    /** 生效日期 */
    private LocalDate effectiveDate;

    /** 法规正文内容 */
    @Column(nullable = false, length = 8000)
    private String content;

    /** 法规文号 */
    @Column(length = 100)
    private String documentNumber;

    public Regulation() {
    }

    public Regulation(String title, String issuingAuthority, RegulationCategory category,
                      LocalDate publishDate, String content) {
        this.title = title;
        this.issuingAuthority = issuingAuthority;
        this.category = category;
        this.publishDate = publishDate;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public RegulationCategory getCategory() {
        return category;
    }

    public void setCategory(RegulationCategory category) {
        this.category = category;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
}
