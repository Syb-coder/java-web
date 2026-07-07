// 声明包路径
package com.example.java2.repository;

// 导入实体类与 Spring Data JPA 接口
import com.example.java2.model.TestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 答题记录仓储
 * <p>
 * 继承 {@link JpaRepository} 自动获得 CRUD 能力。
 * 提供按用户查询答题历史等派生查询。
 * </p>
 */
// 继承 JpaRepository 即可获得 save/findAll/getById/deleteById 等通用 CRUD 方法，
// Spring Data JPA 启动时通过 JDK 动态代理生成 SimpleJpaRepository 实现类，无需手写 Impl。
// 泛型参数：<TestRecord> 表示管理的实体类型；<Long> 表示主键类型（与 TestRecord.id 字段一致）。
public interface TestRecordRepository extends JpaRepository<TestRecord, Long> {

    /**
     * 按用户查询答题记录，按时间倒序分页返回
     *
     * @param userId   用户 ID
     * @param pageable 分页参数
     * @return 答题记录分页
     */
    // 方法名约定解析：findBy + 字段(UserId) + OrderBy + 字段(CreateTime) + 关键字(Desc)
    //   → Spring Data 解析为 SELECT * FROM test_record WHERE user_id = ? ORDER BY create_time DESC
    // 业务场景：个人中心"答题历史"列表，最近一次答题排在最前。
    // 返回 Page：长期使用会累积大量记录，必须分页避免一次性加载全表。
    Page<TestRecord> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 统计某用户的答题次数
     *
     * @param userId 用户 ID
     * @return 答题次数
     */
    // 方法名约定解析：countBy + 字段名(UserId)
    //   → Spring Data 解析为 SELECT COUNT(*) FROM test_record WHERE user_id = ?
    // 业务场景：个人中心展示"累计答题 N 次"统计指标、排行榜数据。
    long countByUserId(Long userId);
}
