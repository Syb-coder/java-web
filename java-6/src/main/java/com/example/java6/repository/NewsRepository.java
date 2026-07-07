package com.example.java6.repository; // 定义 Repository 接口所在包，统一存放数据访问层组件

import com.example.java6.model.News; // 导入新闻资讯实体类，对应数据库 news 表
import com.example.java6.model.NewsCategory; // 导入新闻分类枚举，定义新闻的业务分类
import org.springframework.data.jpa.repository.JpaRepository; // 导入 JPA 基础 Repository 接口，提供标准 CRUD 能力
import org.springframework.stereotype.Repository; // 导入 @Repository 注解，标记数据访问层组件

import java.util.List; // 导入 List 集合，用于返回多条新闻记录

/**
 * 新闻资讯数据访问层
 * 负责新闻资讯的持久化访问，支持按分类查询、按发布时间排序、置顶新闻展示等业务场景
 */
@Repository // 标记为 Spring Repository 组件，由 Spring 容器管理为 Bean，并启用异常转换
public interface NewsRepository extends JpaRepository<News, Long> { // 继承 JpaRepository，主键类型为 Long，自动获得标准 CRUD 方法

    /**
     * 按分类查询新闻列表（按发布时间倒序）
     * Spring Data JPA 派生查询：SELECT * FROM news WHERE category = ? ORDER BY publish_time DESC
     * 用于新闻资讯按分类筛选展示，最新发布的排在最前
     *
     * @param category 新闻分类，为 null 时查询全部分类（注意：null 时仍会拼接到 WHERE 子句）
     * @return 新闻列表
     */
    List<News> findByCategoryOrderByPublishTimeDesc(NewsCategory category); // 派生查询：findBy Category 按 category 字段过滤，OrderByPublishTimeDesc 按发布时间倒序

    /**
     * 查询全部新闻（按发布时间倒序）
     * Spring Data JPA 派生查询：SELECT * FROM news ORDER BY publish_time DESC
     * 用于后台管理展示全部新闻，最新发布的排在最前
     *
     * @return 全部新闻列表
     */
    List<News> findAllByOrderByPublishTimeDesc(); // 派生查询：findAllBy 表示查询全部，OrderByPublishTimeDesc 按发布时间倒序

    /**
     * 查询最新发布的若干条新闻
     * Spring Data JPA 派生查询：SELECT * FROM news ORDER BY publish_time DESC LIMIT 8
     * 用于官网首页"最新动态"板块展示，固定取 8 条
     *
     * @param limit 数量上限（注：方法名固定为 Top8，limit 参数仅用于语义说明，实际由 Top8 决定）
     * @return 最新新闻列表
     */
    List<News> findTop8ByOrderByPublishTimeDesc(); // 派生查询：findTop8 表示取前 8 条，OrderByPublishTimeDesc 按发布时间倒序

    /**
     * 查询置顶新闻列表
     * Spring Data JPA 派生查询：SELECT * FROM news WHERE top = true ORDER BY publish_time DESC
     * 用于首页轮播或重点新闻展示，仅展示置顶标识为 true 的新闻
     *
     * @return 置顶新闻列表
     */
    List<News> findByTopTrueOrderByPublishTimeDesc(); // 派生查询：findByTopTrue 表示 top 字段为 true 的记录，按发布时间倒序
}
