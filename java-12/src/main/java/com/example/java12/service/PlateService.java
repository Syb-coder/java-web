package com.example.java12.service;  // 服务层包

import com.example.java12.dto.PlateRequest;  // 板块请求 DTO
import com.example.java12.dto.PlateResponse;  // 板块响应 DTO
import com.example.java12.model.OperateLog;  // 操作日志实体
import com.example.java12.model.Plate;  // 板块实体
import com.example.java12.model.User;  // 用户实体
import com.example.java12.repository.PlateRepository;  // 板块数据访问层
import com.example.java12.repository.PostRepository;  // 帖子数据访问层
import org.springframework.stereotype.Service;  // Service 注解
import org.springframework.transaction.annotation.Transactional;  // 事务注解

import java.util.List;  // 列表
import java.util.stream.Collectors;  // 流式收集

/**
 * 板块服务
 * <p>
 * 负责板块的增删改查。管理员可创建/编辑/删除板块，
 * 删除前需检查板块下是否有帖子。
 * </p>
 */
@Service
public class PlateService {

    /** 板块数据访问层 */
    private final PlateRepository plateRepository;

    /** 帖子数据访问层（删除板块时检查是否有帖子） */
    private final PostRepository postRepository;

    /** 操作日志服务 */
    private final OperateLogService operateLogService;

    /**
     * 构造器注入
     */
    public PlateService(PlateRepository plateRepository, PostRepository postRepository,
                        OperateLogService operateLogService) {
        this.plateRepository = plateRepository;
        this.postRepository = postRepository;
        this.operateLogService = operateLogService;
    }

    /**
     * 查询全部板块（按排序序号升序）
     *
     * @return 板块响应列表
     */
    public List<PlateResponse> findAll() {
        return plateRepository.findAllByOrderBySortOrderAscCreateTimeAsc()
                .stream()
                .map(PlateResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 按 ID 查询板块
     *
     * @param id 板块 ID
     * @return 板块实体
     * @throws RuntimeException 板块不存在时抛出
     */
    public Plate findById(Long id) {
        return plateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("板块不存在"));
    }

    /**
     * 创建板块（管理员操作）
     *
     * @param req   板块请求
     * @param admin 操作管理员
     * @return 新建板块响应
     */
    @Transactional
    public PlateResponse create(PlateRequest req, User admin, String ip) {
        // 校验名称唯一性
        if (plateRepository.findByName(req.getName()).isPresent()) {
            throw new RuntimeException("板块名称已存在");
        }
        Plate plate = new Plate(req.getName(), req.getDescription());
        if (req.getIcon() != null) {
            plate.setIcon(req.getIcon());
        }
        if (req.getSortOrder() != null) {
            plate.setSortOrder(req.getSortOrder());
        }
        plateRepository.save(plate);
        // 记录操作日志
        operateLogService.log(admin.getId(), admin.getNickname(), "创建板块",
                "板块:" + req.getName(), ip);
        return new PlateResponse(plate);
    }

    /**
     * 编辑板块（管理员操作）
     *
     * @param id    板块 ID
     * @param req   板块请求
     * @param admin 操作管理员
     * @return 更新后的板块响应
     */
    @Transactional
    public PlateResponse update(Long id, PlateRequest req, User admin, String ip) {
        Plate plate = findById(id);
        // 校验名称唯一性（排除自身）
        plateRepository.findByName(req.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("板块名称已存在");
            }
        });
        plate.setName(req.getName());
        plate.setDescription(req.getDescription());
        if (req.getIcon() != null) {
            plate.setIcon(req.getIcon());
        }
        if (req.getSortOrder() != null) {
            plate.setSortOrder(req.getSortOrder());
        }
        plateRepository.save(plate);
        operateLogService.log(admin.getId(), admin.getNickname(), "编辑板块",
                "板块ID:" + id, ip);
        return new PlateResponse(plate);
    }

    /**
     * 删除板块（管理员操作）
     * <p>
     * 如果板块下有帖子则拒绝删除，提示先迁移或删除帖子。
     * </p>
     *
     * @param id    板块 ID
     * @param admin 操作管理员
     */
    @Transactional
    public void delete(Long id, User admin, String ip) {
        Plate plate = findById(id);
        // 检查板块下是否有帖子
        long postCount = postRepository.count();
        if (plate.getPostCount() > 0) {
            throw new RuntimeException("该板块下存在帖子，请先迁移或删除");
        }
        plateRepository.delete(plate);
        operateLogService.log(admin.getId(), admin.getNickname(), "删除板块",
                "板块:" + plate.getName(), ip);
    }
}
