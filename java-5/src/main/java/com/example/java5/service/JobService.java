package com.example.java5.service;

import com.example.java5.dto.JobPostingResponse;
import com.example.java5.dto.JobRequest;
import com.example.java5.model.EducationLevel;
import com.example.java5.model.ExperienceLevel;
import com.example.java5.model.JobCategory;
import com.example.java5.model.JobPosting;
import com.example.java5.model.JobSource;
import com.example.java5.repository.JobPostingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 招聘岗位业务服务
 */
@Service
public class JobService {

    private final JobPostingRepository repository;

    public JobService(JobPostingRepository repository) {
        this.repository = repository;
    }

    public List<JobPostingResponse> search(String keyword, String city,
                                           JobCategory category, ExperienceLevel experience,
                                           EducationLevel education, Integer salaryMin) {
        return repository.search(keyword, city, category, experience, education, salaryMin)
                .stream()
                .map(JobPostingResponse::from)
                .collect(Collectors.toList());
    }

    public JobPostingResponse get(Long id) {
        return repository.findById(id)
                .map(JobPostingResponse::from)
                .orElse(null);
    }

    public JobPostingResponse create(JobRequest req) {
        JobPosting j = new JobPosting();
        applyRequest(j, req);
        if (j.getSource() == null) {
            j.setSource(JobSource.LOCAL);
        }
        if (j.getPublishedDate() == null) {
            j.setPublishedDate(LocalDate.now());
        }
        return JobPostingResponse.from(repository.save(j));
    }

    public JobPostingResponse update(Long id, JobRequest req) {
        return repository.findById(id).map(j -> {
            applyRequest(j, req);
            return JobPostingResponse.from(repository.save(j));
        }).orElse(null);
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public Map<String, Object> stats() {
        Map<String, Object> stats = new HashMap<>();
        List<JobPosting> all = repository.findAll();
        stats.put("total", all.size());

        Map<String, Long> byCategory = all.stream()
                .filter(j -> j.getCategory() != null)
                .collect(Collectors.groupingBy(j -> j.getCategory().getLabel(), Collectors.counting()));
        stats.put("byCategory", byCategory);

        Map<String, Long> byCity = all.stream()
                .filter(j -> j.getCity() != null && !j.getCity().isBlank())
                .collect(Collectors.groupingBy(JobPosting::getCity, Collectors.counting()));
        stats.put("byCity", byCity);

        Map<String, Long> bySource = all.stream()
                .filter(j -> j.getSource() != null)
                .collect(Collectors.groupingBy(j -> j.getSource().getLabel(), Collectors.counting()));
        stats.put("bySource", bySource);

        return stats;
    }

    private void applyRequest(JobPosting j, JobRequest req) {
        j.setTitle(req.title());
        j.setCompany(req.company());
        j.setCity(req.city());
        j.setCategory(req.category());
        j.setExperience(req.experience());
        j.setEducation(req.education());
        j.setSalaryMin(req.salaryMin());
        j.setSalaryMax(req.salaryMax());
        j.setSalaryDesc(req.salaryDesc());
        j.setSkills(req.skills());
        j.setResponsibilities(req.responsibilities());
        j.setRequirements(req.requirements());
        j.setBenefits(req.benefits());
        j.setCompanyDesc(req.companyDesc());
        j.setPublishedDate(req.publishedDate());
    }
}
