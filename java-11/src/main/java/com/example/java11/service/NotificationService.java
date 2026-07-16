package com.example.java11.service;  // 服务层包，存放业务逻辑

import com.example.java11.dto.NotificationResponse;
import com.example.java11.model.Notification;
import com.example.java11.model.NotificationType;
import com.example.java11.model.User;
import com.example.java11.repository.NotificationRepository;
import com.example.java11.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 通知服务
 * <p>
 * 负责站内通知消息的发送、查询与已读状态管理。
 * 其他服务（如 CommentService、InteractionService）通过调用 sendNotification 方法
 * 向用户推送回复、点赞、系统公告等通知。
 * </p>
 */
@Service
public class NotificationService {

    /** 通知数据访问层 */
    private final NotificationRepository notificationRepository;

    /** 用户数据访问层，用于查询发送者信息 */
    private final UserRepository userRepository;

    /**
     * 构造器注入依赖
     *
     * @param notificationRepository 通知数据访问层
     * @param userRepository         用户数据访问层
     */
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * 获取用户通知列表
     *
     * @param userId 用户 ID
     * @return 通知响应列表，按创建时间倒序
     */
    public List<NotificationResponse> getNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户未读通知数
     *
     * @param userId 用户 ID
     * @return 未读通知数量
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * 标记单条通知为已读
     *
     * @param notificationId 通知 ID
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Optional<Notification> optional = notificationRepository.findById(notificationId);
        if (optional.isEmpty()) {
            throw new RuntimeException("通知不存在");
        }
        Notification notification = optional.get();
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    /**
     * 将用户所有未读通知标记为已读
     *
     * @param userId 用户 ID
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadList = notificationRepository.findByUserIdAndIsRead(userId, false);
        for (Notification notification : unreadList) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(unreadList);
    }

    /**
     * 发送通知（内部调用）
     * <p>
     * 由其他服务调用，向指定用户推送通知消息。
     * 如果通知类型无效或用户不存在，将抛出运行时异常。
     * </p>
     *
     * @param userId    接收通知的用户 ID
     * @param type      通知类型（REPLY/LIKE/MENTION/SYSTEM/REPORT）
     * @param content   通知内容
     * @param relatedId 关联 ID（如帖子 ID、评论 ID），可为 null
     * @param senderId  发送者 ID，系统通知为 null
     */
    @Transactional
    public void sendNotification(Long userId, String type, String content, Long relatedId, Long senderId) {
        NotificationType notificationType;
        try {
            notificationType = NotificationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的通知类型: " + type);
        }
        Notification notification = new Notification(userId, notificationType, content, senderId);
        notification.setRelatedId(relatedId);
        notificationRepository.save(notification);
    }

    /**
     * 实体转 DTO
     *
     * @param notification 通知实体
     * @return 通知响应 DTO，实体为 null 时返回 null
     */
    private NotificationResponse toResponse(Notification notification) {
        if (notification == null) {
            return null;
        }
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setType(notification.getType() != null ? notification.getType().name() : null);
        response.setContent(notification.getContent());
        response.setRelatedId(notification.getRelatedId());
        response.setSenderId(notification.getSenderId());
        response.setIsRead(notification.getIsRead());
        response.setCreatedAt(notification.getCreatedAt());

        // 查询发送者用户名，系统通知发送者为 null
        if (notification.getSenderId() != null) {
            Optional<User> sender = userRepository.findById(notification.getSenderId());
            sender.ifPresent(user -> response.setSenderName(user.getNickname() != null ? user.getNickname() : user.getUsername()));
        }
        return response;
    }
}
