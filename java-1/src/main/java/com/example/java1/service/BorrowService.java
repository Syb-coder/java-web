// 声明当前类所在的包路径，归类为 service 业务服务层
package com.example.java1.service;

import com.example.java1.dto.BorrowRecordResponse; // 引入借阅记录响应 DTO，用于对外返回借阅信息
import com.example.java1.dto.BorrowRequest; // 引入借书请求 DTO，封装借书参数
import com.example.java1.dto.RenewRequest; // 引入续借请求 DTO，封装续借参数
import com.example.java1.dto.ReturnRequest; // 引入还书请求 DTO，封装还书参数
import com.example.java1.model.Book; // 引入图书实体类
import com.example.java1.model.BorrowRecord; // 引入借阅记录实体类，对应数据库 borrow_record 表
import com.example.java1.model.BorrowStatus; // 引入借阅状态枚举（BORROWED / RETURNED / OVERDUE）
import com.example.java1.model.Reader; // 引入读者实体类
import com.example.java1.model.ReaderType; // 引入读者类型枚举及常量（学生/教师，借阅上限与借期）
import com.example.java1.repository.BorrowRecordRepository; // 引入借阅记录 JPA 仓储
import org.springframework.stereotype.Service; // 引入 @Service 注解，标记服务层组件
import org.springframework.transaction.annotation.Transactional; // 引入 @Transactional 注解，用于声明式事务管理

import java.time.LocalDate; // 引入日期类（不含时分秒），用于借出/应还/归还日期
import java.util.List; // 引入 List 集合接口
import java.util.stream.Collectors; // 引入 Stream 收集器工具

/**
 * 借阅业务服务
 * <p>
 * 封装借书、还书、续借、查询等核心业务逻辑。关键设计：
 * <ul>
 *   <li>借书时校验读者借阅上限（按 ReaderType 区分学生/教师）；</li>
 *   <li>借书时同步扣减图书可借数量与读者借阅计数，保证一致性；</li>
 *   <li>还书时计算超期罚款（0.5 元/天）；</li>
 *   <li>续借限制最多 1 次，延长 15 天。</li>
 * </ul>
 * </p>
 */
@Service // 声明为 Spring 服务组件，由 IoC 容器管理为单例 Bean
public class BorrowService {

    /** 借阅记录仓储 */
    private final BorrowRecordRepository recordRepository; // 借阅记录仓储，final 保证不可变
    /** 图书服务，用于操作图书库存 */
    private final BookService bookService; // 注入 BookService 以便操作图书库存，避免直接访问图书仓储造成层级穿透
    /** 读者服务，用于操作读者借阅计数 */
    private final ReaderService readerService; // 注入 ReaderService 以便操作读者借阅计数

    /**
     * 构造方法注入
     */
    public BorrowService(BorrowRecordRepository recordRepository, // 注入借阅记录仓储
                         BookService bookService, // 注入图书服务
                         ReaderService readerService) { // 注入读者服务
        this.recordRepository = recordRepository; // 赋值借阅记录仓储
        this.bookService = bookService; // 赋值图书服务
        this.readerService = readerService; // 赋值读者服务
    }

    /**
     * 借书操作
     * <p>
     * 业务规则：
     * 1. 图书必须存在且有可借副本；
     * 2. 读者必须存在；
     * 3. 读者当前借阅数不能超过其类型上限（学生 5 本，教师 10 本）；
     * 4. 借期按读者类型确定（学生 30 天，教师 60 天）。
     * </p>
     *
     * @param request 借书请求
     * @return 借阅记录响应
     * @throws IllegalArgumentException 图书或读者不存在
     * @throws IllegalStateException    库存不足或超出借阅上限
     */
    @Transactional // 开启事务，保证借阅记录写入、库存扣减、读者计数更新的原子性
    public BorrowRecordResponse borrowBook(BorrowRequest request) { // 借书业务入口方法
        Book book = bookService.getBookEntity(request.bookId()); // 根据请求中的图书 ID 加载图书实体
        Reader reader = readerService.getReaderEntity(request.readerId()); // 根据请求中的读者 ID 加载读者实体

        // 校验图书可借数量
        if (book.getAvailableCopies() <= 0) { // 校验库存：可借数量为 0 时禁止借出，防止超借
            throw new IllegalStateException("图书《" + book.getTitle() + "》已无可借副本"); // 抛出状态异常，触发 422 业务冲突响应
        }

        // 校验读者借阅上限
        int maxBorrow = ReaderType.maxBorrow(reader.getType()); // 按读者类型获取借阅上限（学生 5 本，教师 10 本）
        long currentBorrowed = recordRepository.countByReaderIdAndStatus(reader.getId(), BorrowStatus.BORROWED); // 统计该读者当前借出中记录数
        if (currentBorrowed >= maxBorrow) { // 当前借阅数已达上限，防止超出借阅额度
            throw new IllegalStateException(
                    reader.getName() + " 已达借阅上限（" + maxBorrow + " 本），请先归还部分图书"); // 抛出状态异常，触发 422 响应
        }

        // 计算借期与应还日期
        int loanDays = ReaderType.loanDays(reader.getType()); // 按读者类型获取借期（学生 30 天，教师 60 天）
        LocalDate today = LocalDate.now(); // 获取当前日期作为借出日期

        BorrowRecord record = new BorrowRecord(); // 创建新的借阅记录实体
        record.setBook(book); // 关联借阅的图书实体
        record.setReader(reader); // 关联借阅的读者实体
        record.setBorrowDate(today); // 记录借出日期
        record.setDueDate(today.plusDays(loanDays)); // 计算应还日期 = 借出日期 + 借期，用于逾期判定
        record.setStatus(BorrowStatus.BORROWED); // 初始状态置为 BORROWED（已借出未归还）
        record.setRenewCount(0); // 续借次数初始化为 0
        record.setFine(0.0); // 罚款初始化为 0
        record.setRemark(request.remark()); // 记录备注信息

        // 扣减库存与增加读者借阅计数（同一事务内保证一致性）
        bookService.decreaseAvailable(book); // 调用图书服务扣减可借数量
        readerService.incrementBorrowCount(reader); // 调用读者服务增加当前借阅计数

        BorrowRecord saved = recordRepository.save(record); // 持久化借阅记录到数据库
        return BorrowRecordResponse.from(saved); // 转换为响应 DTO 返回
    }

    /**
     * 还书操作
     * <p>
     * 业务规则：
     * 1. 借阅记录必须存在且未归还；
     * 2. 归还日期默认为今天；
     * 3. 若实际归还日期晚于应还日期，计算超期罚款（0.5 元/天），状态置为 OVERDUE；
     * 4. 恢复图书可借数量，减少读者借阅计数。
     * </p>
     *
     * @param request 还书请求
     * @return 更新后的借阅记录响应
     * @throws IllegalArgumentException 记录不存在
     * @throws IllegalStateException    记录已归还
     */
    @Transactional // 开启事务，保证记录更新、库存恢复、读者计数减少的原子性
    public BorrowRecordResponse returnBook(ReturnRequest request) { // 还书业务入口方法
        BorrowRecord record = recordRepository.findById(request.recordId()) // 根据记录 ID 查询借阅记录
                .orElseThrow(() -> new IllegalArgumentException("借阅记录不存在: " + request.recordId())); // 记录不存在则抛出参数异常

        // 已归还记录不允许重复操作
        if (record.getStatus() == BorrowStatus.RETURNED) { // 校验状态：已归还记录禁止重复还书，保证幂等性
            throw new IllegalStateException("该记录已归还，无法重复操作"); // 抛出状态异常，触发 422 响应
        }

        LocalDate today = LocalDate.now(); // 获取当前日期作为归还日期
        record.setReturnDate(today); // 记录实际归还日期

        // 计算超期罚款
        if (today.isAfter(record.getDueDate())) { // 归还日期晚于应还日期，判定为超期
            long overdueDays = today.toEpochDay() - record.getDueDate().toEpochDay(); // 计算超期天数（毫秒级天数差）
            double fine = overdueDays * ReaderType.OVERDUE_FINE_PER_DAY; // 超期罚款 = 超期天数 × 0.5 元/天
            record.setFine(fine); // 记录罚款金额
            record.setStatus(BorrowStatus.OVERDUE); // 状态置为 OVERDUE（归还时已超期）
        } else {
            record.setStatus(BorrowStatus.RETURNED); // 未超期则状态置为 RETURNED（已归还）
        }

        // 恢复库存与减少读者借阅计数
        bookService.increaseAvailable(record.getBook()); // 调用图书服务恢复可借数量
        readerService.decrementBorrowCount(record.getReader()); // 调用读者服务减少当前借阅计数

        BorrowRecord saved = recordRepository.save(record); // 持久化更新后的借阅记录
        return BorrowRecordResponse.from(saved); // 转换为响应 DTO 返回
    }

    /**
     * 续借操作
     * <p>
     * 业务规则：
     * 1. 记录必须存在且状态为 BORROWED；
     * 2. 续借次数不能超过上限（1 次）；
     * 3. 应还日期延长 15 天；
     * 4. 已逾期记录不允许续借。
     * </p>
     *
     * @param request 续借请求
     * @return 更新后的借阅记录响应
     * @throws IllegalArgumentException 记录不存在
     * @throws IllegalStateException    状态不允许续借或已达续借上限
     */
    @Transactional // 开启事务，保证续借操作的原子性
    public BorrowRecordResponse renewBook(RenewRequest request) { // 续借业务入口方法
        BorrowRecord record = recordRepository.findById(request.recordId()) // 根据记录 ID 查询借阅记录
                .orElseThrow(() -> new IllegalArgumentException("借阅记录不存在: " + request.recordId())); // 记录不存在则抛出参数异常

        // 已归还记录不能续借
        if (record.getStatus() == BorrowStatus.RETURNED) { // 已归还记录无法续借，状态已终结
            throw new IllegalStateException("已归还的记录无法续借"); // 抛出状态异常，触发 422 响应
        }

        // 已逾期记录不能续借
        if (LocalDate.now().isAfter(record.getDueDate())) { // 当前日期已超过应还日期，禁止续借避免逃避罚款
            throw new IllegalStateException("已逾期的记录无法续借，请先归还"); // 抛出状态异常，触发 422 响应
        }

        // 校验续借次数
        if (record.getRenewCount() >= ReaderType.MAX_RENEW_COUNT) { // 续借次数已达上限（1 次），防止无限续借
            throw new IllegalStateException("已达续借上限（" + ReaderType.MAX_RENEW_COUNT + " 次）"); // 抛出状态异常，触发 422 响应
        }

        // 延长应还日期
        record.setDueDate(record.getDueDate().plusDays(ReaderType.RENEW_EXTEND_DAYS)); // 应还日期延长 15 天
        record.setRenewCount(record.getRenewCount() + 1); // 续借次数加 1

        BorrowRecord saved = recordRepository.save(record); // 持久化更新后的借阅记录
        return BorrowRecordResponse.from(saved); // 转换为响应 DTO 返回
    }

    /**
     * 查询全部借阅记录
     *
     * @return 借阅记录列表
     */
    public List<BorrowRecordResponse> getAllRecords() { // 查询全部借阅记录方法（只读）
        return recordRepository.findAllByOrderByBorrowDateDesc().stream() // 调用仓储按借出日期倒序查询，最新记录优先展示
                .map(BorrowRecordResponse::from) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 按读者 ID 查询借阅记录
     *
     * @param readerId 读者 ID
     * @return 借阅记录列表
     */
    public List<BorrowRecordResponse> getRecordsByReaderId(Long readerId) { // 按读者 ID 查询借阅历史
        return recordRepository.findByReaderIdOrderByBorrowDateDesc(readerId).stream() // 调用仓储按读者 ID 查询并按借出日期倒序
                .map(BorrowRecordResponse::from) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 按图书 ID 查询借阅历史
     *
     * @param bookId 图书 ID
     * @return 借阅记录列表
     */
    public List<BorrowRecordResponse> getRecordsByBookId(Long bookId) { // 按图书 ID 查询借阅历史
        return recordRepository.findByBookIdOrderByBorrowDateDesc(bookId).stream() // 调用仓储按图书 ID 查询并按借出日期倒序
                .map(BorrowRecordResponse::from) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 按状态查询借阅记录
     *
     * @param status 借阅状态
     * @return 借阅记录列表
     */
    public List<BorrowRecordResponse> getRecordsByStatus(BorrowStatus status) { // 按状态过滤借阅记录
        return recordRepository.findByStatusOrderByBorrowDateDesc(status).stream() // 调用仓储按状态查询并按借出日期倒序
                .map(BorrowRecordResponse::from) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }
}
