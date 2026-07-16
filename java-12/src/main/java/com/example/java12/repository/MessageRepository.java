package com.example.java12.repository;  // 数据访问层包，存放 JPA Repository 接口

import com.example.java12.model.Message;  // 消息实体
import org.springframework.data.jpa.repository.JpaRepository;  // JPA Repository 基接口
import org.springframework.stereotype.Repository;  // Repository 注解

import java.util.List;  // 列表

/**
 * 站内消息数据访问层
 * <p>
 * 继承 JpaRepository，自动提供基础 CRUD。
 * 提供按用户查询消息、统计未读消息数的方法。
 * </p>
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 查询指定用户的消息（按发送时间倒序）
     *
     * @param userId 用户 ID
     * @return 消息列表
     */
    List<Message> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 统计指定用户的未读消息数（用于导航栏未读角标）
     *
     * @param userId 用户 ID
     * @return 未读消息数
     */
    int countByUserIdAndIsReadFalse(Long userId);
}
