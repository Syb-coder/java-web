// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 User 实体类，对应学生用户表
import com.example.java3.model.User;
// 导入 UserStatus 枚举类，定义用户状态（正常、禁用等）
import com.example.java3.model.UserStatus;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 Optional 容器类，用于安全包装可能为 null 的单条查询结果
import java.util.Optional;

/**
 * 学生用户仓储
 * <p>
 * 通过 Spring Data JPA 自动生成实现，提供按学号、用户名查询等能力。
 * </p>
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 按学号查询用户
     *
     * @param studentId 学号
     * @return 用户 Optional
     */
    // 按学号 studentId 查询用户，用于实名认证登录场景
    // 返回 Optional 防止空指针，学号在系统中唯一
    Optional<User> findByStudentId(String studentId);

    /**
     * 按用户名查询用户
     *
     * @param username 用户名
     * @return 用户 Optional
     */
    // 按用户名 username 查询用户，用于用户名登录场景
    // 返回 Optional 防止空指针
    Optional<User> findByUsername(String username);

    /**
     * 判断学号是否已存在
     *
     * @param studentId 学号
     * @return 是否存在
     */
    // 校验学号是否已被注册，注册时用于唯一性校验
    // 返回 boolean，true 表示已存在
    boolean existsByStudentId(String studentId);

    /**
     * 判断用户名是否已存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    // 校验用户名是否已被注册，注册时用于唯一性校验
    // 返回 boolean，true 表示已存在
    boolean existsByUsername(String username);

    /**
     * 按状态统计用户数量
     *
     * @param status 用户状态
     * @return 数量
     */
    // 按 UserStatus 枚举值统计用户数量，用于后台仪表盘展示正常/禁用用户数
    long countByStatus(UserStatus status);
}
