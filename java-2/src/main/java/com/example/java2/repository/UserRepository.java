// 声明包路径
package com.example.java2.repository;

// 导入实体类与 Spring Data JPA 接口
import com.example.java2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 导入 Optional 类型
import java.util.Optional;

/**
 * 前台用户仓储
 * <p>
 * 继承 {@link JpaRepository} 自动获得 CRUD 能力，无需手写 SQL。
 * 通过方法名约定派生查询：findByUsername 即"按用户名查询"。
 * </p>
 */
// 继承 JpaRepository 即可获得 save/findAll/getById/deleteById 等通用 CRUD 方法，
// Spring Data JPA 启动时通过 JDK 动态代理为该接口生成实现类（SimpleJpaRepository），
// 因此无需手写 Impl 实现类即可直接注入使用。
// 泛型参数：<User> 表示当前仓储管理的实体类型；<Long> 表示主键类型（与 User.id 字段一致）。
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 按用户名查询用户
     *
     * @param username 用户名
     * @return 用户（可选）
     */
    // 方法名约定解析：findBy + 字段名(Username) → Spring Data 解析为 SELECT * FROM user WHERE username = ?
    // 业务场景：登录时按用户名定位账号、加载用户详情。
    // 返回 Optional 而非 User/null：避免调用方忘记判空导致 NPE，强制调用方显式处理"未找到"场景。
    Optional<User> findByUsername(String username);

    /**
     * 判断用户名是否已存在（注册时去重）
     *
     * @param username 用户名
     * @return true 已存在
     */
    // 方法名约定解析：existsBy + 字段名(Username) → Spring Data 解析为 SELECT COUNT(*) > 0 FROM user WHERE username = ?
    // 比先 findByUsername 再判空更高效：数据库仅返回布尔值，无需加载整行实体。
    // 业务场景：注册接口校验用户名是否被占用。
    boolean existsByUsername(String username);
}
