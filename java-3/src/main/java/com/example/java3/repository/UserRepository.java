package com.example.java3.repository;

import com.example.java3.model.User;
import com.example.java3.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 学生用户仓储
 * <p>
 * 通过 Spring Data JPA 自动生成实现，提供按学号、用户名查询等能力。
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 按学号查询用户
     *
     * @param studentId 学号
     * @return 用户 Optional
     */
    Optional<User> findByStudentId(String studentId);

    /**
     * 按用户名查询用户
     *
     * @param username 用户名
     * @return 用户 Optional
     */
    Optional<User> findByUsername(String username);

    /**
     * 判断学号是否已存在
     *
     * @param studentId 学号
     * @return 是否存在
     */
    boolean existsByStudentId(String studentId);

    /**
     * 判断用户名是否已存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 按状态统计用户数量
     *
     * @param status 用户状态
     * @return 数量
     */
    long countByStatus(UserStatus status);
}
