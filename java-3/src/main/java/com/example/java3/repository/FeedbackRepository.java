// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 Feedback 实体类，对应意见反馈表
import com.example.java3.model.Feedback;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 List 集合类，用于承载多条查询结果
import java.util.List;

/**
 * 意见反馈仓储
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * 查询全部反馈，按时间倒序
     *
     * @return 反馈列表
     */
    // 查询全部反馈记录，按 createdAt 降序排列，便于管理员优先查看最新反馈
    List<Feedback> findAllByOrderByCreatedAtDesc();

    /**
     * 查询用户提交的反馈
     *
     * @param userId 用户 ID
     * @return 反馈列表
     */
    // 按 userId 查询该用户提交的全部反馈，按 createdAt 降序排列
    // 用于用户查看自己历史反馈记录及处理状态
    List<Feedback> findByUserIdOrderByCreatedAtDesc(Long userId);
}
