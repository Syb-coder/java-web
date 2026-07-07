package com.example.java5.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 招聘岗位实体
 */
@Entity
@Table(name = "job_postings")
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String company;
    private String city;

    @Enumerated(EnumType.STRING)
    private JobCategory category;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experience;

    @Enumerated(EnumType.STRING)
    private EducationLevel education;

    private Integer salaryMin;
    private Integer salaryMax;
    private String salaryDesc;
    private String skills;
    private String responsibilities;
    private String requirements;
    private String benefits;
    private String companyDesc;
    private LocalDate publishedDate;

    @Enumerated(EnumType.STRING)
    private JobSource source;

    private String externalId;
    private String externalUrl;

    private LocalDateTime createdAt;

    public JobPosting() {
    }

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public JobCategory getCategory() { return category; }
    public void setCategory(JobCategory category) { this.category = category; }
    public ExperienceLevel getExperience() { return experience; }
    public void setExperience(ExperienceLevel experience) { this.experience = experience; }
    public EducationLevel getEducation() { return education; }
    public void setEducation(EducationLevel education) { this.education = education; }
    public Integer getSalaryMin() { return salaryMin; }
    public void setSalaryMin(Integer salaryMin) { this.salaryMin = salaryMin; }
    public Integer getSalaryMax() { return salaryMax; }
    public void setSalaryMax(Integer salaryMax) { this.salaryMax = salaryMax; }
    public String getSalaryDesc() { return salaryDesc; }
    public void setSalaryDesc(String salaryDesc) { this.salaryDesc = salaryDesc; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getResponsibilities() { return responsibilities; }
    public void setResponsibilities(String responsibilities) { this.responsibilities = responsibilities; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }
    public String getCompanyDesc() { return companyDesc; }
    public void setCompanyDesc(String companyDesc) { this.companyDesc = companyDesc; }
    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }
    public JobSource getSource() { return source; }
    public void setSource(JobSource source) { this.source = source; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public String getExternalUrl() { return externalUrl; }
    public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
