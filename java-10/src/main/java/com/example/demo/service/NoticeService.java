package com.example.demo.service;

import com.example.demo.entity.Notice;
import com.example.demo.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告业务逻辑层
 * <p>
 * 职责：封装公告相关 CRUD 业务操作。
 * 为什么直接返回 Notice 实体：公告信息无敏感字段，无需 DTO 转换。
 * 为什么用 @Service：标记为 Spring Bean，由容器管理生命周期，便于事务控制和注入。
 * </p>
 */
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 构造器注入依赖
     *
     * @param noticeRepository 公告数据访问层
     */
    @Autowired
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    /**
     * 查询所有公告
     *
     * @return 公告列表
     */
    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }

    /**
     * 创建公告
     *
     * @param notice 公告信息
     * @return 创建后的公告信息（含自增 ID）
     */
    public Notice create(Notice notice) {
        return noticeRepository.save(notice);
    }

    /**
     * 更新公告信息
     * <p>
     * 仅更新非空字段，实现部分更新语义，避免误覆盖为空值。
     * </p>
     *
     * @param id 公告 ID
     * @param notice 更新数据
     * @return 更新后的公告信息，公告不存在时返回 null
     */
    public Notice update(Long id, Notice notice) {
        Notice existing = noticeRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (notice.getTitle() != null) {
            existing.setTitle(notice.getTitle());
        }
        if (notice.getAuthor() != null) {
            existing.setAuthor(notice.getAuthor());
        }
        if (notice.getDate() != null) {
            existing.setDate(notice.getDate());
        }
        return noticeRepository.save(existing);
    }

    /**
     * 删除公告
     *
     * @param id 公告 ID
     * @return 删除成功返回 true，公告不存在返回 false
     */
    public boolean delete(Long id) {
        if (!noticeRepository.existsById(id)) {
            return false;
        }
        noticeRepository.deleteById(id);
        return true;
    }
}
