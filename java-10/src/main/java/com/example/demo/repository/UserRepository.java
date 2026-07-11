package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 * <p>
 * 为什么继承 JpaRepository：自动提供 save、findById、findAll、deleteById 等基础 CRUD 方法，
 * 无需手写 SQL。通过方法名约定，Spring Data JPA 自动生成查询实现。
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     * <p>
     * 为什么需要此方法：登录时需要通过用户名查找账号记录
     * Spring Data JPA 根据方法名 findByUsername 自动生成 SQL: SELECT * FROM users WHERE username = ?
     * </p>
     *
     * @param username 用户名
     * @return 包含用户的 Optional，不存在时为 empty
     */
    Optional<User> findByUsername(String username);

    /**
     * 检查用户名是否已存在
     * <p>
     * 为什么需要此方法：创建用户前需校验用户名唯一性，使用 exists 比 findByUsername 更高效
     * 自动生成 SQL: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     * </p>
     *
     * @param username 用户名
     * @return 存在返回 true，否则 false
     */
    boolean existsByUsername(String username);
}
