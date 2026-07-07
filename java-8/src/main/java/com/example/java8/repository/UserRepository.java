// 声明包路径：repository 层负责实体与数据库的交互抽象
package com.example.java8.repository;

// 导入前台用户实体：区别于 AdminUser，面向 C 端消费者
import com.example.java8.model.User;
// 导入 Spring Data JPA 基础接口：提供开箱即用的 CRUD 与分页能力
import org.springframework.data.jpa.repository.JpaRepository;

// 导入 Optional：用于包装可能为 null 的查询结果，避免 NPE
import java.util.Optional;

/**
 * 前台用户仓储
 *
 * <p>负责前台注册用户的持久化访问。与 AdminUserRepository 分离，
 * 体现"权限隔离"的设计原则——后台管理员与前台用户使用不同的 Session 键，
 * 避免权限越权。</p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     *
     * <p>用于前台登录校验与注册时用户名重复检测。
     * 框架自动翻译为 {@code SELECT * FROM user WHERE username = ?}。</p>
     *
     * @param username 用户名
     * @return 用户（可选）
     */
    // 方法名约定findByUsername，框架解析后自动生成基于 username 字段的查询
    Optional<User> findByUsername(String username);
}
