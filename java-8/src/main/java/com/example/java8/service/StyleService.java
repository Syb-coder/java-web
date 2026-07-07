package com.example.java8.service;

import com.example.java8.dto.StyleRequest;
import com.example.java8.dto.StyleResponse;
import com.example.java8.model.Style;
import com.example.java8.repository.StyleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 款式业务服务
 */
@Service
public class StyleService {

    private final StyleRepository repository;

    public StyleService(StyleRepository repository) {
        this.repository = repository;
    }

    /**
     * 查询款式列表（支持按类别与关键词筛选）
     *
     * @param category 类别（可空）
     * @param keyword  名称关键词（可空）
     * @return 款式响应列表
     */
    public List<StyleResponse> list(String category, String keyword) {
        List<Style> all = repository.findAll();
        return all.stream()
                .filter(s -> category == null || category.isEmpty() || category.equals(s.getCategory()))
                .filter(s -> keyword == null || keyword.isEmpty()
                        || (s.getName() != null && s.getName().contains(keyword)))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据 ID 获取款式
     *
     * @param id 款式 ID
     * @return 款式实体（不存在返回 null）
     */
    public Style getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * 新增款式
     *
     * @param req 款式请求
     * @return 新建后的款式响应
     */
    public StyleResponse create(StyleRequest req) {
        Style style = new Style(req.name(), req.category(), req.craftFee(), req.description());
        return toResponse(repository.save(style));
    }

    /**
     * 更新款式
     *
     * @param id  款式 ID
     * @param req 款式请求
     * @return 更新后的款式响应（不存在返回 null）
     */
    public StyleResponse update(Long id, StyleRequest req) {
        Style style = repository.findById(id).orElse(null);
        if (style == null) return null;
        style.setName(req.name());
        style.setCategory(req.category());
        style.setCraftFee(req.craftFee());
        style.setDescription(req.description());
        return toResponse(repository.save(style));
    }

    /**
     * 删除款式
     *
     * @param id 款式 ID
     */
    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * 统计款式总数
     *
     * @return 数量
     */
    public long count() {
        return repository.count();
    }

    /**
     * 实体转响应 DTO
     *
     * @param s 款式实体
     * @return 响应 DTO
     */
    public StyleResponse toResponse(Style s) {
        return new StyleResponse(s.getId(), s.getName(), s.getCategory(),
                s.getCraftFee(), s.getDescription(), s.getCreateTime());
    }
}
