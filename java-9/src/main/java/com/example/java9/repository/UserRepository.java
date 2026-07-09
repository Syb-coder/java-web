package com.example.java9.repository; // 声明 Repository 层包路径

import com.example.java9.model.User; // 引入 C 端用户实体，对应 users 表
import com.example.java9.model.UserStatus; // 引入用户状态枚举（NORMAL/FROZEN 等），用于状态筛选
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JPA 仓储基础接口
import org.springframework.stereotype.Repository; // 引入 @Repository 注解

import java.util.List; // 引入 List 容器
import java.util.Optional; // 引入 Optional 包装类

/**
 * C端用户 Repository
 */
@Repository // 标识为持久层 Bean
public interface UserRepository extends JpaRepository<User, Long> { // 继承 JPA，主键 Long

    /** 根据用户名查询（登录/注册校验） */
    Optional<User> findByUsername(String username); // 登录时凭用户名定位账号；注册时用于检测用户名是否已存在

    /** 根据身份证号查询（实名认证唯一性校验） */
    Optional<User> findByIdCard(String idCard); // 实名认证时校验身份证是否已被其他账号绑定，保证一人一证

    /** 根据状态查询用户列表 */
    List<User> findByStatus(UserStatus status); // 运营后台按状态筛选用户（如查看全部冻结账号）
}
