// 声明当前类所在的包路径，归类为 service 业务服务层
package com.example.java1.service;

import com.example.java1.dto.StatsResponse; // 引入统计响应 DTO，用于首页数据看板
import com.example.java1.model.BorrowRecord; // 引入借阅记录实体类
import com.example.java1.model.BorrowStatus; // 引入借阅状态枚举（BORROWED / RETURNED / OVERDUE）
import com.example.java1.repository.BorrowRecordRepository; // 引入借阅记录 JPA 仓储
import org.springframework.stereotype.Service; // 引入 @Service 注解，标记服务层组件

import java.time.LocalDate; // 引入日期类，用于逾期判定基准
import java.util.List; // 引入 List 集合接口

/**
 * 统计业务服务
 * <p>
 * 聚合图书、读者、借阅的核心指标，供首页数据看板展示。
 * 使用聚合查询与内存遍历相结合的方式，平衡性能与代码复杂度。
 * </p>
 */
@Service // 声明为 Spring 服务组件，由 IoC 容器管理为单例 Bean
public class StatsService {

    private final BookService bookService; // 注入图书服务，用于获取图书相关聚合指标
    private final ReaderService readerService; // 注入读者服务，用于获取读者总数
    private final BorrowRecordRepository recordRepository; // 借阅记录仓储，用于加载借阅记录进行分类统计

    /**
     * 构造方法注入
     */
    public StatsService(BookService bookService, ReaderService readerService, // 注入图书服务
                        BorrowRecordRepository recordRepository) { // 注入读者服务与借阅记录仓储
        this.bookService = bookService; // 赋值图书服务
        this.readerService = readerService; // 赋值读者服务
        this.recordRepository = recordRepository; // 赋值借阅记录仓储
    }

    /**
     * 获取首页统计数据
     * <p>
     * 指标说明：
     * <ul>
     *   <li>totalBooks：图书种类总数；</li>
     *   <li>totalCopies：副本总数（聚合 sum）；</li>
     *   <li>availableCopies：可借副本总数；</li>
     *   <li>totalReaders：读者总数；</li>
     *   <li>borrowedCount：当前借出中（含逾期）；</li>
     *   <li>returnedCount：已归还数量；</li>
     *   <li>overdueCount：逾期数量（状态为 BORROWED 且应还日期早于今天）；</li>
     *   <li>totalFine：累计罚款金额。</li>
     * </ul>
     * </p>
     *
     * @return 统计响应
     */
    public StatsResponse getStats() { // 首页统计聚合方法（只读）
        long totalBooks = bookService.getTotalBookCount(); // 图书种类总数（聚合 count 查询）
        long totalCopies = bookService.sumTotalCopies(); // 副本总数（聚合 sum 查询）
        long availableCopies = bookService.sumAvailableCopies(); // 可借副本总数（聚合 sum 查询）
        long totalReaders = readerService.getTotalReaderCount(); // 读者总数（聚合 count 查询）

        // 遍历全部记录分类统计
        List<BorrowRecord> allRecords = recordRepository.findAll(); // 加载全部借阅记录用于分类统计
        LocalDate today = LocalDate.now(); // 获取当前日期，作为逾期判定基准
        long borrowed = 0; // 初始化借出中计数器
        long returned = 0; // 初始化已归还计数器
        long overdue = 0; // 初始化逾期计数器
        double totalFine = 0.0; // 初始化累计罚款金额

        for (BorrowRecord r : allRecords) { // 遍历每条记录进行分类统计
            // 累计罚款（已归还的罚款已结算，未归还的罚款为 0）
            if (r.getFine() != null) { // 罚款非空时累计，避免 NPE
                totalFine += r.getFine(); // 累加罚款金额
            }
            switch (r.getStatus()) { // 根据借阅状态分支处理
                case BORROWED -> { // 状态为已借出未归还
                    // 应还日期早于今天则视为逾期
                    if (r.getDueDate() != null && today.isAfter(r.getDueDate())) { // 应还日期非空且当前日期已超过应还日期，判定为逾期
                        overdue++; // 逾期计数加 1
                    } else {
                        borrowed++; // 未逾期则计入正常借出计数
                    }
                }
                case RETURNED -> returned++; // 状态为已归还，已归还计数加 1
                case OVERDUE -> { // 状态为归还时已超期
                    overdue++; // 逾期计数加 1
                    // OVERDUE 状态表示归还时已超期，已计入罚款
                }
            }
        }

        return new StatsResponse( // 构造统计响应对象返回
                totalBooks, totalCopies, availableCopies, totalReaders, // 图书与读者指标
                borrowed, returned, overdue, totalFine // 借阅与罚款指标
        );
    }
}
