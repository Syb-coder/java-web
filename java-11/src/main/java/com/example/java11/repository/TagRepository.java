package com.example.java11.repository;

import com.example.java11.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 标签数据访问层
 * <p>
 * 提供对 tags 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 按标签名查找标签
     *
     * @param name 标签名
     * @return 标签对象，不存在时返回 empty
     */
    Optional<Tag> findByName(String name);

    /**
     * 查询使用次数最高的前 10 个标签
     *
     * @return 热门标签列表
     */
    List<Tag> findTop10ByOrderByUsageCountDesc();
}
