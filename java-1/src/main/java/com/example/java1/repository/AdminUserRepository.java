// 声明包路径，归类为 repository 仓储层，存放 JPA 仓储接口
package com.example.java1.repository;

// 导入管理员用户实体类，对应数据库 admin_users 表
import com.example.java1.model.AdminUser;
// 导入 Spring Data JPA 仓储接口，继承后自动获得标准 CRUD 实现
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标记为持久层组件，由 Spring IoC 容器管理为 Bean
import org.springframework.stereotype.Repository;

// 导入 Optional 包装类型，强制调用方显式处理空值，避免 NPE
import java.util.Optional;

/**
 * 管理员用户仓储
 * <p>
 * 继承 JpaRepository 即可获得标准 CRUD 方法（save、findById、findAll 等）。
 * 通过方法名约定声明自定义查询，Spring Data JPA 在运行时自动生成实现。
 * </p>
 */
@Repository // 声明为 Spring 仓储组件，由 IoC 容器管理为单例 Bean，并封装数据访问异常转换
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> { // 泛型参数：实体类型为 AdminUser，主键类型为 Long

    /**
     * 根据用户名查询管理员
     * <p>Spring Data 会解析方法名为 "findBy + Username"，生成 WHERE username = ? 查询。</p>
     *
     * @param username 用户名
     * @return 管理员（可能为空）
     */
    Optional<AdminUser> findByUsername(String username); // 方法名约定：findBy + 字段名，Spring Data 在启动期解析并生成 JPQL 实现，登录校验时按用户名定位管理员
}
