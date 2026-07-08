package com.example.java3.repository;

import com.example.java3.model.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 商品点赞仓储
 */
@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    /**
     * 查询用户是否已点赞某商品
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     * @return 点赞记录 Optional
     */
    Optional<ProductLike> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * 统计商品被点赞次数
     *
     * @param productId 商品 ID
     * @return 数量
     */
    long countByProductId(Long productId);
}
