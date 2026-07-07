// 声明包路径，归类为 repository 仓储层，存放 JPA 仓储接口
package com.example.java1.repository;

// 导入借阅记录实体类，对应数据库 borrow_records 表
import com.example.java1.model.BorrowRecord;
// 导入借阅状态枚举（BORROWED / RETURNED / OVERDUE），用于按状态过滤查询
import com.example.java1.model.BorrowStatus;
// 导入 Spring Data JPA 仓储接口，继承后自动获得标准 CRUD 实现
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标记为持久层组件
import org.springframework.stereotype.Repository;

// 导入 List 集合接口，用于返回多结果查询
import java.util.List;

/**
 * 借阅记录仓储
 * <p>
 * 提供借阅记录的持久化访问能力，支持按读者、图书、状态多维查询。
 * </p>
 */
@Repository // 声明为 Spring 仓储组件，由 IoC 容器管理为单例 Bean
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> { // 泛型参数：实体类型为 BorrowRecord，主键类型为 Long

    /**
     * 按读者 ID 查询借阅记录，按借出日期倒序（最新优先）
     *
     * @param readerId 读者 ID
     * @return 借阅记录列表
     */
    // 方法名约定：findBy + ReaderId + OrderBy + BorrowDate + Desc
    // Spring Data 解析为 WHERE reader_id = ? ORDER BY borrow_date DESC，便于读者查看最新借阅
    List<BorrowRecord> findByReaderIdOrderByBorrowDateDesc(Long readerId);

    /**
     * 按读者 ID 与状态查询借阅记录
     *
     * @param readerId 读者 ID
     * @param status   借阅状态
     * @return 借阅记录列表
     */
    // 方法名中的 And 表示多条件 AND 拼接，Spring Data 生成 WHERE reader_id = ? AND status = ?
    // 用于读者中心按"借出中/已归还/逾期"标签页筛选
    List<BorrowRecord> findByReaderIdAndStatus(Long readerId, BorrowStatus status);

    /**
     * 按图书 ID 查询借阅历史
     *
     * @param bookId 图书 ID
     * @return 借阅记录列表
     */
    List<BorrowRecord> findByBookIdOrderByBorrowDateDesc(Long bookId); // 用于图书详情页展示该书的完整借阅历史轨迹

    /**
     * 按状态查询借阅记录
     *
     * @param status 借阅状态
     * @return 借阅记录列表
     */
    List<BorrowRecord> findByStatusOrderByBorrowDateDesc(BorrowStatus status); // 用于管理员按状态全局筛选借阅记录

    /**
     * 查询全部借阅记录，按借出日期倒序
     *
     * @return 借阅记录列表
     */
    List<BorrowRecord> findAllByOrderByBorrowDateDesc(); // findAll + OrderBy 组合，覆盖默认 findAll 的无序返回，保证列表展示稳定排序

    /**
     * 统计某读者处于指定状态的借阅记录数（用于借阅限额校验）
     *
     * @param readerId 读者 ID
     * @param status   借阅状态
     * @return 记录数
     */
    // countBy 约定生成 SELECT COUNT(*) WHERE ... 查询，只返回数值不加载实体，性能优于 findAll().size()
    // 借书前校验当前借出数是否已达限额（学生 5 本、教师 10 本）
    long countByReaderIdAndStatus(Long readerId, BorrowStatus status);
}
