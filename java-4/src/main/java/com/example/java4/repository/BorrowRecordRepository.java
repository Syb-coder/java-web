// 声明包路径，存放 Spring Data JPA Repository 接口
package com.example.java4.repository;

// 导入实体类与 JPA 注解
import com.example.java4.model.BorrowRecord;
import com.example.java4.model.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 借阅记录数据访问层
 * <p>
 * 提供借阅记录的 CRUD、按读者查询、按状态查询、逾期记录查询等能力。
 * </p>
 */
@Repository // 声明本接口为 Spring Bean
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    /**
     * 按读者 ID 分页查询借阅记录（用户端"我的借阅"使用）
     *
     * @param readerId 读者 ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<BorrowRecord> findByReaderIdOrderByBorrowDateDesc(Long readerId, Pageable pageable);

    /**
     * 按读者 ID 和借阅状态分页查询借阅记录
     *
     * @param readerId 读者 ID
     * @param status   借阅状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<BorrowRecord> findByReaderIdAndStatusOrderByBorrowDateDesc(Long readerId, BorrowStatus status, Pageable pageable);

    /**
     * 按借阅状态分页查询全部记录（后台管理使用）
     *
     * @param status   借阅状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<BorrowRecord> findByStatusOrderByBorrowDateDesc(BorrowStatus status, Pageable pageable);

    /**
     * 查询所有借阅中且已逾期的记录（定时任务或手动触发逾期更新使用）
     *
     * @param today    当前日期
     * @param status   借阅中状态
     * @return 逾期记录列表
     */
    List<BorrowRecord> findByDueDateBeforeAndStatus(LocalDate today, BorrowStatus status);

    /**
     * 统计指定状态的借阅记录数
     *
     * @param status 借阅状态
     * @return 记录数
     */
    long countByStatus(BorrowStatus status);

    /**
     * 统计指定读者的当前借阅中图书数量（借阅上限校验使用）
     *
     * @param readerId 读者 ID
     * @param status   借阅中状态
     * @return 借阅中数量
     */
    long countByReaderIdAndStatus(Long readerId, BorrowStatus status);

    /**
     * 查询热门借阅图书（按借阅次数降序，用于数据统计）
     *
     * @param pageable 分页参数（取前 N 条）
     * @return 图书 ID 与借阅次数的统计结果
     */
    @Query("SELECT br.book.id, COUNT(br) as borrowCount FROM BorrowRecord br GROUP BY br.book.id ORDER BY borrowCount DESC")
    List<Object[]> findHotBooks(Pageable pageable);

    /**
     * 分页查询全部借阅记录（后台管理使用，按借阅日期降序）
     *
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<BorrowRecord> findAllByOrderByBorrowDateDesc(Pageable pageable);
}
