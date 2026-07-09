package com.example.java9.repository; // 声明 Repository 层包路径

import com.example.java9.model.FinancialProduct; // 引入理财产品实体，对应 financial_products 表
import com.example.java9.model.ProductStatus; // 引入产品状态枚举（ON_SALE/OFF_SHELF 等）
import com.example.java9.model.ProductType; // 引入产品类型枚举（DEPOSIT/FUND/INSURANCE）
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JPA 仓储基础接口
import org.springframework.stereotype.Repository; // 引入 @Repository 注解

import java.util.List; // 引入 List 容器

/**
 * 理财产品 Repository
 */
@Repository // 标识为持久层 Bean
public interface FinancialProductRepository extends JpaRepository<FinancialProduct, Long> { // 继承 JPA，主键 Long

    /** 根据状态查询产品列表 */
    List<FinancialProduct> findByStatus(ProductStatus status); // C 端首页拉取在售产品；运营后台查看下架产品

    /** 根据类型查询产品列表 */
    List<FinancialProduct> findByType(ProductType type); // C 端按存款/基金/保险分类浏览产品
}
