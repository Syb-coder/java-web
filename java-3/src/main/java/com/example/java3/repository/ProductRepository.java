package com.example.java3.repository;

import com.example.java3.model.Product;
import com.example.java3.model.ProductAuditStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品仓储
 * <p>
 * 提供商品查询、按状态/分类/卖家筛选、统计等能力。
 * </p>
 */
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
    @Query("SELECT p FROM Product p WHERE (p.title LIKE %:keyword% OR p.description LIKE %:keyword%) " +
            "AND p.auditStatus = :auditStatus AND p.sold = :sold")
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
    List<Product> findBySellerIdOrderByCreatedAtDesc(Long sellerId);

    /**
     * 按审核状态查询商品（后台审核列表）
     *
     * @param auditStatus 审核状态
     * @return 商品列表
     */
    List<Product> findByAuditStatusOrderByCreatedAtDesc(ProductAuditStatus auditStatus);

    /**
     * 统计各审核状态的商品数量
     *
     * @param auditStatus 审核状态
     * @return 数量
     */
    long countByAuditStatus(ProductAuditStatus auditStatus);

    /**
     * 按分类统计商品数量（仅已通过且未售出）
     *
     * @param categoryId 分类 ID
     * @return 数量
     */
    long countByCategoryIdAndAuditStatusAndSold(Long categoryId, ProductAuditStatus auditStatus, Boolean sold);
}
