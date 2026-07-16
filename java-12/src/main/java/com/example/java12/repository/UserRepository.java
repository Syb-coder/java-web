package com.example.java12.repository;  // 数据访问层包，存放 JPA Repository 接口

import com.example.java12.model.User;  // 用户实体
import org.springframework.data.jpa.repository.JpaRepository;  // JPA Repository 基接口
import org.springframework.stereotype.Repository;  // Repository 注解

import java.util.Optional;  // Optional 包装类

/**
 * 用户数据访问层
 * <p>
 * 继承 JpaRepository，自动提供基础 CRUD。
 * 额外定义按账号、昵称查询的方法，用于登录校验和唯一性检查。
 * </p>
 */
@Repository  // 声明为 Spring Data Repository，由 Spring 自动生成实现
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 按登录账号查询用户（登录时使用）
     *
     * @param account 登录账号
     * @return 用户实体（可能为空）
     */
    Optional<User> findByAccount(String account);

    /**
     * 按昵称查询用户（唯一性校验时使用）
     *
     * @param nickname 昵称
     * @return 用户实体（可能为空）
     */
    Optional<User> findByNickname(String nickname);

    /**
     * 检查账号是否已存在（注册时唯一性校验）
     *
     * @param account 登录账号
     * @return true 表示已存在
     */
    boolean existsByAccount(String account);

    /**
     * 检查昵称是否已存在（注册/修改昵称时唯一性校验）
     *
     * @param nickname 昵称
     * @return true 表示已存在
     */
    boolean existsByNickname(String nickname);
}
