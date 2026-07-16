package com.example.java12.repository;  // 数据访问层包，存放 JPA Repository 接口

import com.example.java12.model.Post;  // 帖子实体
import org.springframework.data.domain.Page;  // 分页结果
import org.springframework.data.domain.Pageable;  // 分页参数
import org.springframework.data.jpa.repository.JpaRepository;  // JPA Repository 基接口
import org.springframework.data.jpa.repository.Query;  // 自定义查询注解
import org.springframework.data.repository.query.Param;  // 命名参数绑定
import org.springframework.stereotype.Repository;  // Repository 注解

import java.time.LocalDateTime;  // 时间类型
import java.util.List;  // 列表
import java.util.Optional;  // Optional 包装类

/**
 * 帖子数据访问层
 * <p>
 * 继承 JpaRepository，自动提供基础 CRUD。
 * 提供最新帖子、热门帖子、板块帖子、搜索帖子等查询方法。
 * 所有查询均排除已软删除的帖子（isDeleted = false）。
 * </p>
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 查询最新帖子（按创建时间倒序）
     *
     * @param pageable 分页参数
     * @return 帖子分页列表
     */
    Page<Post> findByIsDeletedFalseOrderByCreateTimeDesc(Pageable pageable);

    /**
     * 查询热门帖子（按点赞数倒序，其次评论数倒序）
     * <p>
     * 用于首页热门推荐卡片
     * </p>
     *
     * @param pageable 分页参数
     * @return 帖子分页列表
     */
    Page<Post> findByIsDeletedFalseOrderByLikeCountDescCommentCountDescCreateTimeDesc(Pageable pageable);

    /**
     * 查询指定板块的帖子（按创建时间倒序）
     *
     * @param plateId  板块 ID
     * @param pageable 分页参数
     * @return 帖子分页列表
     */
    Page<Post> findByPlateIdAndIsDeletedFalseOrderByCreateTimeDesc(Long plateId, Pageable pageable);

    /**
     * 查询指定用户的帖子（按创建时间倒序，用于"我的发帖"）
     *
     * @param userId   用户 ID
     * @param pageable 分页参数
     * @return 帖子分页列表
     */
    Page<Post> findByUserIdAndIsDeletedFalseOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 按 ID 查询未删除的帖子（帖子详情页使用）
     *
     * @param id 帖子 ID
     * @return 帖子实体（可能为空）
     */
    Optional<Post> findByIdAndIsDeletedFalse(Long id);

    /**
     * 查询置顶帖子（版主/管理员推送的优质帖）
     *
     * @return 置顶帖子列表
     */
    List<Post> findByIsTopTrueAndIsDeletedFalse();

    /**
     * 综合搜索帖子
     * <p>
     * 支持关键词模糊搜索（匹配标题和正文）、板块筛选、时间范围筛选。
     * 排序方式由 Pageable 的 Sort 参数控制（热度排序或时间排序）。
     * 所有参数为 null 时等同于浏览全部帖子。
     * </p>
     *
     * @param keyword   搜索关键词（可为 null）
     * @param plateId   板块 ID（可为 null）
     * @param startTime 起始时间（可为 null）
     * @param pageable  分页参数（含排序）
     * @return 帖子分页列表
     */
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false " +
            "AND (:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " +
            "AND (:plateId IS NULL OR p.plateId = :plateId) " +
            "AND (:startTime IS NULL OR p.createTime >= :startTime)")
    Page<Post> searchPosts(@Param("keyword") String keyword,
                           @Param("plateId") Long plateId,
                           @Param("startTime") LocalDateTime startTime,
                           Pageable pageable);
}
