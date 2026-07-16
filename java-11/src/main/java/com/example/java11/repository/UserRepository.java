package com.example.java11.repository;

import com.example.java11.model.User;
import com.example.java11.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层
 * <p>
 * 提供对 users 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 按用户名查找用户（登录验证用）
     *
     * @param username 登录用户名
     * @return 用户对象，不存在时返回 empty
     */
    Optional<User> findByUsername(String username);

    /**
     * 校验用户名是否已存在（注册时唯一性检查）
     *
     * @param username 待校验的用户名
     * @return true 已存在，false 可用
     */
    boolean existsByUsername(String username);

    /**
     * 按账户状态筛选用户
     *
     * @param status 账户状态
     * @return 符合状态的用户列表
     */
    List<User> findByStatus(UserStatus status);

    /**
     * 按用户名关键词模糊搜索用户
     *
     * @param keyword 搜索关键词
     * @return 匹配的用户列表
     */
    List<User> findByUsernameContaining(String keyword);
}
