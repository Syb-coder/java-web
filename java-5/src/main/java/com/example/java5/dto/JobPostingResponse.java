package com.example.java5.dto;

import com.example.java5.model.JobPosting;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 招聘岗位响应 DTO
 */
public record JobPostingResponse(
        Long id,
        String title,
        String company,
        String city,
        String category,
        String categoryLabel,
        String experience,
        String experienceLabel,
        String education,
        String educationLabel,
        Integer salaryMin,
        Integer salaryMax,
        String salaryDesc,
        String skills,
        String responsibilities,
        String requirements,
        String benefits,
        String companyDesc,
        LocalDate publishedDate,
        String source,
        String sourceLabel,
        String externalUrl,
        LocalDateTime createdAt
) {

    public static JobPostingResponse from(JobPosting j) {
        return new JobPostingResponse(
                j.getId(),
                j.getTitle(),
                j.getCompany(),
                j.getCity(),
                j.getCategory() != null ? j.getCategory().name() : null,
                j.getCategory() != null ? j.getCategory().getLabel() : null,
                j.getExperience() != null ? j.getExperience().name() : null,
                j.getExperience() != null ? j.getExperience().getLabel() : null,
                j.getEducation() != null ? j.getEducation().name() : null,
                j.getEducation() != null ? j.getEducation().getLabel() : null,
                j.getSalaryMin(),
                j.getSalaryMax(),
                j.getSalaryDesc(),
                j.getSkills(),
                j.getResponsibilities(),
                j.getRequirements(),
                j.getBenefits(),
                j.getCompanyDesc(),
                j.getPublishedDate(),
                j.getSource() != null ? j.getSource().name() : null,
                j.getSource() != null ? j.getSource().getLabel() : null,
                j.getExternalUrl(),
                j.getCreatedAt()
        );
    }
}
