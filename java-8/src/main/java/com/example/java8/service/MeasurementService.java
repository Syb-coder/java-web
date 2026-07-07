package com.example.java8.service;

import com.example.java8.dto.MeasurementRequest;
import com.example.java8.dto.MeasurementResponse;
import com.example.java8.model.Measurement;
import com.example.java8.model.User;
import com.example.java8.repository.MeasurementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 量体数据业务服务
 * <p>每个用户的量体数据完全隔离，仅能查询/管理本人的数据。</p>
 */
@Service
public class MeasurementService {

    private final MeasurementRepository repository;

    public MeasurementService(MeasurementRepository repository) {
        this.repository = repository;
    }

    /**
     * 查询指定用户的全部量体数据
     *
     * @param userId 用户 ID
     * @return 量体数据响应列表
     */
    public List<MeasurementResponse> listByUser(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据 ID 获取量体数据
     *
     * @param id 量体数据 ID
     * @return 量体数据实体（不存在返回 null）
     */
    public Measurement getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * 新增量体数据
     *
     * @param user 所属用户
     * @param req  量体请求
     * @return 新建后的量体响应
     */
    public MeasurementResponse create(User user, MeasurementRequest req) {
        Measurement m = new Measurement();
        applyToEntity(m, req);
        m.setUser(user);
        m.setCreateTime(LocalDateTime.now());
        return toResponse(repository.save(m));
    }

    /**
     * 更新量体数据
     *
     * @param id        量体数据 ID
     * @param req       量体请求
     * @param currentUserId 当前登录用户 ID（用于权限校验）
     * @return 更新后的响应（不存在或越权返回 null）
     */
    public MeasurementResponse update(Long id, MeasurementRequest req, Long currentUserId) {
        Measurement m = repository.findById(id).orElse(null);
        if (m == null) return null;
        // 权限校验：仅本人可编辑自己的量体数据
        if (!m.getUser().getId().equals(currentUserId)) return null;
        applyToEntity(m, req);
        return toResponse(repository.save(m));
    }

    /**
     * 删除量体数据
     *
     * @param id            量体数据 ID
     * @param currentUserId 当前登录用户 ID
     * @return true 删除成功，false 不存在或越权
     */
    public boolean delete(Long id, Long currentUserId) {
        Measurement m = repository.findById(id).orElse(null);
        if (m == null) return false;
        if (!m.getUser().getId().equals(currentUserId)) return false;
        repository.deleteById(id);
        return true;
    }

    /**
     * 将请求 DTO 字段应用到实体
     *
     * @param m   目标实体
     * @param req 请求 DTO
     */
    private void applyToEntity(Measurement m, MeasurementRequest req) {
        m.setName(req.name());
        m.setHeight(req.height());
        m.setWeight(req.weight());
        m.setNeckCircumference(req.neckCircumference());
        m.setShoulderWidth(req.shoulderWidth());
        m.setChestCircumference(req.chestCircumference());
        m.setWaistCircumference(req.waistCircumference());
        m.setHipCircumference(req.hipCircumference());
        m.setClothesLength(req.clothesLength());
        m.setSleeveLength(req.sleeveLength());
        m.setPantsLength(req.pantsLength());
        m.setThighCircumference(req.thighCircumference());
        m.setRemark(req.remark());
    }

    /**
     * 实体转响应 DTO
     *
     * @param m 实体
     * @return 响应 DTO
     */
    public MeasurementResponse toResponse(Measurement m) {
        return new MeasurementResponse(m.getId(), m.getName(), m.getHeight(), m.getWeight(),
                m.getNeckCircumference(), m.getShoulderWidth(), m.getChestCircumference(),
                m.getWaistCircumference(), m.getHipCircumference(), m.getClothesLength(),
                m.getSleeveLength(), m.getPantsLength(), m.getThighCircumference(),
                m.getRemark(), m.getCreateTime());
    }
}
