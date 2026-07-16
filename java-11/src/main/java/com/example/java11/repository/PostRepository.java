package com.example.java11.repository;

import com.example.java11.model.Post;
import com.example.java11.model.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 帖子数据访问层
 * <p>
 * 提供对 posts 表的 CRUD 操作及多种维度的查询，包括按板块、状态、用户、
 * 关键词检索及热门帖子统计等。
 * </p>
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 按板块与状态查询帖子列表，置顶优先，再按创建时间倒序
     *
     * @param sectionId 板块 ID
     * @param status    帖子状态
     * @return 符合条件的帖子列表
     */
    List<Post> findBySectionIdAndStatusOrderByIsTopDescCreatedAtDesc(Long sectionId, PostStatus status);

    /**
     * 按状态查询全部帖子，按创建时间倒序
     *
     * @param status 帖子状态
     * @return 符合状态的帖子列表
     */
    List<Post> findByStatusOrderByCreatedAtDesc(PostStatus status);

    /**
     * 查询指定用户发布的全部帖子，按创建时间倒序
     *
     * @param userId 用户 ID
     * @return 用户的发帖历史
     */
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 按标题或正文关键词搜索帖子，按创建时间倒序
     *
     * @param title   标题关键词
     * @param content 正文关键词
     * @return 匹配的帖子列表
     */
    List<Post> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(String title, String content);

    /**
     * 统计指定板块下符合状态的帖子数量
     *
     * @param sectionId 板块 ID
     * @param status    帖子状态
     * @return 帖子数量
     */
    long countBySectionIdAndStatus(Long sectionId, PostStatus status);

    /**
     * 查询浏览量最高的前 10 条帖子（仅指定状态）
     *
     * @param status 帖子状态
     * @return 热门帖子列表
     */
    List<Post> findTop10ByStatusOrderByViewCountDesc(PostStatus status);

    /**
     * 分页查询指定状态的帖子，按创建时间倒序
     *
     * @param status   帖子状态
     * @param pageable 分页参数
     * @return 帖子分页结果
     */
    Page<Post> findByStatusOrderByCreatedAtDesc(PostStatus status, Pageable pageable);
}
