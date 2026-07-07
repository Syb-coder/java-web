package com.example.java6.service;

import com.example.java6.dto.RegulationRequest;
import com.example.java6.dto.RegulationResponse;
import com.example.java6.model.Regulation;
import com.example.java6.model.RegulationCategory;
import com.example.java6.repository.RegulationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 政策法规业务服务
 *
 * <p>封装政策法规的查询、创建、更新、删除等业务逻辑。</p>
 */
@Service
public class RegulationService {

    private final RegulationRepository repository;

    public RegulationService(RegulationRepository repository) {
        this.repository = repository;
    }

    /**
     * 查询法规列表（可按分类过滤）
     *
     * @param category 分类，为 null 时返回全部
     * @return 法规响应列表
     */
    public List<RegulationResponse> list(RegulationCategory category) {
        List<Regulation> list = (category == null)
                ? repository.findAllByOrderByPublishDateDesc()
                : repository.findByCategoryOrderByPublishDateDesc(category);
        return list.stream().map(RegulationResponse::from).collect(Collectors.toList());
    }

    /**
     * 查询首页最新法规
     *
     * @return 最新 6 条法规
     */
    public List<RegulationResponse> latest() {
        return repository.findTop6ByOrderByPublishDateDesc().stream()
                .map(RegulationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 获取法规详情
     *
     * @param id 法规 ID
     * @return 法规响应，不存在返回 null
     */
    public RegulationResponse get(Long id) {
        return repository.findById(id).map(RegulationResponse::from).orElse(null);
    }

    /**
     * 创建法规
     *
     * @param req 创建请求
     * @return 创建后的法规响应
     */
    public RegulationResponse create(RegulationRequest req) {
        Regulation r = new Regulation();
        applyRequest(r, req);
        return RegulationResponse.from(repository.save(r));
    }

    /**
     * 更新法规
     *
     * @param id  法规 ID
     * @param req 更新请求
     * @return 更新后的法规响应，不存在返回 null
     */
    public RegulationResponse update(Long id, RegulationRequest req) {
        return repository.findById(id).map(r -> {
            applyRequest(r, req);
            return RegulationResponse.from(repository.save(r));
        }).orElse(null);
    }

    /**
     * 删除法规
     *
     * @param id 法规 ID
     * @return 是否删除成功
     */
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 将请求字段应用到实体
     *
     * @param r   法规实体
     * @param req 请求对象
     */
    private void applyRequest(Regulation r, RegulationRequest req) {
        r.setTitle(req.title());
        r.setIssuingAuthority(req.issuingAuthority());
        r.setCategory(req.category());
        r.setPublishDate(req.publishDate());
        r.setEffectiveDate(req.effectiveDate());
        r.setContent(req.content());
        r.setDocumentNumber(req.documentNumber());
    }
}
