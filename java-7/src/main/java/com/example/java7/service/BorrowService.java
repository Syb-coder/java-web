package com.example.java7.service; // 声明当前类所在的包路径，归类为 service 业务服务层

import com.example.java7.dto.BorrowRecordResponse; // 引入借阅记录响应 DTO，用于对外返回借阅信息
import com.example.java7.dto.BorrowRequest; // 引入借书请求 DTO，封装借书参数
import com.example.java7.dto.ReturnRequest; // 引入还书请求 DTO，封装还书参数
import com.example.java7.dto.StatsResponse; // 引入统计响应 DTO，用于首页数据展示
import com.example.java7.model.Book; // 引入图书实体类
import com.example.java7.model.BorrowRecord; // 引入借阅记录实体类，对应数据库 borrow_records 表
import com.example.java7.model.BorrowStatus; // 引入借阅状态枚举（BORROWED / RETURNED / OVERDUE）
import com.example.java7.repository.BorrowRecordRepository; // 引入借阅记录 JPA 仓储
import org.springframework.stereotype.Service; // 引入 @Service 注解，标记服务层组件
import org.springframework.transaction.annotation.Transactional; // 引入 @Transactional 注解，用于声明式事务管理

import java.time.LocalDate; // 引入日期类（不含时分秒），用于借出/应还/归还日期
import java.util.List; // 引入 List 集合接口
import java.util.stream.Collectors; // 引入 Stream 收集器工具

/**
 * 借阅业务服务
 * <p>
 * 职责：封装借书、还书、记录查询、统计等核心业务逻辑。
 * 关键设计：
 * 1. 借书时同步扣减图书可借数量，保证数据一致性；
 * 2. 还书时同步恢复可借数量，并更新记录状态；
 * 3. 查询时动态计算逾期标志，无需定时任务扫描（个人系统简化方案）。
 * </p>
 */
@Service // 声明为 Spring 服务组件，由 IoC 容器管理为单例 Bean
public class BorrowService {

    /** 借阅记录仓储 */
    private final BorrowRecordRepository recordRepository; // 借阅记录仓储，final 保证不可变

    /** 图书服务，用于操作图书可借数量 */
    private final BookService bookService; // 注入 BookService 以便操作图书库存，避免直接访问图书仓储造成层级穿透

    /**
     * 构造方法注入依赖
     *
     * @param recordRepository 借阅记录仓储
     * @param bookService      图书服务
     */
    public BorrowService(BorrowRecordRepository recordRepository, BookService bookService) { // 构造方法注入两个依赖
        this.recordRepository = recordRepository; // 赋值借阅记录仓储
        this.bookService = bookService; // 赋值图书服务
    }

    /**
     * 借书操作
     * <p>
     * 业务规则：
     * 1. 图书必须存在；
     * 2. 可借数量必须 > 0；
     * 3. 借出日期不能晚于应还日期。
     * 借出后可借数量减 1，状态置为 BORROWED。
     * </p>
     *
     * @param request 借书请求
     * @return 借阅记录响应
     * @throws IllegalArgumentException 参数非法（图书不存在、库存不足）
     */
    @Transactional // 开启事务，保证借阅记录写入与库存扣减的原子性，任一失败则全部回滚
    public BorrowRecordResponse borrowBook(BorrowRequest request) { // 借书业务入口方法
        Book book = bookService.getBookEntity(request.getBookId()); // 根据请求中的图书 ID 加载图书实体

        // 校验可借数量
        if (book.getAvailableCopies() <= 0) { // 校验库存：可借数量为 0 时禁止借出，防止超借
            throw new IllegalStateException("图书《" + book.getTitle() + "》已无可借副本"); // 抛出状态异常，触发 422 业务冲突响应
        }

        // 校验日期合法性
        if (request.getDueDate().isBefore(request.getBorrowDate())) { // 校验应还日期不能早于借出日期，保证时间逻辑合理
            throw new IllegalArgumentException("应还日期不能早于借出日期"); // 抛出参数异常，触发 400 响应
        }

        BorrowRecord record = new BorrowRecord(); // 创建新的借阅记录实体
        record.setBook(book); // 关联借阅的图书实体，建立外键关系
        record.setBorrowerName(request.getBorrowerName()); // 记录借阅人姓名
        record.setBorrowDate(request.getBorrowDate()); // 记录借出日期
        record.setDueDate(request.getDueDate()); // 记录应还日期，用于逾期判定
        record.setStatus(BorrowStatus.BORROWED); // 初始状态置为 BORROWED（已借出未归还）
        record.setRemark(request.getRemark()); // 记录备注信息

        // 扣减可借数量
        bookService.decreaseAvailable(book); // 调用图书服务扣减可借数量，借出与扣减在同一事务内保证一致性

        BorrowRecord saved = recordRepository.save(record); // 持久化借阅记录到数据库
        return new BorrowRecordResponse(saved); // 转换为响应 DTO 返回
    }

    /**
     * 还书操作
     * <p>
     * 业务规则：
     * 1. 借阅记录必须存在；
     * 2. 状态必须为 BORROWED 或 OVERDUE；
     * 3. 归还日期不能早于借出日期。
     * 还书后可借数量加 1，状态置为 RETURNED，记录实际归还日期。
     * </p>
     *
     * @param request 还书请求
     * @return 更新后的借阅记录响应
     * @throws IllegalArgumentException 记录不存在或状态非法
     */
    @Transactional // 开启事务，保证记录更新与库存恢复的原子性
    public BorrowRecordResponse returnBook(ReturnRequest request) { // 还书业务入口方法
        BorrowRecord record = recordRepository.findById(request.getRecordId()) // 根据记录 ID 查询借阅记录
                .orElseThrow(() -> new IllegalArgumentException( // 记录不存在则抛出参数异常
                        "借阅记录不存在: " + request.getRecordId()));

        // 已归还记录不允许重复操作
        if (record.getStatus() == BorrowStatus.RETURNED) { // 校验状态：已归还记录禁止重复还书，保证幂等性
            throw new IllegalStateException("该记录已归还，无法重复操作"); // 抛出状态异常，触发 422 响应
        }

        // 校验归还日期合法性
        if (request.getReturnDate().isBefore(record.getBorrowDate())) { // 校验归还日期不能早于借出日期，避免时间倒挂
            throw new IllegalArgumentException("归还日期不能早于借出日期"); // 抛出参数异常，触发 400 响应
        }

        record.setReturnDate(request.getReturnDate()); // 记录实际归还日期
        record.setStatus(BorrowStatus.RETURNED); // 状态更新为 RETURNED（已归还）
        // 合并备注信息（保留原备注 + 追加新备注）
        if (request.getRemark() != null && !request.getRemark().isBlank()) { // 当新备注非空时才追加，避免空字符串污染
            String existing = record.getRemark() == null ? "" : record.getRemark() + " | "; // 原备注存在则以分隔符拼接，否则视为空
            record.setRemark(existing + request.getRemark()); // 追加新备注，保留历史信息
        }

        // 恢复可借数量
        bookService.increaseAvailable(record.getBook()); // 调用图书服务恢复可借数量，与记录状态更新在同一事务内

        BorrowRecord saved = recordRepository.save(record); // 持久化更新后的借阅记录
        return new BorrowRecordResponse(saved); // 转换为响应 DTO 返回
    }

    /**
     * 查询所有借阅记录
     *
     * @return 借阅记录响应列表
     */
    public List<BorrowRecordResponse> getAllRecords() { // 查询全部借阅记录方法（只读）
        return recordRepository.findAllByOrderByBorrowDateDesc().stream() // 调用仓储按借出日期倒序查询，最新记录优先展示
                .map(BorrowRecordResponse::new) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 按状态查询借阅记录
     *
     * @param status 借阅状态
     * @return 符合状态的记录列表
     */
    public List<BorrowRecordResponse> getRecordsByStatus(BorrowStatus status) { // 按状态过滤借阅记录
        return recordRepository.findByStatusOrderByBorrowDateDesc(status).stream() // 调用仓储按状态查询并按借出日期倒序
                .map(BorrowRecordResponse::new) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 按图书 ID 查询借阅历史
     *
     * @param bookId 图书 ID
     * @return 该图书的借阅记录列表
     */
    public List<BorrowRecordResponse> getRecordsByBookId(Long bookId) { // 按图书 ID 查询借阅历史
        return recordRepository.findByBookIdOrderByBorrowDateDesc(bookId).stream() // 调用仓储按图书 ID 查询并按借出日期倒序
                .map(BorrowRecordResponse::new) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 按借阅人姓名检索
     *
     * @param name 借阅人姓名关键字
     * @return 匹配的记录列表
     */
    public List<BorrowRecordResponse> searchByBorrower(String name) { // 按借阅人姓名模糊检索
        if (name == null || name.isBlank()) { // 关键字为空时降级返回全部记录
            return getAllRecords(); // 返回全部记录列表
        }
        return recordRepository
                .findByBorrowerNameContainingIgnoreCaseOrderByBorrowDateDesc(name.trim()).stream() // 调用仓储忽略大小写模糊匹配，去除关键字首尾空白
                .map(BorrowRecordResponse::new) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 获取首页统计数据
     * <p>
     * 聚合图书总数、副本总数、可借数量、借出/归还/逾期记录数。
     * 使用聚合查询避免 N+1 性能问题；逾期数实时计算：
     * 状态为 BORROWED 且应还日期早于今天。
     * </p>
     *
     * @return 统计响应
     */
    public StatsResponse getStats() { // 首页统计聚合方法
        StatsResponse stats = new StatsResponse(); // 创建统计响应对象
        // 使用聚合查询，避免加载全部实体
        stats.setTotalBooks(bookService.getTotalBookCount()); // 设置图书种类总数（聚合 count 查询）
        stats.setTotalCopies(bookService.sumTotalCopies()); // 设置副本总数（聚合 sum 查询）
        stats.setAvailableCopies(bookService.sumAvailableCopies()); // 设置可借副本总数（聚合 sum 查询）

        List<BorrowRecord> allRecords = recordRepository.findAll(); // 加载全部借阅记录用于分类统计
        LocalDate today = LocalDate.now(); // 获取当前日期，作为逾期判定基准
        long borrowed = 0; // 初始化借出中计数器
        long returned = 0; // 初始化已归还计数器
        long overdue = 0; // 初始化逾期计数器
        for (BorrowRecord r : allRecords) { // 遍历每条记录进行分类统计
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
                case OVERDUE -> overdue++; // 状态已显式标记为逾期，逾期计数加 1
            }
        }
        stats.setBorrowedCount(borrowed); // 设置借出中数量
        stats.setReturnedCount(returned); // 设置已归还数量
        stats.setOverdueCount(overdue); // 设置逾期数量
        return stats; // 返回统计响应
    }
}
