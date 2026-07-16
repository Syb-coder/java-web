package com.example.java12.repository;  // 数据访问层包，存放 JPA Repository 接口

import com.example.java12.model.Comment;  // 评论实体
import org.springframework.data.jpa.repository.JpaRepository;  // JPA Repository 基接口
import org.springframework.stereotype.Repository;  // Repository 注解

import java.util.List;  // 列表

/**
 * 评论数据访问层
 * <p>
 * 继承 JpaRepository，自动提供基础 CRUD。
 * 提供按帖子查询评论、按用户查询评论的方法。
 * </p>
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 查询指定帖子的评论（按创建时间升序，先评论的在前）
     * <p>
     * 排除已删除的评论。已屏蔽评论（isHidden=true）仍返回，由前端展示"该评论已被屏蔽"。
     * </p>
     *
     * @param postId 帖子 ID
     * @return 评论列表
     */
    List<Comment> findByPostIdAndIsDeletedFalseOrderByCreateTimeAsc(Long postId);

    /**
     * 查询指定用户的评论（按创建时间倒序，用于"我的评论"）
     *
     * @param userId 用户 ID
     * @return 评论列表
     */
    List<Comment> findByUserIdAndIsDeletedFalseOrderByCreateTimeDesc(Long userId);
}
