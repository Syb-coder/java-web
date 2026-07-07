package com.example.java5.dto;

import com.example.java5.model.EducationLevel;
import com.example.java5.model.ExperienceLevel;
import com.example.java5.model.JobCategory;

import java.time.LocalDate;

/**
 * 招聘岗位创建/更新请求 DTO
 */
public record JobRequest(
        String title,
        String company,
        String city,
        JobCategory category,
        ExperienceLevel experience,
        EducationLevel education,
        Integer salaryMin,
        Integer salaryMax,
        String salaryDesc,
        String skills,
        String responsibilities,
        String requirements,
        String benefits,
        String companyDesc,
        LocalDate publishedDate
) {
}
