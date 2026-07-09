// 声明包路径，存放业务服务层
package com.example.java4.service;

// 导入 DTO 与实体类
import com.example.java4.dto.StatsResponse;
import com.example.java4.model.BorrowStatus;

// 导入 Repository
import com.example.java4.repository.BookCategoryRepository;
import com.example.java4.repository.BookRepository;
import com.example.java4.repository.BorrowRecordRepository;
import com.example.java4.repository.ReaderRepository;

// 导入 Spring 工具
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计业务服务
 * <p>
 * 后台管理端首页仪表盘的数据来源，汇总馆藏、借阅、读者、热门图书等统计数据。
 * </p>
 */
@Service // 声明为 Spring 服务组件
public class StatsService {

    /** 图书仓储 */
    private final BookRepository bookRepository;

    /** 分类仓储 */
    private final BookCategoryRepository categoryRepository;

    /** 读者仓储 */
    private final ReaderRepository readerRepository;

    /** 借阅记录仓储 */
    private final BorrowRecordRepository borrowRecordRepository;

    /**
     * 构造方法注入依赖
     */
    public StatsService(BookRepository bookRepository, BookCategoryRepository categoryRepository,
                        ReaderRepository readerRepository, BorrowRecordRepository borrowRecordRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.readerRepository = readerRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    /**
     * 获取统计数据汇总
     *
     * @return 统计响应 DTO
     */
    public StatsResponse getStats() {
        StatsResponse stats = new StatsResponse();

        // ===== 馆藏统计 =====
        stats.setTotalBooks(bookRepository.count());
        // 遍历全部图书累加副本数（H2 不支持 SUM 聚合的简便方式，此处用 Java 流计算）
        long totalCopies = bookRepository.findAll().stream()
                .mapToInt(b -> b.getTotalCopies() != null ? b.getTotalCopies() : 0)
                .sum();
        stats.setTotalCopies(totalCopies);
        long availableCopies = bookRepository.findAll().stream()
                .mapToInt(b -> b.getAvailableCopies() != null ? b.getAvailableCopies() : 0)
                .sum();
        stats.setAvailableCopies(availableCopies);

        // ===== 读者统计 =====
        stats.setTotalReaders(readerRepository.count());
        stats.setStudentCount(readerRepository.countByType(
                com.example.java4.model.ReaderType.STUDENT));
        stats.setTeacherCount(readerRepository.countByType(
                com.example.java4.model.ReaderType.TEACHER));

        // ===== 借阅统计 =====
        stats.setBorrowingCount(borrowRecordRepository.countByStatus(BorrowStatus.BORROWING));
        stats.setReturnedCount(borrowRecordRepository.countByStatus(BorrowStatus.RETURNED));
        stats.setOverdueCount(borrowRecordRepository.countByStatus(BorrowStatus.OVERDUE));
        stats.setTotalBorrowRecords(borrowRecordRepository.count());

        // ===== 分类统计 =====
        stats.setCategoryStats(getCategoryStats());

        // ===== 热门图书 Top5 =====
        stats.setHotBooks(getHotBooks());

        return stats;
    }

    /**
     * 获取分类统计（分类名称 -> 图书数量）
     *
     * @return 统计列表
     */
    private List<Map<String, Object>> getCategoryStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        categoryRepository.findAllByOrderBySortOrderAsc().forEach(category -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", category.getName());
            item.put("count", bookRepository.findByCategoryId(category.getId(),
                    PageRequest.of(0, 1)).getTotalElements());
            result.add(item);
        });
        return result;
    }

    /**
     * 获取热门借阅图书 Top5
     *
     * @return 热门图书列表（书名 -> 借阅次数）
     */
    private List<Map<String, Object>> getHotBooks() {
        List<Map<String, Object>> result = new ArrayList<>();
        // 查询借阅次数最多的前 5 本图书
        List<Object[]> hotBooksData = borrowRecordRepository.findHotBooks(PageRequest.of(0, 5));
        for (Object[] row : hotBooksData) {
            Long bookId = (Long) row[0];
            Long borrowCount = (Long) row[1];
            // 查询图书信息获取书名
            bookRepository.findById(bookId).ifPresent(book -> {
                Map<String, Object> item = new HashMap<>();
                item.put("title", book.getTitle());
                item.put("author", book.getAuthor());
                item.put("count", borrowCount);
                result.add(item);
            });
        }
        return result;
    }
}
