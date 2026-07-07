package com.example.java6.repository; // 定义 Repository 接口所在包，统一存放数据访问层组件

import com.example.java6.model.AdminUser; // 导入管理员实体类，对应数据库 admin_user 表
import org.springframework.data.jpa.repository.JpaRepository; // 导入 JPA 基础 Repository 接口，提供标准 CRUD 能力
import org.springframework.stereotype.Repository; // 导入 @Repository 注解，标记数据访问层组件

import java.util.Optional; // 导入 Optional 容器，用于安全包装可能为 null 的查询结果

/**
 * 管理员账号仓库
 * 负责管理员账号的持久化访问，提供登录认证、注册查重等数据操作能力
 */
@Repository // 标记为 Spring Repository 组件，由 Spring 容器管理为 Bean，并启用异常转换
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> { // 继承 JpaRepository，主键类型为 Long，自动获得 save/findAll/findById 等标准 CRUD 方法

    /**
     * 按用户名查找管理员
     * Spring Data JPA 根据方法名派生查询 SQL：SELECT * FROM admin_user WHERE username = ?
     * 主要用于登录认证时根据用户名定位账号记录
     *
     * @param username 用户名
     * @return 管理员（可选），使用 Optional 包装避免返回 null 造成 NPE
     */
    Optional<AdminUser> findByUsername(String username); // 派生查询方法：findBy 表示按字段查询，Username 对应实体 username 字段

    /**
     * 判断用户名是否已存在
     * Spring Data JPA 派生查询：SELECT COUNT(*) > 0 FROM admin_user WHERE username = ?
     * 主要用于管理员注册时的用户名重复校验，避免唯一约束冲突
     *
     * @param username 用户名
     * @return 是否存在，true 表示已被占用
     */
    boolean existsByUsername(String username); // 派生查询方法：existsBy 表示存在性判断，比 count 性能更优（命中即返回）
}
