package com.example.java6.controller;

import com.example.java6.dto.RegulationRequest;
import com.example.java6.dto.RegulationResponse;
import com.example.java6.model.RegulationCategory;
import com.example.java6.service.RegulationService;
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

/**
 * 政策法规 REST 控制器
 *
 * <p>提供政策法规的查询、详情、增删改接口。</p>
 */
@RestController
@RequestMapping("/api/regulations")
public class RegulationController {

    private final RegulationService regulationService;

    public RegulationController(RegulationService regulationService) {
        this.regulationService = regulationService;
    }

    /**
     * 查询法规列表（可按分类过滤）
     *
     * @param category 分类（可选）
     * @return 法规列表
     */
    @GetMapping
    public List<RegulationResponse> list(@RequestParam(required = false) RegulationCategory category) {
        return regulationService.list(category);
    }

    /**
     * 查询最新法规（首页用）
     *
     * @return 最新法规列表
     */
    @GetMapping("/latest")
    public List<RegulationResponse> latest() {
        return regulationService.latest();
    }

    /**
     * 获取法规详情
     *
     * @param id 法规 ID
     * @return 法规详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegulationResponse> get(@PathVariable Long id) {
        RegulationResponse resp = regulationService.get(id);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    /**
     * 创建法规
     *
     * @param req 创建请求
     * @return 创建后的法规
     */
    @PostMapping
    public RegulationResponse create(@RequestBody RegulationRequest req) {
        return regulationService.create(req);
    }

    /**
     * 更新法规
     *
     * @param id  法规 ID
     * @param req 更新请求
     * @return 更新后的法规
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegulationResponse> update(@PathVariable Long id, @RequestBody RegulationRequest req) {
        RegulationResponse resp = regulationService.update(id, req);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    /**
     * 删除法规
     *
     * @param id 法规 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (regulationService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
