package com.example.java5.controller;

import com.example.java5.dto.JobPostingResponse;
import com.example.java5.dto.JobRequest;
import com.example.java5.model.EducationLevel;
import com.example.java5.model.ExperienceLevel;
import com.example.java5.model.JobCategory;
import com.example.java5.service.JobService;
import com.example.java5.service.V2exJobSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 招聘岗位 REST 控制器
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final V2exJobSyncService v2exJobSyncService;

    public JobController(JobService jobService, V2exJobSyncService v2exJobSyncService) {
        this.jobService = jobService;
        this.v2exJobSyncService = v2exJobSyncService;
    }

    @GetMapping
    public List<JobPostingResponse> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) JobCategory category,
            @RequestParam(required = false) ExperienceLevel experience,
            @RequestParam(required = false) EducationLevel education,
            @RequestParam(required = false) Integer salaryMin) {
        return jobService.search(keyword, city, category, experience, education, salaryMin);
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return jobService.stats();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostingResponse> get(@PathVariable Long id) {
        JobPostingResponse resp = jobService.get(id);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public JobPostingResponse create(@RequestBody JobRequest req) {
        return jobService.create(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPostingResponse> update(@PathVariable Long id, @RequestBody JobRequest req) {
        JobPostingResponse resp = jobService.update(id, req);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (jobService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/sync-v2ex")
    public Map<String, Integer> syncV2ex() {
        int inserted = v2exJobSyncService.syncLatest();
        return Map.of("inserted", inserted);
    }
}
