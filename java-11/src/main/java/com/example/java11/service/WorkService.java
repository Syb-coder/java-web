package com.example.java11.service;  // 服务层包，存放业务逻辑

import com.example.java11.dto.WorkResponse;
import com.example.java11.model.Work;
import com.example.java11.model.WorkType;
import com.example.java11.repository.WorkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 作品服务
 * <p>
 * 负责二次元作品库的管理，包括动画、漫画、游戏、轻小说等作品的
 * 创建、查询、搜索与高分推荐。
 * </p>
 */
@Service
public class WorkService {

    /** 作品数据访问层 */
    private final WorkRepository workRepository;

    /**
     * 构造器注入依赖
     *
     * @param workRepository 作品数据访问层
     */
    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    /**
     * 获取所有作品
     *
     * @return 作品响应列表
     */
    public List<WorkResponse> getAllWorks() {
        return workRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 按类型筛选作品
     *
     * @param type 作品类型名称
     * @return 匹配类型的作品列表
     */
    public List<WorkResponse> getWorksByType(String type) {
        WorkType workType = parseType(type);
        return workRepository.findByType(workType).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取高分作品（评分前 10）
     *
     * @return 高分作品列表
     */
    public List<WorkResponse> getHotWorks() {
        return workRepository.findTop10ByOrderByRatingDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取作品详情
     *
     * @param id 作品 ID
     * @return 作品响应 DTO
     */
    public WorkResponse getWork(Long id) {
        Optional<Work> optional = workRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("作品不存在");
        }
        return toResponse(optional.get());
    }

    /**
     * 搜索作品
     *
     * @param keyword 搜索关键词
     * @return 匹配的作品列表
     */
    public List<WorkResponse> searchWorks(String keyword) {
        return workRepository.findByTitleContaining(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 创建作品
     *
     * @param title       作品名
     * @param description 作品简介
     * @param coverImage  封面图 URL
     * @param type        作品类型
     * @param rating      评分（0-10）
     * @param tags        标签（逗号分隔）
     * @return 创建后的作品响应 DTO
     */
    @Transactional
    public WorkResponse createWork(String title, String description, String coverImage, String type, Double rating, String tags) {
        WorkType workType = parseType(type);
        Work work = new Work(title, workType, description);
        work.setCoverImage(coverImage);
        work.setRating(rating);
        work.setTags(tags);
        Work saved = workRepository.save(work);
        return toResponse(saved);
    }

    /**
     * 更新作品
     *
     * @param id          作品 ID
     * @param title       作品名
     * @param description 作品简介
     * @param coverImage  封面图 URL
     * @param type        作品类型
     * @param rating      评分（0-10）
     * @param tags        标签（逗号分隔）
     */
    @Transactional
    public void updateWork(Long id, String title, String description, String coverImage, String type, Double rating, String tags) {
        Optional<Work> optional = workRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("作品不存在");
        }
        Work work = optional.get();
        work.setTitle(title);
        work.setDescription(description);
        work.setCoverImage(coverImage);
        work.setType(parseType(type));
        work.setRating(rating);
        work.setTags(tags);
        workRepository.save(work);
    }

    /**
     * 删除作品
     *
     * @param id 作品 ID
     */
    @Transactional
    public void deleteWork(Long id) {
        if (!workRepository.existsById(id)) {
            throw new RuntimeException("作品不存在");
        }
        workRepository.deleteById(id);
    }

    /**
     * 解析作品类型字符串为枚举
     *
     * @param type 类型字符串
     * @return 作品类型枚举
     */
    private WorkType parseType(String type) {
        try {
            return WorkType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的作品类型: " + type);
        }
    }

    /**
     * 实体转 DTO
     *
     * @param work 作品实体
     * @return 作品响应 DTO，实体为 null 时返回 null
     */
    private WorkResponse toResponse(Work work) {
        if (work == null) {
            return null;
        }
        WorkResponse response = new WorkResponse();
        response.setId(work.getId());
        response.setTitle(work.getTitle());
        response.setCoverImage(work.getCoverImage());
        response.setDescription(work.getDescription());
        response.setType(work.getType() != null ? work.getType().name() : null);
        response.setRating(work.getRating());
        response.setTags(work.getTags());
        response.setCreatedAt(work.getCreatedAt());
        response.setUpdateTime(work.getUpdateTime());
        return response;
    }
}
