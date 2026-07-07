package com.example.java6.repository;

import com.example.java6.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 管理员账号仓库
 */
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    /**
     * 按用户名查找管理员
     *
     * @param username 用户名
     * @return 管理员（可选）
     */
    Optional<AdminUser> findByUsername(String username);

    /**
     * 判断用户名是否已存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
}
