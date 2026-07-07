package com.example.java7.repository; // 声明包路径，归属 repository 仓储层

import com.example.java7.model.BorrowRecord; // 引入 BorrowRecord 实体类，作为 JpaRepository 的泛型参数
import com.example.java7.model.BorrowStatus; // 引入借阅状态枚举，用于按状态查询
import org.springframework.data.jpa.repository.JpaRepository; // 引入 JpaRepository 接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.Query; // 引入 @Query 注解，用于声明自定义 JPQL 查询
import org.springframework.data.repository.query.Param; // 引入 @Param 注解，用于绑定命名参数到查询语句
import org.springframework.stereotype.Repository; // 引入 @Repository 注解，声明为 Spring 仓储组件

import java.util.List; // 引入 List 集合，作为查询方法的返回类型

/**
 * 借阅记录仓储接口
 * <p>
 * 职责：提供 BorrowRecord 实体的 CRUD 与状态查询能力。
 * 通过方法名派生查询和 @Query 自定义查询满足业务需求。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>方法名派生查询利用 Spring Data JPA 的关键字解析（如 OrderBy、ContainingIgnoreCase）</li>
 *   <li>复杂多条件查询使用 @Query 显式声明 JPQL，提升可读性与可维护性</li>
 *   <li>统计方法 countByStatus 用于首页数据聚合</li>
 * </ul>
 * </p>
 *
 * @author example
 * @see JpaRepository
 */
@Repository // 声明为 Spring 仓储组件，启用持久层异常转换（将原生 JPA 异常转换为 DataAccessException 体系）
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> { // 继承 JpaRepository，指定实体类型为 BorrowRecord、主键类型为 Long

    /**
     * 按图书 ID 查询借阅历史
     * <p>
     * 方法名派生查询：findByBookId 表示按 bookId 字段查询，
     * OrderByBorrowDateDesc 表示按 borrowDate 倒序排列，
     * 最近借阅记录排在前面，便于查看最新动态。
     * </p>
     *
     * @param bookId 图书 ID
     * @return 该图书的借阅记录列表
     */
    List<BorrowRecord> findByBookIdOrderByBorrowDateDesc(Long bookId); // 派生查询：按 bookId 等值查询，结果按 borrowDate 降序

    /**
     * 按状态查询借阅记录
     * <p>
     * 方法名派生查询：findByStatus 表示按 status 字段查询，
     * OrderByBorrowDateDesc 表示按借出日期倒序排列，
     * 便于运营人员优先处理最新记录。
     * </p>
     *
     * @param status 借阅状态
     * @return 符合状态的记录列表
     */
    List<BorrowRecord> findByStatusOrderByBorrowDateDesc(BorrowStatus status); // 派生查询：按 status 等值查询，结果按 borrowDate 降序

    /**
     * 按借阅人姓名模糊查询
     * <p>
     * 方法名派生查询：findByBorrowerNameContainingIgnoreCase 表示
     * 对 borrowerName 字段进行忽略大小写的包含匹配（等价于 LIKE '%xxx%' 忽略大小写），
     * OrderByBorrowDateDesc 按借出日期倒序排列。
     * </p>
     *
     * @param borrowerName 借阅人姓名关键字
     * @return 匹配的记录列表
     */
    List<BorrowRecord> findByBorrowerNameContainingIgnoreCaseOrderByBorrowDateDesc(String borrowerName); // 派生查询：姓名忽略大小写模糊匹配，结果按 borrowDate 降序

    /**
     * 查询所有借阅记录，按借出日期倒序
     * <p>
     * 方法名派生查询：findAllByOrderByBorrowDateDesc 表示查询全部记录，
     * 按借出日期倒序排列，最近的借阅记录排在最前。
     * </p>
     *
     * @return 全部借阅记录
     */
    List<BorrowRecord> findAllByOrderByBorrowDateDesc(); // 派生查询：查询全部记录，按 borrowDate 降序

    /**
     * 统计指定状态的记录数
     * <p>
     * 方法名派生查询：countByStatus 表示按 status 字段统计记录数，
     * 用于首页统计面板展示借出中、已归还、逾期等数量。
     * </p>
     *
     * @param status 借阅状态
     * @return 记录数量
     */
    long countByStatus(BorrowStatus status); // 派生查询：按 status 统计记录数，返回 long 类型

    /**
     * 查询某本书当前未归还的借阅记录
     * <p>
     * 用于判断图书是否可借：若存在状态为 BORROWED 或 OVERDUE 的记录，
     * 表示该书仍有副本未归还，需结合 availableCopies 综合判断。
     * </p>
     * <p>
     * JPQL 解析：
     * <ul>
     *   <li>SELECT r FROM BorrowRecord r：查询 BorrowRecord 实体，别名 r</li>
     *   <li>WHERE r.book.id = :bookId：关联 Book 实体，按图书 ID 过滤</li>
     *   <li>AND r.status = :status：按借阅状态过滤（BORROWED 或 OVERDUE）</li>
     * </ul>
     * </p>
     *
     * @param bookId 图书 ID
     * @param status 借阅状态
     * @return 未归还记录列表
     */
    @Query("SELECT r FROM BorrowRecord r WHERE r.book.id = :bookId AND r.status = :status") // JPQL：通过关联查询 book.id 与 status 双条件过滤
    List<BorrowRecord> findActiveRecordsByBook(@Param("bookId") Long bookId, // 方法入参：@Param 绑定 bookId 到 JPQL 中的 :bookId
                                               @Param("status") BorrowStatus status); // 方法入参：@Param 绑定 status 到 JPQL 中的 :status
}
