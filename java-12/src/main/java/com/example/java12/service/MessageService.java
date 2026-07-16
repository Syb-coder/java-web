package com.example.java12.service;  // 服务层包

import com.example.java12.model.Message;  // 消息实体
import com.example.java12.repository.MessageRepository;  // 消息数据访问层
import org.springframework.stereotype.Service;  // Service 注解

import java.util.List;  // 列表

/**
 * 站内消息服务
 * <p>
 * 负责发送站内通知消息、查询用户消息列表、标记已读。
 * 被 PostService、UserService 等调用，向用户推送系统通知。
 * </p>
 */
@Service
public class MessageService {

    /** 消息数据访问层 */
    private final MessageRepository messageRepository;

    /**
     * 构造器注入
     *
     * @param messageRepository 消息数据访问层
     */
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * 发送站内消息
     *
     * @param userId  接收者 ID
     * @param content 消息内容
     */
    public void sendMessage(Long userId, String content) {
        Message message = new Message(userId, content);
        messageRepository.save(message);
    }

    /**
     * 查询用户的全部消息（按时间倒序）
     *
     * @param userId 用户 ID
     * @return 消息列表
     */
    public List<Message> findByUserId(Long userId) {
        return messageRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    /**
     * 统计用户未读消息数
     *
     * @param userId 用户 ID
     * @return 未读消息数
     */
    public int countUnread(Long userId) {
        return messageRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * 标记消息为已读
     *
     * @param messageId 消息 ID
     */
    public void markAsRead(Long messageId) {
        messageRepository.findById(messageId).ifPresent(msg -> {
            msg.setIsRead(true);
            messageRepository.save(msg);
        });
    }
}
