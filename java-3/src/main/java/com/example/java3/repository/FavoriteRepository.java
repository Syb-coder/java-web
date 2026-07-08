package com.example.java3.repository;

import com.example.java3.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 商品收藏仓储
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * 查询用户是否已收藏某商品
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     * @return 收藏记录 Optional
     */
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * 查询用户的收藏列表
     *
     * @param userId 用户 ID
     * @return 收藏列表
     */
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 删除用户对某商品的收藏
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);

    /**
     * 统计商品被收藏次数
     *
     * @param productId 商品 ID
     * @return 数量
     */
    long countByProductId(Long productId);
}
