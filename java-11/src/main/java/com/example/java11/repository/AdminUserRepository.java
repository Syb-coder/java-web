package com.example.java11.repository;

import com.example.java11.model.AdminRole;
import com.example.java11.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 管理员数据访问层
 * <p>
 * 提供对 admin_users 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    /**
     * 按用户名查找管理员（后台登录验证用）
     *
     * @param username 管理员用户名
     * @return 管理员对象，不存在时返回 empty
     */
    Optional<AdminUser> findByUsername(String username);

    /**
     * 按角色查询管理员列表
     *
     * @param role 管理员角色
     * @return 符合角色的管理员列表
     */
    List<AdminUser> findByRole(AdminRole role);
}
