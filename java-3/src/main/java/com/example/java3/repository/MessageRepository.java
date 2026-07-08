// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 Message 实体类，对应私信消息表
import com.example.java3.model.Message;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 List 集合类，用于承载多条查询结果
import java.util.List;

/**
 * 私信消息仓储
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 查询两个用户之间的全部私信（双向），按时间升序
     *
     * @param userIdA 用户 A ID
     * @param userIdB 用户 B ID
     * @return 消息列表
     */
    // 使用 @Query 注解自定义 JPQL 查询，检索两个用户之间的双向会话记录
    // 查询逻辑：(senderId=A 且 receiverId=B) 或 (senderId=B 且 receiverId=A)
    // 按 createdAt 升序排列，便于按时间顺序展示完整对话
    @org.springframework.data.jpa.repository.Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :userIdA AND m.receiverId = :userIdB) OR " +
            "(m.senderId = :userIdB AND m.receiverId = :userIdA) ORDER BY m.createdAt ASC")
    // findConversation 方法通过 @Param 注解将参数绑定到 JPQL 中的命名参数 :userIdA / :userIdB
    List<Message> findConversation(@org.springframework.data.repository.query.Param("userIdA") Long userIdA,
                                   @org.springframework.data.repository.query.Param("userIdB") Long userIdB);

    /**
     * 查询用户最近的会话对象（去重）
     *
     * @param userId 用户 ID
     * @return 消息列表（按时间倒序，前端自行去重展示）
     */
    // 使用 @Query 自定义 JPQL 查询，查询用户作为发送者或接收者的全部消息
    // 按 createdAt 倒序排列，前端拿到后按对方用户去重展示最近会话列表
    @org.springframework.data.jpa.repository.Query("SELECT m FROM Message m WHERE m.senderId = :userId OR m.receiverId = :userId ORDER BY m.createdAt DESC")
    // findUserMessages 方法通过 @Param 注解将 userId 绑定到 JPQL 的命名参数 :userId
    List<Message> findUserMessages(@org.springframework.data.repository.query.Param("userId") Long userId);

    /**
     * 统计用户未读消息数
     *
     * @param receiverId 接收者 ID
     * @return 未读数
     */
    // 按 receiverId 查询并统计 read 字段为 false 的消息数量，用于展示未读消息角标
    long countByReceiverIdAndReadFalse(Long receiverId);
}
