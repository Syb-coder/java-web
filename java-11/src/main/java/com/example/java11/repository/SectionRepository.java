package com.example.java11.repository;

import com.example.java11.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 板块数据访问层
 * <p>
 * 提供对 sections 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    /**
     * 按排序权重升序获取全部板块列表
     *
     * @return 排序后的板块列表
     */
    List<Section> findAllByOrderBySortOrderAsc();
}
