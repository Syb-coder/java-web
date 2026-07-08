package com.example.java3.repository;

import com.example.java3.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 公告仓储
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    /**
     * 查询全部公告，置顶优先，再按时间倒序
     *
     * @return 公告列表
     */
    List<Announcement> findAllByOrderByPinnedDescCreatedAtDesc();
}
