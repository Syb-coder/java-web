package com.example.java11.repository;

import com.example.java11.model.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 帖子-标签关联数据访问层
 * <p>
 * 提供对 post_tags 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    /**
     * 获取帖子的所有标签关联
     *
     * @param postId 帖子 ID
     * @return 标签关联列表
     */
    List<PostTag> findByPostId(Long postId);

    /**
     * 获取使用该标签的所有帖子关联
     *
     * @param tagId 标签 ID
     * @return 帖子关联列表
     */
    List<PostTag> findByTagId(Long tagId);

    /**
     * 删除帖子的所有标签关联
     *
     * @param postId 帖子 ID
     */
    void deleteByPostId(Long postId);
}
