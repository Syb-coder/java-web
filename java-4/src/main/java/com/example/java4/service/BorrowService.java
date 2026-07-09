// 声明包路径，存放业务服务层
package com.example.java4.service;

// 导入 DTO 与实体类
import com.example.java4.dto.BorrowRecordResponse;
import com.example.java4.model.Book;
import com.example.java4.model.BorrowRecord;
import com.example.java4.model.BorrowStatus;
import com.example.java4.model.Reader;
import com.example.java4.model.ReaderType;

// 导入 Repository
import com.example.java4.repository.BookRepository;
import com.example.java4.repository.BorrowRecordRepository;
import com.example.java4.repository.ReaderRepository;

// 导入 Spring 工具
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 借阅业务服务
 * <p>
 * 核心业务逻辑层，处理图书借阅、续借、归还、逾期判定等流程。
 * </p>
 * <p>
 * 借阅规则：
 * <ul>
 *   <li>学生：最多借 5 本，借期 30 天；</li>
 *   <li>教师：最多借 10 本，借期 60 天；</li>
 *   <li>续借：最多 1 次，延长 15 天；</li>
 *   <li>逾期罚款：每天 0.5 元。</li>
 * </ul>
 * </p>
 */
@Service // 声明为 Spring 服务组件
public class BorrowService {

    /** 续借延长的天数 */
    private static final int RENEW_DAYS = 15;

    /** 逾期罚款单价（元/天） */
    private static final double FINE_PER_DAY = 0.5;

    /** 图书仓储 */
    private final BookRepository bookRepository;

    /** 读者仓储 */
    private final ReaderRepository readerRepository;

    /** 借阅记录仓储 */
    private final BorrowRecordRepository borrowRecordRepository;

    /**
     * 构造方法注入依赖
     */
    public BorrowService(BookRepository bookRepository, ReaderRepository readerRepository,
                         BorrowRecordRepository borrowRecordRepository) {
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    /**
     * 借阅单本图书
     * <p>
     * 核心借阅流程：校验读者借阅上限 -> 校验图书可借 -> 扣减库存 -> 创建借阅记录。
     * 使用 @Transactional 保证库存扣减与记录创建的原子性。
     * </p>
     *
     * @param readerId 读者 ID
     * @param bookId   图书 ID
     * @return 借阅记录响应
     * @throws IllegalArgumentException 读者/图书不存在、达到借阅上限、图书无可用副本
     */
    @Transactional // 事务保证库存扣减与记录创建原子性
    public BorrowRecordResponse borrowBook(Long readerId, Long bookId) {
        // 加载读者实体
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new IllegalArgumentException("读者不存在"));
        // 加载图书实体
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("图书不存在"));

        // 校验图书是否有可借副本
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalArgumentException("该书暂无可借副本");
        }

        // 校验读者是否达到借阅上限（统计借阅中记录数）
        long borrowingCount = borrowRecordRepository.countByReaderIdAndStatus(readerId, BorrowStatus.BORROWING);
        ReaderType readerType = reader.getType();
        if (borrowingCount >= readerType.getMaxBorrowCount()) {
            throw new IllegalArgumentException(
                    "已达到借阅上限（" + readerType.getMaxBorrowCount() + " 本），请先归还部分图书");
        }

        // 扣减图书可借数量
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // 计算应还日期
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(readerType.getBorrowDays());

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setReader(reader);
        record.setBorrowDate(borrowDate);
        record.setDueDate(dueDate);
        record.setStatus(BorrowStatus.BORROWING);
        record.setRenewCount(0);
        record.setFine(0.0);
        borrowRecordRepository.save(record);

        // 更新读者当前借阅数（冗余字段，避免频繁 COUNT 查询）
        reader.setCurrentBorrowCount(reader.getCurrentBorrowCount() + 1);
        readerRepository.save(reader);

        return toResponse(record);
    }

    /**
     * 续借图书
     * <p>
     * 规则：仅借阅中状态可续借，且最多续借 1 次，延长 15 天。
     * </p>
     *
     * @param readerId 读者 ID（校验归属）
     * @param recordId 借阅记录 ID
     * @return 续借后的借阅记录响应
     * @throws IllegalArgumentException 记录不存在、非本人记录、已续借过、非借阅中状态
     */
    @Transactional
    public BorrowRecordResponse renewBook(Long readerId, Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("借阅记录不存在"));

        // 校验记录归属
        if (!record.getReader().getId().equals(readerId)) {
            throw new IllegalArgumentException("无权操作他人借阅记录");
        }
        // 校验状态
        if (record.getStatus() != BorrowStatus.BORROWING) {
            throw new IllegalArgumentException("仅借阅中状态可续借");
        }
        // 校验续借次数
        if (record.getRenewCount() >= 1) {
            throw new IllegalArgumentException("每条记录最多续借 1 次");
        }

        // 续借：应还日期延长 15 天，续借次数 +1
        record.setDueDate(record.getDueDate().plusDays(RENEW_DAYS));
        record.setRenewCount(record.getRenewCount() + 1);
        borrowRecordRepository.save(record);

        return toResponse(record);
    }

    /**
     * 归还图书
     * <p>
     * 读者归还或管理员处理归还。归还时计算逾期罚款，恢复图书可借数量。
     * </p>
     *
     * @param recordId 借阅记录 ID
     * @return 归还后的借阅记录响应
     * @throws IllegalArgumentException 记录不存在或已归还
     */
    @Transactional
    public BorrowRecordResponse returnBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("借阅记录不存在"));

        // 校验状态
        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new IllegalArgumentException("该图书已归还");
        }

        LocalDate today = LocalDate.now();
        record.setReturnDate(today);

        // 判断是否逾期并计算罚款
        if (today.isAfter(record.getDueDate())) {
            long overdueDays = today.toEpochDay() - record.getDueDate().toEpochDay();
            record.setFine(overdueDays * FINE_PER_DAY);
            record.setStatus(BorrowStatus.RETURNED); // 归还后状态变为已归还（逾期信息保留在 fine 字段）
        } else {
            record.setFine(0.0);
            record.setStatus(BorrowStatus.RETURNED);
        }

        // 恢复图书可借数量
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        // 更新读者当前借阅数
        Reader reader = record.getReader();
        reader.setCurrentBorrowCount(Math.max(0, reader.getCurrentBorrowCount() - 1));
        readerRepository.save(reader);

        borrowRecordRepository.save(record);
        return toResponse(record);
    }

    /**
     * 查询读者的借阅记录（用户端"我的借阅"）
     *
     * @param readerId 读者 ID
     * @param status   借阅状态（可为空，空则查询全部）
     * @param pageable 分页参数
     * @return 借阅记录分页结果
     */
    public Page<BorrowRecordResponse> getMyBorrowRecords(Long readerId, String status, Pageable pageable) {
        // 先更新逾期状态，确保返回的数据状态准确
        updateOverdueStatusForReader(readerId);

        if (status == null || status.trim().isEmpty()) {
            return borrowRecordRepository.findByReaderIdOrderByBorrowDateDesc(readerId, pageable)
                    .map(this::toResponse);
        }
        BorrowStatus borrowStatus = BorrowStatus.valueOf(status.toUpperCase());
        return borrowRecordRepository.findByReaderIdAndStatusOrderByBorrowDateDesc(readerId, borrowStatus, pageable)
                .map(this::toResponse);
    }

    /**
     * 查询全部借阅记录（后台管理端）
     *
     * @param status   借阅状态（可为空）
     * @param pageable 分页参数
     * @return 借阅记录分页结果
     */
    public Page<BorrowRecordResponse> getAllBorrowRecords(String status, Pageable pageable) {
        // 更新逾期状态
        updateOverdueStatus();

        if (status == null || status.trim().isEmpty()) {
            return borrowRecordRepository.findAllByOrderByBorrowDateDesc(pageable)
                    .map(this::toResponse);
        }
        BorrowStatus borrowStatus = BorrowStatus.valueOf(status.toUpperCase());
        return borrowRecordRepository.findByStatusOrderByBorrowDateDesc(borrowStatus, pageable)
                .map(this::toResponse);
    }

    /**
     * 更新所有读者的逾期状态
     * <p>
     * 遍历所有借阅中且已超过应还日期的记录，将状态更新为 OVERDUE。
     * 此方法在查询借阅记录前调用，确保数据状态实时准确。
     * </p>
     */
    @Transactional
    public void updateOverdueStatus() {
        LocalDate today = LocalDate.now();
        List<BorrowRecord> overdueRecords = borrowRecordRepository
                .findByDueDateBeforeAndStatus(today, BorrowStatus.BORROWING);
        for (BorrowRecord record : overdueRecords) {
            record.setStatus(BorrowStatus.OVERDUE);
            borrowRecordRepository.save(record);
        }
    }

    /**
     * 更新指定读者的逾期状态
     *
     * @param readerId 读者 ID
     */
    @Transactional
    public void updateOverdueStatusForReader(Long readerId) {
        LocalDate today = LocalDate.now();
        // 复用查询方法，过滤当前读者的逾期记录
        List<BorrowRecord> overdueRecords = borrowRecordRepository
                .findByDueDateBeforeAndStatus(today, BorrowStatus.BORROWING)
                .stream()
                .filter(r -> r.getReader().getId().equals(readerId))
                .toList();
        for (BorrowRecord record : overdueRecords) {
            record.setStatus(BorrowStatus.OVERDUE);
            borrowRecordRepository.save(record);
        }
    }

    /**
     * 实体转响应 DTO
     *
     * @param record 借阅记录实体
     * @return 借阅记录响应 DTO
     */
    public BorrowRecordResponse toResponse(BorrowRecord record) {
        BorrowRecordResponse resp = new BorrowRecordResponse();
        resp.setId(record.getId());

        // 安全获取图书信息
        Book book = record.getBook();
        if (book != null) {
            resp.setBookId(book.getId());
            resp.setBookTitle(book.getTitle());
            resp.setBookAuthor(book.getAuthor());
            resp.setCoverImage(book.getCoverImage());
            if (book.getCategory() != null) {
                resp.setCategoryName(book.getCategory().getName());
            }
        }

        // 安全获取读者信息
        Reader reader = record.getReader();
        if (reader != null) {
            resp.setReaderId(reader.getId());
            resp.setReaderName(reader.getName());
            resp.setReaderNo(reader.getReaderNo());
        }

        resp.setBorrowDate(record.getBorrowDate());
        resp.setDueDate(record.getDueDate());
        resp.setReturnDate(record.getReturnDate());
        resp.setStatus(record.getStatus().name());
        resp.setRenewCount(record.getRenewCount());
        resp.setFine(record.getFine());
        resp.setRemark(record.getRemark());

        // 判断是否可续借：借阅中状态且续借次数为 0
        resp.setRenewable(record.getStatus() == BorrowStatus.BORROWING && record.getRenewCount() == 0);
        resp.setUpdateTime(record.getUpdateTime());

        return resp;
    }
}
