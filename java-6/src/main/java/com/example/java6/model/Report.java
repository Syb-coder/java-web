package com.example.java6.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 举报记录实体
 *
 * <p>承载公众提交的违法和不良信息举报记录，包括举报类型、
 * 举报对象、举报描述、举报人信息以及处置状态等字段。</p>
 */
@Entity
@Table(name = "report")
public class Report {

    /** 主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 举报类型 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportType reportType;

    /** 举报对象链接 */
    @Column(length = 500)
    private String targetUrl;

    /** 举报描述 */
    @Column(nullable = false, length = 2000)
    private String description;

    /** 举报人姓名 */
    @Column(length = 50)
    private String reporterName;

    /** 举报人联系方式 */
    @Column(length = 100)
    private String reporterContact;

    /** 处理状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status = ReportStatus.PENDING;

    /** 处置备注 */
    @Column(length = 1000)
    private String handleNote;

    /** 举报提交时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /** 处置时间 */
    private LocalDateTime handledAt;

    public Report() {
    }

    public Report(ReportType reportType, String targetUrl, String description,
                  String reporterName, String reporterContact) {
        this.reportType = reportType;
        this.targetUrl = targetUrl;
        this.description = description;
        this.reporterName = reporterName;
        this.reporterContact = reporterContact;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterContact() {
        return reporterContact;
    }

    public void setReporterContact(String reporterContact) {
        this.reporterContact = reporterContact;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getHandleNote() {
        return handleNote;
    }

    public void setHandleNote(String handleNote) {
        this.handleNote = handleNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getHandledAt() {
        return handledAt;
    }

    public void setHandledAt(LocalDateTime handledAt) {
        this.handledAt = handledAt;
    }
}
