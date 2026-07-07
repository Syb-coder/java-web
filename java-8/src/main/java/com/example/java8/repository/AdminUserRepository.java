// 声明包路径：repository 层封装数据访问逻辑，隔离 JPA 实现细节
package com.example.java8.repository;

// 导入管理员实体：仓储操作的领域对象
import com.example.java8.model.AdminUser;
// 导入 Spring Data JPA 核心接口：自动提供 save/findById/findAll/delete 等基础 CRUD
import org.springframework.data.jpa.repository.JpaRepository;

// 导入 Optional 容器：强制调用方显式处理"用户不存在"的边界情况，规避 NPE
import java.util.Optional;

/**
 * 管理员仓储
 *
 * <p>继承 JpaRepository 即可获得基础 CRUD；自定义按用户名查询用于登录校验。</p>
 *
 * <p>设计说明：
 * <ul>
 *   <li>接口无需手写实现，Spring 启动时由 JDK 动态代理生成代理类；</li>
 *   <li>方法名遵循 Spring Data JPA 命名约定（findByXxx），框架解析方法名生成 SQL；</li>
 *   <li>泛型参数 &lt;AdminUser, Long&gt; 分别表示实体类型与主键类型。</li>
 * </ul>
 * </p>
 */
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    /**
     * 根据用户名查询管理员
     *
     * <p>框架自动翻译为 {@code SELECT * FROM admin_user WHERE username = ?}。</p>
     *
     * @param username 用户名
     * @return 管理员（可选）—— 使用 Optional 显式表达"可能不存在"的语义
     */
    // 方法名约定：findBy + 字段名（首字母大写），框架据此生成 WHERE 条件
    Optional<AdminUser> findByUsername(String username);
}
