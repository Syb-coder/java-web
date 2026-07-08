// 声明当前类所在包路径
package com.example.java3.service;

// 导入公告请求 DTO
import com.example.java3.dto.AnnouncementRequest;
// 导入公告响应 DTO
import com.example.java3.dto.AnnouncementResponse;
// 导入公告实体模型
import com.example.java3.model.Announcement;
// 导入公告仓储接口（Spring Data JPA）
import com.example.java3.repository.AnnouncementRepository;
// 导入 @Service 注解
import org.springframework.stereotype.Service;

// 导入 List 集合
import java.util.List;

/**
 * 公告服务
 */
// 标识为业务层组件，由 Spring 容器管理为单例
@Service
public class AnnouncementService {

    // 公告仓储，处理公告表 CRUD
    private final AnnouncementRepository announcementRepository;

    // 构造方法注入公告仓储 Bean
    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    /**
     * 查询全部公告（前台展示）
     *
     * @return 公告响应列表
     */
    public List<AnnouncementResponse> findAll() {
        // 查询全部公告：置顶在前，同级别按创建时间降序
        return announcementRepository.findAllByOrderByPinnedDescCreatedAtDesc()
                // 转 Stream
                .stream()
                // 用构造方法引用将每个实体转为响应 DTO
                .map(AnnouncementResponse::new)
                // 收集为 List
                .toList();
    }

    /**
     * 发布公告
     *
     * @param adminId 管理员 ID
     * @param req     公告请求
     * @return 公告响应
     */
    public AnnouncementResponse create(Long adminId, AnnouncementRequest req) {
        // 构造公告实体，处理 pinned 字段为 null 的情况（默认非置顶）
        Announcement a = new Announcement(req.getTitle(), req.getContent(),
                Boolean.TRUE.equals(req.getPinned()), adminId);
        // 持久化到数据库
        announcementRepository.save(a);
        // 返回响应 DTO
        return new AnnouncementResponse(a);
    }

    /**
     * 更新公告
     *
     * @param id  公告 ID
     * @param req 公告请求
     * @return 公告响应
     */
    public AnnouncementResponse update(Long id, AnnouncementRequest req) {
        // 根据 ID 查询公告
        Announcement a = announcementRepository.findById(id)
                // 公告不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("公告不存在"));
        // 更新标题
        a.setTitle(req.getTitle());
        // 更新内容
        a.setContent(req.getContent());
        // 更新置顶标志，处理 null 情况
        a.setPinned(Boolean.TRUE.equals(req.getPinned()));
        // 持久化
        announcementRepository.save(a);
        // 返回响应 DTO
        return new AnnouncementResponse(a);
    }

    /**
     * 删除公告
     *
     * @param id 公告 ID
     */
    public void delete(Long id) {
        // 调用 JPA 默认方法按 ID 删除公告
        announcementRepository.deleteById(id);
    }
}
