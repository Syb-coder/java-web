// 声明包路径，存放业务服务层
package com.example.java4.service;

// 导入 DTO 与实体类
import com.example.java4.dto.BookRequest;
import com.example.java4.dto.BookResponse;
import com.example.java4.model.Book;
import com.example.java4.model.BookCategory;

// 导入 Repository
import com.example.java4.repository.BookCategoryRepository;
import com.example.java4.repository.BookRepository;

// 导入 Spring 工具
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 图书业务服务
 * <p>
 * 提供图书的 CRUD、分页查询、关键字搜索、按分类筛选等能力。
 * 用户端调用查询接口，管理端调用增删改接口。
 * </p>
 */
@Service // 声明为 Spring 服务组件
public class BookService {

    /** 图书仓储 */
    private final BookRepository bookRepository;

    /** 分类仓储（用于关联分类查询） */
    private final BookCategoryRepository categoryRepository;

    /**
     * 构造方法注入依赖
     */
    public BookService(BookRepository bookRepository, BookCategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * 分页查询图书（支持关键字搜索）
     *
     * @param keyword  搜索关键字（书名或作者，可为空）
     * @param pageable 分页参数
     * @return 图书分页结果
     */
    public Page<BookResponse> searchBooks(String keyword, Pageable pageable) {
        // 关键字为空时查询全部
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.findAll(pageable).map(this::toResponse);
        }
        // 按书名或作者关键字搜索
        return bookRepository.findByTitleContainingOrAuthorContaining(keyword, keyword, pageable)
                .map(this::toResponse);
    }

    /**
     * 按分类分页查询图书（支持关键字组合搜索）
     *
     * @param categoryId 分类 ID
     * @param keyword    搜索关键字（可为空）
     * @param pageable   分页参数
     * @return 图书分页结果
     */
    public Page<BookResponse> searchByCategory(Long categoryId, String keyword, Pageable pageable) {
        // 关键字为空时仅按分类查询
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.findByCategoryId(categoryId, pageable).map(this::toResponse);
        }
        // 按分类 + 关键字组合搜索
        return bookRepository.findByCategoryIdAndKeyword(categoryId, keyword, pageable)
                .map(this::toResponse);
    }

    /**
     * 获取最新入库图书（主页展示）
     *
     * @param pageable 分页参数
     * @return 图书分页结果
     */
    public Page<BookResponse> getLatestBooks(Pageable pageable) {
        return bookRepository.findAllByOrderByCreateTimeDesc(pageable).map(this::toResponse);
    }

    /**
     * 根据 ID 获取图书详情
     *
     * @param id 图书 ID
     * @return 图书响应 DTO（不存在返回 null）
     */
    public BookResponse getBookById(Long id) {
        return bookRepository.findById(id).map(this::toResponse).orElse(null);
    }

    /**
     * 根据 ID 获取图书实体（内部使用）
     *
     * @param id 图书 ID
     * @return 图书实体（不存在返回 null）
     */
    public Book getBookEntity(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    /**
     * 新增图书（管理端）
     *
     * @param req 图书请求
     * @return 新创建的图书响应
     * @throws IllegalArgumentException 分类不存在
     */
    public BookResponse createBook(BookRequest req) {
        // 加载分类实体
        BookCategory category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        // 构建图书实体
        Book book = new Book();
        applyRequestToEntity(book, req, category);
        book.setAvailableCopies(req.getTotalCopies()); // 新书入库默认全部可借
        book.setCreateTime(LocalDateTime.now());
        // 持久化并返回响应
        return toResponse(bookRepository.save(book));
    }

    /**
     * 修改图书（管理端）
     *
     * @param id  图书 ID
     * @param req 图书请求
     * @return 修改后的图书响应
     * @throws IllegalArgumentException 图书或分类不存在
     */
    public BookResponse updateBook(Long id, BookRequest req) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("图书不存在"));
        BookCategory category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        // 计算可借数量差值：如果总数量调整了，可借数量同步调整
        int diff = req.getTotalCopies() - book.getTotalCopies();
        applyRequestToEntity(book, req, category);
        book.setAvailableCopies(book.getAvailableCopies() + diff);
        // 防止可借数量为负
        if (book.getAvailableCopies() < 0) {
            book.setAvailableCopies(0);
        }
        return toResponse(bookRepository.save(book));
    }

    /**
     * 删除图书（管理端）
     *
     * @param id 图书 ID
     * @throws IllegalArgumentException 图书不存在
     */
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("图书不存在");
        }
        bookRepository.deleteById(id);
    }

    /**
     * 将请求 DTO 的字段赋值到实体（复用方法，避免重复代码）
     *
     * @param book     目标实体
     * @param req      请求 DTO
     * @param category 关联分类
     */
    private void applyRequestToEntity(Book book, BookRequest req, BookCategory category) {
        book.setTitle(req.getTitle());
        book.setAuthor(req.getAuthor());
        book.setIsbn(req.getIsbn());
        book.setCategory(category);
        book.setPublisher(req.getPublisher());
        book.setPublishYear(req.getPublishYear());
        book.setDescription(req.getDescription());
        book.setCoverImage(req.getCoverImage());
        book.setTotalCopies(req.getTotalCopies());
        book.setLocation(req.getLocation());
    }

    /**
     * 实体转响应 DTO
     *
     * @param book 图书实体
     * @return 图书响应 DTO
     */
    public BookResponse toResponse(Book book) {
        BookResponse resp = new BookResponse();
        resp.setId(book.getId());
        resp.setTitle(book.getTitle());
        resp.setAuthor(book.getAuthor());
        resp.setIsbn(book.getIsbn());
        // 安全获取分类信息，避免空指针
        if (book.getCategory() != null) {
            resp.setCategoryId(book.getCategory().getId());
            resp.setCategoryName(book.getCategory().getName());
        }
        resp.setPublisher(book.getPublisher());
        resp.setPublishYear(book.getPublishYear());
        resp.setDescription(book.getDescription());
        resp.setCoverImage(book.getCoverImage());
        resp.setTotalCopies(book.getTotalCopies());
        resp.setAvailableCopies(book.getAvailableCopies());
        resp.setLocation(book.getLocation());
        resp.setCreateTime(book.getCreateTime());
        resp.setUpdateTime(book.getUpdateTime());
        return resp;
    }
}
