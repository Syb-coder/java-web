// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 Comment 实体类，对应商品评论表
import com.example.java3.model.Comment;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 List 集合类，用于承载多条查询结果
import java.util.List;

/**
 * 商品评论仓储
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 按商品查询评论（按时间升序，便于展示对话顺序）
     *
     * @param productId 商品 ID
     * @return 评论列表
     */
    // 按商品 ID 查询该商品的全部评论，按 createdAt 升序排列
    // 升序排列便于前端按发布时间展示评论对话顺序，呈现完整的讨论脉络
    List<Comment> findByProductIdOrderByCreatedAtAsc(Long productId);
}
