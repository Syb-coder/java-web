package com.example.java12.repository;  // 数据访问层包，存放 JPA Repository 接口

import com.example.java12.model.Collect;  // 收藏实体
import org.springframework.data.jpa.repository.JpaRepository;  // JPA Repository 基接口
import org.springframework.stereotype.Repository;  // Repository 注解

import java.util.List;  // 列表
import java.util.Optional;  // Optional 包装类

/**
 * 收藏数据访问层
 * <p>
 * 继承 JpaRepository，自动提供基础 CRUD。
 * 提供按用户查询收藏、检查收藏关系、取消收藏的方法。
 * </p>
 */
@Repository
public interface CollectRepository extends JpaRepository<Collect, Long> {

    /**
     * 查询指定用户的收藏记录（按收藏时间倒序，用于"我的收藏"）
     *
     * @param userId 用户 ID
     * @return 收藏列表
     */
    List<Collect> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 按用户 ID 和帖子 ID 查询收藏记录（检查是否已收藏）
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     * @return 收藏实体（可能为空）
     */
    Optional<Collect> findByUserIdAndPostId(Long userId, Long postId);

    /**
     * 检查用户是否已收藏指定帖子（幂等校验）
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     * @return true 表示已收藏
     */
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    /**
     * 按用户 ID 和帖子 ID 删除收藏记录（取消收藏）
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     */
    void deleteByUserIdAndPostId(Long userId, Long postId);
}
