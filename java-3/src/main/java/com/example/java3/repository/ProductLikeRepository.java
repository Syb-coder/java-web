// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 ProductLike 实体类，对应商品点赞表
import com.example.java3.model.ProductLike;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 Optional 容器类，用于安全包装可能为 null 的单条查询结果
import java.util.Optional;

/**
 * 商品点赞仓储
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现
@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    /**
     * 查询用户是否已点赞某商品
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     * @return 点赞记录 Optional
     */
    // 按 userId 和 productId 联合查询点赞记录，用于判断用户是否已点赞该商品
    // 返回 Optional，存在即表示已点赞，可避免重复点赞
    Optional<ProductLike> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * 统计商品被点赞次数
     *
     * @param productId 商品 ID
     * @return 数量
     */
    // 按 productId 统计该商品被点赞的总次数，用于商品热度展示
    long countByProductId(Long productId);
}
