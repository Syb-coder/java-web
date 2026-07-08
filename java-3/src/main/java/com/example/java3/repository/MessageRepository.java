package com.example.java3.repository;

import com.example.java3.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私信消息仓储
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 查询两个用户之间的全部私信（双向），按时间升序
     *
     * @param userIdA 用户 A ID
     * @param userIdB 用户 B ID
     * @return 消息列表
     */
    @org.springframework.data.jpa.repository.Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :userIdA AND m.receiverId = :userIdB) OR " +
            "(m.senderId = :userIdB AND m.receiverId = :userIdA) ORDER BY m.createdAt ASC")
    List<Message> findConversation(@org.springframework.data.repository.query.Param("userIdA") Long userIdA,
                                   @org.springframework.data.repository.query.Param("userIdB") Long userIdB);

    /**
     * 查询用户最近的会话对象（去重）
     *
     * @param userId 用户 ID
     * @return 消息列表（按时间倒序，前端自行去重展示）
     */
    @org.springframework.data.jpa.repository.Query("SELECT m FROM Message m WHERE m.senderId = :userId OR m.receiverId = :userId ORDER BY m.createdAt DESC")
    List<Message> findUserMessages(@org.springframework.data.repository.query.Param("userId") Long userId);

    /**
     * 统计用户未读消息数
     *
     * @param receiverId 接收者 ID
     * @return 未读数
     */
    long countByReceiverIdAndReadFalse(Long receiverId);
}
