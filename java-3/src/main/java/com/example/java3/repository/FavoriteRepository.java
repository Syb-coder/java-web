// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 Favorite 实体类，对应商品收藏表
import com.example.java3.model.Favorite;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 List 集合类，用于承载多条查询结果
import java.util.List;
// 导入 Optional 容器类，用于安全包装可能为 null 的单条查询结果
import java.util.Optional;

/**
 * 商品收藏仓储
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * 查询用户是否已收藏某商品
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     * @return 收藏记录 Optional
     */
    // 按 userId 和 productId 联合查询收藏记录，用于判断用户是否已收藏某商品
    // 返回 Optional，存在即表示已收藏，可避免重复收藏
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * 查询用户的收藏列表
     *
     * @param userId 用户 ID
     * @return 收藏列表
     */
    // 查询用户的全部收藏记录，按 createdAt 降序排列，便于展示最新收藏在前
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 删除用户对某商品的收藏
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     */
    // 按 userId 和 productId 联合条件删除收藏记录，用于取消收藏操作
    // 返回 void，调用方无需关注删除条数
    void deleteByUserIdAndProductId(Long userId, Long productId);

    /**
     * 统计商品被收藏次数
     *
     * @param productId 商品 ID
     * @return 数量
     */
    // 按 productId 统计该商品被收藏的总次数，用于商品热度展示
    long countByProductId(Long productId);
}
