package com.example.java12.repository;  // 数据访问层包，存放 JPA Repository 接口

import com.example.java12.model.Moderator;  // 版主关联实体
import org.springframework.data.jpa.repository.JpaRepository;  // JPA Repository 基接口
import org.springframework.stereotype.Repository;  // Repository 注解

import java.util.List;  // 列表

/**
 * 版主关联数据访问层
 * <p>
 * 继承 JpaRepository，自动提供基础 CRUD。
 * 提供按用户查询所管板块、按板块查询版主、检查版主权限、回收版主权限的方法。
 * </p>
 */
@Repository
public interface ModeratorRepository extends JpaRepository<Moderator, Long> {

    /**
     * 查询指定用户负责管理的所有板块（用于判断版主权限范围）
     *
     * @param userId 用户 ID
     * @return 版主关联列表
     */
    List<Moderator> findByUserId(Long userId);

    /**
     * 查询指定板块的所有版主
     *
     * @param plateId 板块 ID
     * @return 版主关联列表
     */
    List<Moderator> findByPlateId(Long plateId);

    /**
     * 检查用户是否是指定板块的版主（权限校验）
     *
     * @param userId  用户 ID
     * @param plateId 板块 ID
     * @return true 表示是版主
     */
    boolean existsByUserIdAndPlateId(Long userId, Long plateId);

    /**
     * 删除指定用户在指定板块的版主权限（回收版主权限）
     *
     * @param userId  用户 ID
     * @param plateId 板块 ID
     */
    void deleteByUserIdAndPlateId(Long userId, Long plateId);
}
