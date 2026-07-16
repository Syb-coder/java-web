package com.example.java11.repository;

import com.example.java11.model.Comment;
import com.example.java11.model.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论数据访问层
 * <p>
 * 提供对 comments 表的 CRUD 操作及按帖子、用户、楼层等维度的查询。
 * </p>
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 按帖子与状态查询评论列表，按楼层号升序返回
     *
     * @param postId 帖子 ID
     * @param status 评论状态
     * @return 评论列表
     */
    List<Comment> findByPostIdAndStatusOrderByFloorAsc(Long postId, CommentStatus status);

    /**
     * 查询指定用户发布的全部评论，按创建时间倒序
     *
     * @param userId 用户 ID
     * @return 用户评论历史
     */
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 获取帖子下指定状态的全部评论（不排序）
     *
     * @param postId 帖子 ID
     * @param status 评论状态
     * @return 评论列表
     */
    List<Comment> findByPostIdAndStatus(Long postId, CommentStatus status);

    /**
     * 统计帖子下指定状态的评论数量
     *
     * @param postId 帖子 ID
     * @param status 评论状态
     * @return 评论数量
     */
    long countByPostIdAndStatus(Long postId, CommentStatus status);

    /**
     * 获取帖子最大楼层号（用于生成下一楼层号）
     *
     * @param postId 帖子 ID
     * @return 最大楼层号，无评论时返回 null
     */
    @Query("SELECT MAX(c.floor) FROM Comment c WHERE c.postId = :postId")
    Integer findMaxFloorByPostId(@Param("postId") Long postId);
}
