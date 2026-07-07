package com.example.java6.repository;

import com.example.java6.model.Regulation;
import com.example.java6.model.RegulationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 政策法规数据访问层
 */
@Repository
public interface RegulationRepository extends JpaRepository<Regulation, Long> {

    /**
     * 按分类查询法规列表（按发布日期倒序）
     *
     * @param category 法规分类，为 null 时查询全部分类
     * @return 法规列表
     */
    List<Regulation> findByCategoryOrderByPublishDateDesc(RegulationCategory category);

    /**
     * 查询全部法规（按发布日期倒序）
     *
     * @return 全部法规列表
     */
    List<Regulation> findAllByOrderByPublishDateDesc();

    /**
     * 查询最新发布的若干条法规
     *
     * @return 最新法规列表
     */
    List<Regulation> findTop6ByOrderByPublishDateDesc();
}
