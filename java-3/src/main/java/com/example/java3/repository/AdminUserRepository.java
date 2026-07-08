// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 AdminUser 实体类，对应管理员用户表
import com.example.java3.model.AdminUser;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 Optional 容器类，用于安全包装可能为 null 的查询结果
import java.util.Optional;

/**
 * 管理员用户仓储
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现，自动将异常转换为数据访问异常
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    /**
     * 按用户名查询管理员
     *
     * @param username 用户名
     * @return 管理员 Optional
     */
    // 按用户名查询管理员记录，Spring Data 根据方法名自动生成查询 SQL
    // 返回 Optional 防止空指针异常，调用方需用 isPresent() 或 orElse() 处理
    Optional<AdminUser> findByUsername(String username);
}
