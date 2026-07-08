package com.example.java3.service;

import com.example.java3.dto.MessageRequest;
import com.example.java3.dto.MessageResponse;
import com.example.java3.model.Message;
import com.example.java3.model.User;
import com.example.java3.repository.MessageRepository;
import com.example.java3.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 私信消息服务
 * <p>
 * 买卖双方基于商品进行一对一私信，消息永久留存用于纠纷取证。
 * </p>
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    /**
     * 发送私信
     *
     * @param senderId 发送者 ID
     * @param req      消息请求
     * @return 消息响应
     */
    public MessageResponse send(Long senderId, MessageRequest req) {
        if (senderId.equals(req.getReceiverId())) {
            throw new IllegalArgumentException("不能给自己发消息");
        }
        Message m = new Message(senderId, req.getReceiverId(), req.getProductId(), req.getContent());
        messageRepository.save(m);
        return toResponse(m);
    }

    /**
     * 查询两个用户之间的全部对话
     *
     * @param userId   当前用户 ID
     * @param otherId  对方用户 ID
     * @return 消息列表（按时间升序）
     */
    public List<MessageResponse> conversation(Long userId, Long otherId) {
        // 查询对话后，将对方发给当前用户的未读消息标记为已读
        List<Message> messages = messageRepository.findConversation(userId, otherId);
        messages.stream()
                .filter(m -> m.getReceiverId().equals(userId) && !m.getRead())
                .forEach(m -> {
                    m.setRead(true);
                    messageRepository.save(m);
                });
        return messages.stream().map(this::toResponse).toList();
    }

    /**
     * 查询用户最近会话列表（前端展示会话列表用）
     * <p>
     * 按对方用户 ID 去重，返回每个会话的最后一条消息。
     * </p>
     *
     * @param userId 用户 ID
     * @return 会话列表
     */
    public List<MessageResponse> recentConversations(Long userId) {
        List<Message> all = messageRepository.findUserMessages(userId);
        // 按对方 ID 分组取最新一条
        Map<Long, Message> latest = new HashMap<>();
        for (Message m : all) {
            Long otherId = m.getSenderId().equals(userId) ? m.getReceiverId() : m.getSenderId();
            Message exist = latest.get(otherId);
            if (exist == null || m.getCreatedAt().isAfter(exist.getCreatedAt())) {
                latest.put(otherId, m);
            }
        }
        return latest.values().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 统计用户未读消息数
     *
     * @param userId 用户 ID
     * @return 未读数
     */
    public long unreadCount(Long userId) {
        return messageRepository.countByReceiverIdAndReadFalse(userId);
    }

    /**
     * 实体转响应
     *
     * @param m 消息实体
     * @return 响应
     */
    private MessageResponse toResponse(Message m) {
        User sender = userRepository.findById(m.getSenderId()).orElse(null);
        User receiver = userRepository.findById(m.getReceiverId()).orElse(null);
        String senderName = sender != null ? sender.getNickname() : "未知用户";
        String receiverName = receiver != null ? receiver.getNickname() : "未知用户";
        return new MessageResponse(m, senderName, receiverName);
    }
}
