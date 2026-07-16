package com.example.java11.service;  // 服务层包，存放业务逻辑

import com.example.java11.dto.SectionResponse;
import com.example.java11.model.Section;
import com.example.java11.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 板块服务
 * <p>
 * 负责讨论板块的创建、查询、更新、删除及版主分配。
 * 板块用于对帖子进行一级分类管理。
 * </p>
 */
@Service
public class SectionService {

    /** 板块数据访问层 */
    private final SectionRepository sectionRepository;

    /**
     * 构造器注入依赖
     *
     * @param sectionRepository 板块数据访问层
     */
    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    /**
     * 获取所有板块（按排序权重升序）
     *
     * @return 板块响应列表
     */
    public List<SectionResponse> getAllSections() {
        return sectionRepository.findAllByOrderBySortOrderAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取板块详情
     *
     * @param id 板块 ID
     * @return 板块响应 DTO
     */
    public SectionResponse getSection(Long id) {
        Optional<Section> optional = sectionRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("板块不存在");
        }
        return toResponse(optional.get());
    }

    /**
     * 创建板块
     *
     * @param name        板块名称
     * @param description 板块描述
     * @param icon        板块图标标识
     * @return 创建后的板块响应 DTO
     */
    @Transactional
    public SectionResponse createSection(String name, String description, String icon) {
        Section section = new Section(name, description);
        section.setIcon(icon);
        Section saved = sectionRepository.save(section);
        return toResponse(saved);
    }

    /**
     * 更新板块
     *
     * @param id          板块 ID
     * @param name        板块名称
     * @param description 板块描述
     * @param icon        板块图标标识
     * @return 更新后的板块响应 DTO
     */
    @Transactional
    public SectionResponse updateSection(Long id, String name, String description, String icon) {
        Optional<Section> optional = sectionRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("板块不存在");
        }
        Section section = optional.get();
        section.setName(name);
        section.setDescription(description);
        section.setIcon(icon);
        sectionRepository.save(section);
        return toResponse(section);
    }

    /**
     * 删除板块
     *
     * @param id 板块 ID
     */
    @Transactional
    public void deleteSection(Long id) {
        if (!sectionRepository.existsById(id)) {
            throw new RuntimeException("板块不存在");
        }
        sectionRepository.deleteById(id);
    }

    /**
     * 更新板块排序权重
     *
     * @param id        板块 ID
     * @param sortOrder 排序权重，数值越小越靠前
     */
    @Transactional
    public void updateSortOrder(Long id, Integer sortOrder) {
        Optional<Section> optional = sectionRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("板块不存在");
        }
        Section section = optional.get();
        section.setSortOrder(sortOrder);
        sectionRepository.save(section);
    }

    /**
     * 设置版主
     *
     * @param id          板块 ID
     * @param moderatorId 版主（管理员）ID
     */
    @Transactional
    public void setModerator(Long id, Long moderatorId) {
        Optional<Section> optional = sectionRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("板块不存在");
        }
        Section section = optional.get();
        section.setModeratorId(moderatorId);
        sectionRepository.save(section);
    }

    /**
     * 实体转 DTO
     *
     * @param section 板块实体
     * @return 板块响应 DTO，实体为 null 时返回 null
     */
    private SectionResponse toResponse(Section section) {
        if (section == null) {
            return null;
        }
        SectionResponse response = new SectionResponse();
        response.setId(section.getId());
        response.setName(section.getName());
        response.setDescription(section.getDescription());
        response.setIcon(section.getIcon());
        response.setSortOrder(section.getSortOrder());
        response.setPostCount(section.getPostCount());
        response.setModeratorId(section.getModeratorId());
        response.setCreatedAt(section.getCreatedAt());
        response.setUpdateTime(section.getUpdateTime());
        return response;
    }
}
