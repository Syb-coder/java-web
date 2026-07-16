package com.example.java11.repository;

import com.example.java11.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 通知消息数据访问层
 * <p>
 * 提供对 notifications 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 查询用户通知列表，按创建时间倒序
     *
     * @param userId 用户 ID
     * @return 通知列表
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 按已读状态筛选用户通知
     *
     * @param userId 用户 ID
     * @param isRead 是否已读
     * @return 通知列表
     */
    List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);

    /**
     * 统计用户未读通知数量
     *
     * @param userId 用户 ID
     * @return 未读通知数
     */
    long countByUserIdAndIsReadFalse(Long userId);
}
