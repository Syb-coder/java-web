package com.example.java11.repository;

import com.example.java11.model.Work;
import com.example.java11.model.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 作品数据访问层
 * <p>
 * 提供对 works 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    /**
     * 按类型筛选作品
     *
     * @param type 作品类型
     * @return 作品列表
     */
    List<Work> findByType(WorkType type);

    /**
     * 按标题关键词搜索作品
     *
     * @param keyword 搜索关键词
     * @return 匹配的作品列表
     */
    List<Work> findByTitleContaining(String keyword);

    /**
     * 查询评分最高的前 10 个作品（高分推荐）
     *
     * @return 高分作品列表
     */
    List<Work> findTop10ByOrderByRatingDesc();
}
