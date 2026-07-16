package com.example.java12.repository;  // 数据访问层包，存放 JPA Repository 接口

import com.example.java12.model.Plate;  // 板块实体
import org.springframework.data.jpa.repository.JpaRepository;  // JPA Repository 基接口
import org.springframework.stereotype.Repository;  // Repository 注解

import java.util.List;  // 列表
import java.util.Optional;  // Optional 包装类

/**
 * 板块数据访问层
 * <p>
 * 继承 JpaRepository，自动提供基础 CRUD。
 * 额外定义按排序序号查询全部板块、按名称查询板块的方法。
 * </p>
 */
@Repository
public interface PlateRepository extends JpaRepository<Plate, Long> {

    /**
     * 查询全部板块，按排序序号升序、创建时间升序排列
     * <p>
     * 用于首页板块导航栏展示
     * </p>
     *
     * @return 板块列表
     */
    List<Plate> findAllByOrderBySortOrderAscCreateTimeAsc();

    /**
     * 按名称查询板块（唯一性校验时使用）
     *
     * @param name 板块名称
     * @return 板块实体（可能为空）
     */
    Optional<Plate> findByName(String name);
}
