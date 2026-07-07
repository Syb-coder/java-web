// 声明包路径，归类为 repository 仓储层，存放 JPA 仓储接口
package com.example.java1.repository;

// 导入读者实体类，对应数据库 readers 表
import com.example.java1.model.Reader;
// 导入 Spring Data JPA 仓储接口，继承后自动获得标准 CRUD 实现
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标记为持久层组件
import org.springframework.stereotype.Repository;

// 导入 List 集合接口，用于返回多结果查询
import java.util.List;
// 导入 Optional 包装类型，强制调用方显式处理空值
import java.util.Optional;

/**
 * 读者仓储
 * <p>
 * 提供读者实体的持久化访问能力。通过方法名约定声明自定义查询。
 * </p>
 */
@Repository // 声明为 Spring 仓储组件，由 IoC 容器管理为单例 Bean
public interface ReaderRepository extends JpaRepository<Reader, Long> { // 泛型参数：实体类型为 Reader，主键类型为 Long

    /**
     * 根据学号/工号查询读者（用于登录校验）
     *
     * @param readerNo 学号/工号
     * @return 读者（可能为空）
     */
    // 方法名约定：findBy + ReaderNo，Spring Data 生成 WHERE reader_no = ? 查询
    // 读者登录时以学号/工号作为账号，需据此定位读者记录以校验密码
    Optional<Reader> findByReaderNo(String readerNo);

    /**
     * 按姓名模糊检索读者
     * <p>ContainingIgnoreCase 表示忽略大小写的 LIKE %keyword% 查询。</p>
     *
     * @param name 姓名关键字
     * @return 匹配的读者列表
     */
    // Containing 对应 LIKE %keyword%（两端模糊），IgnoreCase 在 SQL 层转换为 LOWER() 比较
    // 用于管理员检索读者，支持部分匹配以适应"只记姓或名"的检索场景
    List<Reader> findByNameContainingIgnoreCase(String name);

    /**
     * 检查学号/工号是否已存在（用于注册时唯一性校验）
     *
     * @param readerNo 学号/工号
     * @return true 已存在
     */
    // existsBy 约定生成 SELECT EXISTS 查询，只返回布尔值不加载实体，性能优于 findByXxx().isPresent()
    // 新建读者前校验学号/工号唯一性，避免数据库唯一约束异常向上抛出
    boolean existsByReaderNo(String readerNo);
}
