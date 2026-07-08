// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 Product 实体类，对应商品表
import com.example.java3.model.Product;
// 导入 ProductAuditStatus 枚举类，定义商品审核状态（待审核、通过、驳回等）
import com.example.java3.model.ProductAuditStatus;
// 导入 Page 分页结果容器，承载分页查询的数据与分页元信息
import org.springframework.data.domain.Page;
// 导入 Pageable 分页参数接口，封装页码、页大小、排序等信息
import org.springframework.data.domain.Pageable;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Query 注解，用于自定义 JPQL/SQL 查询语句
import org.springframework.data.jpa.repository.Query;
// 导入 @Param 注解，用于将方法参数绑定到查询语句中的命名参数
import org.springframework.data.repository.query.Param;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 List 集合类，用于承载多条查询结果
import java.util.List;

/**
 * 商品仓储
 * <p>
 * 提供商品查询、按状态/分类/卖家筛选、统计等能力。
 * </p>
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 分页查询已审核通过且未售出的商品（前台展示用）
     *
     * @param auditStatus 审核状态
     * @param sold        是否售出
     * @param pageable    分页参数
     * @return 商品分页
     */
    // 按 auditStatus 和 sold 联合查询商品，用于前台商品列表展示
    // Pageable 参数支持分页与排序，返回 Page 包含当前页数据及总页数等元信息
    Page<Product> findByAuditStatusAndSold(ProductAuditStatus auditStatus, Boolean sold, Pageable pageable);

    /**
     * 按分类分页查询已审核通过且未售出的商品
     *
     * @param auditStatus 审核状态
     * @param sold        是否售出
     * @param categoryId  分类 ID
     * @param pageable    分页参数
     * @return 商品分页
     */
    // 按 auditStatus、sold、categoryId 三个条件联合分页查询商品
    // 用于前台按分类浏览商品时筛选已通过审核且仍在售的商品
    Page<Product> findByAuditStatusAndSoldAndCategoryId(ProductAuditStatus auditStatus, Boolean sold,
                                                        Long categoryId, Pageable pageable);

    /**
     * 关键词搜索（标题或描述模糊匹配），仅返回已审核通过且未售出的商品
     *
     * @param keyword     关键词
     * @param auditStatus 审核状态
     * @param sold        是否售出
     * @param pageable    分页参数
     * @return 商品分页
     */
    // 使用 @Query 注解自定义 JPQL 查询，实现关键词模糊匹配
    // 查询逻辑：title 或 description 任一字段 LIKE 关键词，且审核状态匹配且未售出
    // LIKE %:keyword% 表示前后模糊匹配，:keyword 为命名参数
    @Query("SELECT p FROM Product p WHERE (p.title LIKE %:keyword% OR p.description LIKE %:keyword%) " +
            "AND p.auditStatus = :auditStatus AND p.sold = :sold")
    // searchByKeyword 方法通过 @Param 注解将参数绑定到 JPQL 命名参数 :keyword / :auditStatus / :sold
    Page<Product> searchByKeyword(@Param("keyword") String keyword,
                                  @Param("auditStatus") ProductAuditStatus auditStatus,
                                  @Param("sold") Boolean sold,
                                  Pageable pageable);

    /**
     * 按卖家查询商品（我的发布）
     *
     * @param sellerId 卖家 ID
     * @return 商品列表
     */
    // 按卖家 ID 查询其发布的全部商品，按 createdAt 降序排列，用于"我的发布"列表
    List<Product> findBySellerIdOrderByCreatedAtDesc(Long sellerId);

    /**
     * 按审核状态查询商品（后台审核列表）
     *
     * @param auditStatus 审核状态
     * @return 商品列表
     */
    // 按审核状态查询商品列表，按 createdAt 降序排列，用于后台管理员审核工作台
    List<Product> findByAuditStatusOrderByCreatedAtDesc(ProductAuditStatus auditStatus);

    /**
     * 统计各审核状态的商品数量
     *
     * @param auditStatus 审核状态
     * @return 数量
     */
    // 按 auditStatus 统计对应状态的商品数量，用于后台仪表盘展示待审核/已通过等数量
    long countByAuditStatus(ProductAuditStatus auditStatus);

    /**
     * 按分类统计商品数量（仅已通过且未售出）
     *
     * @param categoryId 分类 ID
     * @return 数量
     */
    // 按 categoryId、auditStatus、sold 三个条件联合统计商品数量
    // 用于前台分类页展示该分类下在售商品的数量
    long countByCategoryIdAndAuditStatusAndSold(Long categoryId, ProductAuditStatus auditStatus, Boolean sold);
}
