package com.example.java5.repository;

import com.example.java5.model.EducationLevel;
import com.example.java5.model.ExperienceLevel;
import com.example.java5.model.JobCategory;
import com.example.java5.model.JobPosting;
import com.example.java5.model.JobSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    @Query("""
        SELECT j FROM JobPosting j WHERE
          (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                              OR LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%'))
                              OR LOWER(j.skills) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:city IS NULL OR j.city = :city)
          AND (:category IS NULL OR j.category = :category)
          AND (:experience IS NULL OR j.experience = :experience)
          AND (:education IS NULL OR j.education = :education)
          AND (:salaryMin IS NULL OR j.salaryMax >= :salaryMin)
        ORDER BY j.publishedDate DESC, j.id DESC
        """)
    List<JobPosting> search(
            @Param("keyword") String keyword,
            @Param("city") String city,
            @Param("category") JobCategory category,
            @Param("experience") ExperienceLevel experience,
            @Param("education") EducationLevel education,
            @Param("salaryMin") Integer salaryMin
    );

    boolean existsBySourceAndExternalId(JobSource source, String externalId);
}
