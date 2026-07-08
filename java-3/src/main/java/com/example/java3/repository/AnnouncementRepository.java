// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 Announcement 实体类，对应系统公告表
import com.example.java3.model.Announcement;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 List 集合类，用于承载多条查询结果
import java.util.List;

/**
 * 公告仓储
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    /**
     * 查询全部公告，置顶优先，再按时间倒序
     *
     * @return 公告列表
     */
    // 查询全部公告，方法名约定 ByOrderByPinnedDescCreatedAtDesc 表示：
    // 先按 pinned 字段降序（置顶的在前面），再按 createdAt 字段降序（新的在前面）
    // 返回 List 集合，便于前端按顺序展示公告列表
    List<Announcement> findAllByOrderByPinnedDescCreatedAtDesc();
}
