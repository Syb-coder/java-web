package com.example.java3.repository;

import com.example.java3.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 商品分类仓储
 */
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    /**
     * 按排序序号升序查询全部分类
     *
     * @return 分类列表
     */
    List<ProductCategory> findAllByOrderBySortOrderAsc();

    /**
     * 按名称查询分类
     *
     * @param name 分类名称
     * @return 分类 Optional
     */
    Optional<ProductCategory> findByName(String name);
}
