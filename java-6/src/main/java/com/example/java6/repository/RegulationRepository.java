package com.example.java6.repository; // 定义 Repository 接口所在包，统一存放数据访问层组件

import com.example.java6.model.Regulation; // 导入政策法规实体类，对应数据库 regulation 表
import com.example.java6.model.RegulationCategory; // 导入法规分类枚举，定义政策法规的业务分类
import org.springframework.data.jpa.repository.JpaRepository; // 导入 JPA 基础 Repository 接口，提供标准 CRUD 能力
import org.springframework.stereotype.Repository; // 导入 @Repository 注解，标记数据访问层组件

import java.util.List; // 导入 List 集合，用于返回多条法规记录

/**
 * 政策法规数据访问层
 * 负责政策法规的持久化访问，支持按分类查询、按发布日期排序、最新法规展示等业务场景
 */
@Repository // 标记为 Spring Repository 组件，由 Spring 容器管理为 Bean，并启用异常转换
public interface RegulationRepository extends JpaRepository<Regulation, Long> { // 继承 JpaRepository，主键类型为 Long，自动获得标准 CRUD 方法

    /**
     * 按分类查询法规列表（按发布日期倒序）
     * Spring Data JPA 派生查询：SELECT * FROM regulation WHERE category = ? ORDER BY publish_date DESC
     * 用于政策法规按分类筛选展示，最新发布的排在最前
     *
     * @param category 法规分类，为 null 时查询全部分类
     * @return 法规列表
     */
    List<Regulation> findByCategoryOrderByPublishDateDesc(RegulationCategory category); // 派生查询：findBy Category 按分类过滤，OrderByPublishDateDesc 按发布日期倒序

    /**
     * 查询全部法规（按发布日期倒序）
     * Spring Data JPA 派生查询：SELECT * FROM regulation ORDER BY publish_date DESC
     * 用于后台管理展示全部法规，最新发布的排在最前
     *
     * @return 全部法规列表
     */
    List<Regulation> findAllByOrderByPublishDateDesc(); // 派生查询：findAllBy 表示查询全部，OrderByPublishDateDesc 按发布日期倒序

    /**
     * 查询最新发布的若干条法规
     * Spring Data JPA 派生查询：SELECT * FROM regulation ORDER BY publish_date DESC LIMIT 6
     * 用于首页"政策法规"板块展示，固定取 6 条
     *
     * @return 最新法规列表
     */
    List<Regulation> findTop6ByOrderByPublishDateDesc(); // 派生查询：findTop6 表示取前 6 条，OrderByPublishDateDesc 按发布日期倒序
}
