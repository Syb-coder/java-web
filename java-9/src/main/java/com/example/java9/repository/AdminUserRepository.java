package com.example.java9.repository; // 声明 Repository 层包路径，集中管理数据访问接口

import com.example.java9.model.AdminUser; // 引入管理员实体类，对应 admin_users 表
import com.example.java9.model.AdminRole; // 引入管理员角色枚举（OPERATION/RISK），用于按角色筛选
import org.springframework.data.jpa.repository.JpaRepository; // 引入 Spring Data JPA 仓储基础接口，提供 CRUD 能力
import org.springframework.stereotype.Repository; // 引入 @Repository 注解，标识为持久层 Bean

import java.util.List; // 引入 List，承载多行查询结果
import java.util.Optional; // 引入 Optional，安全包装可能为空的查询结果

/**
 * 管理员用户 Repository
 * <p>
 * 提供对 admin_users 表的数据库访问操作。
 * </p>
 */
@Repository // 标识为 Spring 持久层 Bean，由容器扫描并生成代理实现类
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> { // 继承 JPA 仓储，主键类型为 Long

    /**
     * 根据用户名查询管理员（登录校验用）
     *
     * @param username 用户名
     * @return 管理员实体（可能为空）
     */
    Optional<AdminUser> findByUsername(String username); // Spring Data 按方法名生成 SQL，登录时通过用户名定位账号

    /**
     * 根据角色查询管理员列表
     *
     * @param role 角色
     * @return 管理员列表
     */
    List<AdminUser> findByRole(AdminRole role); // 按角色枚举筛选（如查所有风控专员），运营后台分配工单时使用
}
