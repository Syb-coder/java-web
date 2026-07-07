// 声明包路径
package com.example.java2.repository;

// 导入实体类与 Spring Data JPA 接口
import com.example.java2.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

// 导入 Optional 类型
import java.util.Optional;

/**
 * 管理员仓储
 * <p>
 * 继承 {@link JpaRepository} 自动获得 CRUD 能力。
 * 通过 findByUsername 派生查询管理员账号。
 * </p>
 */
// 继承 JpaRepository 即可获得 save/findAll/getById/deleteById 等通用 CRUD 方法，
// Spring Data JPA 启动时通过 JDK 动态代理为该接口生成实现类（SimpleJpaRepository），无需手写实现。
// 泛型参数：<AdminUser> 表示管理的实体类型；<Long> 表示主键类型（与 AdminUser.id 字段一致）。
// 与 UserRepository 分离：前台用户与后台管理员权限模型差异大，独立实体表便于权限隔离与字段定制。
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    /**
     * 按用户名查询管理员
     *
     * @param username 用户名
     * @return 管理员（可选）
     */
    // 方法名约定解析：findBy + 字段名(Username) → Spring Data 解析为 SELECT * FROM admin_user WHERE username = ?
    // 业务场景：管理员登录鉴权、加载后台操作人信息。
    // 返回 Optional：管理员不存在时返回 empty，调用方需显式 isPresent/orElseThrow 处理。
    Optional<AdminUser> findByUsername(String username);
}
