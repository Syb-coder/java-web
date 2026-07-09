// 声明包路径，存放 Spring Data JPA Repository 接口
package com.example.java4.repository;

// 导入实体类与 JPA 注解
import com.example.java4.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 管理员数据访问层
 * <p>
 * 继承 JpaRepository 即可获得 CRUD 能力，无需手写 SQL。
 * 自定义查询方法按 Spring Data 方法命名约定自动生成实现。
 * </p>
 */
@Repository // 声明本接口为 Spring Bean，由容器管理
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    /**
     * 根据用户名查找管理员（登录校验使用）
     *
     * @param username 用户名
     * @return 管理员实体（可能为空）
     */
    Optional<AdminUser> findByUsername(String username);

    /**
     * 检查用户名是否已存在（注册时判重）
     *
     * @param username 用户名
     * @return true 表示已存在
     */
    boolean existsByUsername(String username);
}
