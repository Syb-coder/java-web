package com.example.java5.service;

import com.example.java5.model.JobCategory;
import com.example.java5.model.JobPosting;
import com.example.java5.model.JobSource;
import com.example.java5.repository.JobPostingRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * V2EX 酷工作节点 API 同步服务
 * <p>
 * 接口：https://www.v2ex.com/api/topics/show.json?node_name=jobs
 * V2EX 主题标题常见格式：[城市] 公司 招聘 岗位 薪资 xx-xxK
 */
@Service
public class V2exJobSyncService {

    private static final Logger log = LoggerFactory.getLogger(V2exJobSyncService.class);

    private static final String V2EX_JOBS_API =
            "https://www.v2ex.com/api/topics/show.json?node_name=jobs";

    private static final Pattern CITY_PATTERN = Pattern.compile("\\[(.+?)]");
    private static final Pattern SALARY_PATTERN = Pattern.compile("(\\d+)-(\\d+)\\s*K", Pattern.CASE_INSENSITIVE);

    private final RestClient restClient;
    private final JobPostingRepository repository;

    public V2exJobSyncService(JobPostingRepository repository) {
        this.repository = repository;
        this.restClient = RestClient.builder()
                .baseUrl("https://www.v2ex.com")
                .defaultHeader("User-Agent", "java5-job-board/1.0")
                .build();
    }

    /**
     * 拉取 V2EX 酷工作节点最新主题，去重后入库
     *
     * @return 新增的岗位数量
     */
    public int syncLatest() {
        V2exTopic[] topics;
        try {
            topics = restClient.get()
                    .uri("/api/topics/show.json?node_name=jobs")
                    .retrieve()
                    .body(V2exTopic[].class);
        } catch (Exception e) {
            log.warn("调用 V2EX API 失败: {}", e.getMessage());
            return 0;
        }
        if (topics == null || topics.length == 0) {
            return 0;
        }

        int inserted = 0;
        List<JobPosting> toSave = new ArrayList<>();
        for (V2exTopic t : topics) {
            if (t.id() <= 0) {
                continue;
            }
            String externalId = String.valueOf(t.id());
            if (repository.existsBySourceAndExternalId(JobSource.V2EX, externalId)) {
                continue;
            }
            JobPosting j = convert(t);
            if (j != null) {
                toSave.add(j);
                inserted++;
            }
        }
        if (!toSave.isEmpty()) {
            repository.saveAll(toSave);
        }
        log.info("V2EX 同步完成：共 {} 条主题，新增 {}", topics.length, inserted);
        return inserted;
    }

    private JobPosting convert(V2exTopic t) {
        JobPosting j = new JobPosting();
        j.setSource(JobSource.V2EX);
        j.setExternalId(String.valueOf(t.id()));
        j.setExternalUrl(t.url());

        String title = t.title();
        if (title == null || title.isBlank()) {
            return null;
        }
        j.setTitle(title.trim());

        // 城市：[xxx] 提取
        Matcher cityMatcher = CITY_PATTERN.matcher(title);
        if (cityMatcher.find()) {
            j.setCity(cityMatcher.group(1).trim());
        }

        // 薪资：xx-xxK
        Matcher salaryMatcher = SALARY_PATTERN.matcher(title);
        if (salaryMatcher.find()) {
            try {
                int min = Integer.parseInt(salaryMatcher.group(1));
                int max = Integer.parseInt(salaryMatcher.group(2));
                j.setSalaryMin(min * 1000);
                j.setSalaryMax(max * 1000);
                j.setSalaryDesc(min + "-" + max + "K");
            } catch (NumberFormatException ignored) {
            }
        }

        // 公司：标题里没有结构化公司名，暂用作者用户名占位
        if (t.member() != null && t.member().username() != null) {
            j.setCompany(t.member().username());
        }

        // 分类：按关键词
        j.setCategory(detectCategory(title));

        // 详情正文
        if (t.content() != null && !t.content().isBlank()) {
            j.setResponsibilities(t.content());
        }

        // 发布日期
        if (t.created() > 0) {
            j.setPublishedDate(LocalDate.ofInstant(
                    Instant.ofEpochSecond(t.created()),
                    ZoneId.of("Asia/Shanghai")));
        } else {
            j.setPublishedDate(LocalDate.now());
        }

        return j;
    }

    private JobCategory detectCategory(String title) {
        String lower = title.toLowerCase();
        if (lower.contains("算法") || lower.contains("ai") || lower.contains("machine learning")) {
            return JobCategory.ALGORITHM;
        }
        if (lower.contains("数据") || lower.contains("data") || lower.contains("etl") || lower.contains("数仓")) {
            return JobCategory.DATA;
        }
        if (lower.contains("devops") || lower.contains("运维") || lower.contains("sre") || lower.contains("infra")) {
            return JobCategory.DEVOPS;
        }
        if (lower.contains("测试") || lower.contains("qa") || lower.contains("质量")) {
            return JobCategory.QA;
        }
        if (lower.contains("设计") || lower.contains("design") || lower.contains("ui") || lower.contains("ux")) {
            return JobCategory.DESIGN;
        }
        if (lower.contains("产品") || lower.contains("product") || lower.contains("pm")) {
            return JobCategory.PRODUCT;
        }
        if (lower.contains("运营") || lower.contains("operations") || lower.contains("运营")) {
            return JobCategory.OPERATIONS;
        }
        return JobCategory.DEVELOPMENT;
    }

    /**
     * V2EX 主题响应（仅保留需要的字段）
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record V2exTopic(
            long id,
            String title,
            String content,
            String url,
            @JsonProperty("created") long created,
            @JsonProperty("last_modified") long lastModified,
            V2exMember member
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record V2exMember(String username) {
    }
}
