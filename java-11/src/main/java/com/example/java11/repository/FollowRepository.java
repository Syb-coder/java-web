package com.example.java11.repository;

import com.example.java11.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 关注关系数据访问层
 * <p>
 * 提供对 follows 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * 查询用户的关注列表，按创建时间倒序
     *
     * @param followerId 关注者 ID
     * @return 关注列表
     */
    List<Follow> findByFollowerIdOrderByCreatedAtDesc(Long followerId);

    /**
     * 查询用户的粉丝列表，按创建时间倒序
     *
     * @param followeeId 被关注者 ID
     * @return 粉丝列表
     */
    List<Follow> findByFolloweeIdOrderByCreatedAtDesc(Long followeeId);

    /**
     * 检查是否已存在关注关系
     *
     * @param followerId 关注者 ID
     * @param followeeId 被关注者 ID
     * @return 关注记录，未关注时返回 empty
     */
    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    /**
     * 取消关注（删除关注记录）
     *
     * @param followerId 关注者 ID
     * @param followeeId 被关注者 ID
     */
    void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    /**
     * 统计用户的关注数
     *
     * @param followerId 关注者 ID
     * @return 关注数
     */
    long countByFollowerId(Long followerId);

    /**
     * 统计用户的粉丝数
     *
     * @param followeeId 被关注者 ID
     * @return 粉丝数
     */
    long countByFolloweeId(Long followeeId);
}
