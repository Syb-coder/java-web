package com.example.java3.repository;

import com.example.java3.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 管理员用户仓储
 */
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    /**
     * 按用户名查询管理员
     *
     * @param username 用户名
     * @return 管理员 Optional
     */
    Optional<AdminUser> findByUsername(String username);
}
