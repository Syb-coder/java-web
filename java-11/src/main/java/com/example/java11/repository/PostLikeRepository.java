package com.example.java11.repository;

import com.example.java11.model.PostLike;
import com.example.java11.model.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 点赞记录数据访问层
 * <p>
 * 提供对 post_likes 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * 检查用户是否已对指定目标点赞
     *
     * @param userId     用户 ID
     * @param targetType 目标类型（POST 或 COMMENT）
     * @param targetId   目标 ID
     * @return 点赞记录，未点赞时返回 empty
     */
    Optional<PostLike> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    /**
     * 取消点赞（删除点赞记录）
     *
     * @param userId     用户 ID
     * @param targetType 目标类型
     * @param targetId   目标 ID
     */
    void deleteByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    /**
     * 统计指定目标的点赞总数
     *
     * @param targetType 目标类型
     * @param targetId   目标 ID
     * @return 点赞数量
     */
    long countByTargetTypeAndTargetId(TargetType targetType, Long targetId);
}
