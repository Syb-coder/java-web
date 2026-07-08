package com.example.java3.repository;

import com.example.java3.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品评论仓储
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 按商品查询评论（按时间升序，便于展示对话顺序）
     *
     * @param productId 商品 ID
     * @return 评论列表
     */
    List<Comment> findByProductIdOrderByCreatedAtAsc(Long productId);
}
