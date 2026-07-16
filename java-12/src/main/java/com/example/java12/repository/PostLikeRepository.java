package com.example.java12.repository;  // 数据访问层包，存放 JPA Repository 接口

import com.example.java12.model.PostLike;  // 点赞实体
import org.springframework.data.jpa.repository.JpaRepository;  // JPA Repository 基接口
import org.springframework.stereotype.Repository;  // Repository 注解

import java.util.Optional;  // Optional 包装类

/**
 * 帖子点赞数据访问层
 * <p>
 * 继承 JpaRepository，自动提供基础 CRUD。
 * 提供按用户和帖子查询点赞记录、检查点赞状态、取消点赞的方法。
 * </p>
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * 按用户 ID 和帖子 ID 查询点赞记录（检查是否已点赞）
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     * @return 点赞实体（可能为空）
     */
    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

    /**
     * 检查用户是否已点赞指定帖子（幂等校验）
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     * @return true 表示已点赞
     */
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    /**
     * 按用户 ID 和帖子 ID 删除点赞记录（取消点赞）
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     */
    void deleteByUserIdAndPostId(Long userId, Long postId);
}
