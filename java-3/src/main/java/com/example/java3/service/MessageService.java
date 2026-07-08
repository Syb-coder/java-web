// 声明当前类所在包路径
package com.example.java3.service;

// 导入消息请求 DTO
import com.example.java3.dto.MessageRequest;
// 导入消息响应 DTO
import com.example.java3.dto.MessageResponse;
// 导入消息实体模型
import com.example.java3.model.Message;
// 导入用户实体模型
import com.example.java3.model.User;
// 导入消息仓储接口（Spring Data JPA）
import com.example.java3.repository.MessageRepository;
// 导入用户仓储接口
import com.example.java3.repository.UserRepository;
// 导入 @Service 注解
import org.springframework.stereotype.Service;

// 导入 HashMap
import java.util.HashMap;
// 导入 List 集合
import java.util.List;
// 导入 Map 接口
import java.util.Map;
// 导入 Collectors 工具类
import java.util.stream.Collectors;

/**
 * 私信消息服务
 * <p>
 * 买卖双方基于商品进行一对一私信，消息永久留存用于纠纷取证。
 * </p>
 */
// 标识为业务层组件，由 Spring 容器管理为单例
@Service
public class MessageService {

    // 消息仓储，处理私信表 CRUD
    private final MessageRepository messageRepository;
    // 用户仓储，用于补充发送者/接收者昵称
    private final UserRepository userRepository;

    // 构造方法注入两个仓储 Bean
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
        // 校验不能给自己发消息，避免自言自语造成数据冗余
        if (senderId.equals(req.getReceiverId())) {
            throw new IllegalArgumentException("不能给自己发消息");
        }
        // 构造消息实体，包含发送者、接收者、关联商品 ID、内容
        Message m = new Message(senderId, req.getReceiverId(), req.getProductId(), req.getContent());
        // 持久化到数据库
        messageRepository.save(m);
        // 转换为响应 DTO
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
        // 过滤出对方发给当前用户的未读消息，逐条标记为已读
        messages.stream()
                // 接收者是当前用户且未读
                .filter(m -> m.getReceiverId().equals(userId) && !m.getRead())
                .forEach(m -> {
                    // 设置已读标志
                    m.setRead(true);
                    // 持久化更新
                    messageRepository.save(m);
                });
        // 将消息实体列表转换为响应 DTO 列表返回
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
        // 查询当前用户的所有消息
        List<Message> all = messageRepository.findUserMessages(userId);
        // 按对方 ID 分组取最新一条
        Map<Long, Message> latest = new HashMap<>();
        for (Message m : all) {
            // 判断对方 ID：如果发送者是当前用户，对方为接收者；否则对方为发送者
            Long otherId = m.getSenderId().equals(userId) ? m.getReceiverId() : m.getSenderId();
            // 获取已存的最新消息
            Message exist = latest.get(otherId);
            // 不存在或当前消息时间更晚则覆盖
            if (exist == null || m.getCreatedAt().isAfter(exist.getCreatedAt())) {
                latest.put(otherId, m);
            }
        }
        // 按时间降序排序，最近会话展示在前
        return latest.values().stream()
                // 比较时间，b 在前表示降序
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                // 转换为响应 DTO
                .map(this::toResponse)
                // 收集为 List
                .collect(Collectors.toList());
    }

    /**
     * 统计用户未读消息数
     *
     * @param userId 用户 ID
     * @return 未读数
     */
    public long unreadCount(Long userId) {
        // 调用仓储方法统计当前用户作为接收者且未读的消息数
        return messageRepository.countByReceiverIdAndReadFalse(userId);
    }

    /**
     * 实体转响应
     *
     * @param m 消息实体
     * @return 响应
     */
    private MessageResponse toResponse(Message m) {
        // 查询发送者用户信息
        User sender = userRepository.findById(m.getSenderId()).orElse(null);
        // 查询接收者用户信息
        User receiver = userRepository.findById(m.getReceiverId()).orElse(null);
        // 发送者昵称，不存在时使用占位文本
        String senderName = sender != null ? sender.getNickname() : "未知用户";
        // 接收者昵称，不存在时使用占位文本
        String receiverName = receiver != null ? receiver.getNickname() : "未知用户";
        // 构造响应 DTO
        return new MessageResponse(m, senderName, receiverName);
    }
}
