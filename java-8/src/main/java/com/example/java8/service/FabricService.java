package com.example.java8.service;

import com.example.java8.dto.FabricRequest;
import com.example.java8.dto.FabricResponse;
import com.example.java8.model.Fabric;
import com.example.java8.repository.FabricRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 面料业务服务
 * <p>提供面料的增删改查及筛选能力。</p>
 */
@Service
public class FabricService {

    private final FabricRepository repository;

    public FabricService(FabricRepository repository) {
        this.repository = repository;
    }

    /**
     * 查询面料列表（支持按材质、颜色、价格上限筛选）
     *
     * @param material 材质（可空）
     * @param color    颜色（可空）
     * @param maxPrice 单价上限（可空）
     * @return 面料响应列表
     */
    public List<FabricResponse> list(String material, String color, Double maxPrice) {
        List<Fabric> all = repository.findAll();
        return all.stream()
                .filter(f -> material == null || material.isEmpty() || material.equals(f.getMaterial()))
                .filter(f -> color == null || color.isEmpty() || color.equals(f.getColor()))
                .filter(f -> maxPrice == null || f.getUnitPrice() <= maxPrice)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据 ID 获取面料
     *
     * @param id 面料 ID
     * @return 面料实体（不存在返回 null）
     */
    public Fabric getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * 新增面料
     *
     * @param req 面料请求
     * @return 新建后的面料响应
     */
    public FabricResponse create(FabricRequest req) {
        Fabric fabric = new Fabric(req.name(), req.material(), req.color(),
                req.unitPrice(), req.stock() != null ? req.stock() : 0,
                req.description());
        return toResponse(repository.save(fabric));
    }

    /**
     * 更新面料
     *
     * @param id  面料 ID
     * @param req 面料请求
     * @return 更新后的面料响应（不存在返回 null）
     */
    public FabricResponse update(Long id, FabricRequest req) {
        Fabric fabric = repository.findById(id).orElse(null);
        if (fabric == null) return null;
        fabric.setName(req.name());
        fabric.setMaterial(req.material());
        fabric.setColor(req.color());
        fabric.setUnitPrice(req.unitPrice());
        if (req.stock() != null) fabric.setStock(req.stock());
        fabric.setDescription(req.description());
        return toResponse(repository.save(fabric));
    }

    /**
     * 删除面料
     *
     * @param id 面料 ID
     */
    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * 统计面料总数
     *
     * @return 数量
     */
    public long count() {
        return repository.count();
    }

    /**
     * 实体转响应 DTO
     *
     * @param f 面料实体
     * @return 响应 DTO
     */
    public FabricResponse toResponse(Fabric f) {
        return new FabricResponse(f.getId(), f.getName(), f.getMaterial(), f.getColor(),
                f.getUnitPrice(), f.getStock(), f.getDescription(), f.getCreateTime());
    }
}
