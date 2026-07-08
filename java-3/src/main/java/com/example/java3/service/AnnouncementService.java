package com.example.java3.service;

import com.example.java3.dto.AnnouncementRequest;
import com.example.java3.dto.AnnouncementResponse;
import com.example.java3.model.Announcement;
import com.example.java3.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告服务
 */
@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    /**
     * 查询全部公告（前台展示）
     *
     * @return 公告响应列表
     */
    public List<AnnouncementResponse> findAll() {
        return announcementRepository.findAllByOrderByPinnedDescCreatedAtDesc()
                .stream()
                .map(AnnouncementResponse::new)
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
        Announcement a = new Announcement(req.getTitle(), req.getContent(),
                Boolean.TRUE.equals(req.getPinned()), adminId);
        announcementRepository.save(a);
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
        Announcement a = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("公告不存在"));
        a.setTitle(req.getTitle());
        a.setContent(req.getContent());
        a.setPinned(Boolean.TRUE.equals(req.getPinned()));
        announcementRepository.save(a);
        return new AnnouncementResponse(a);
    }

    /**
     * 删除公告
     *
     * @param id 公告 ID
     */
    public void delete(Long id) {
        announcementRepository.deleteById(id);
    }
}
