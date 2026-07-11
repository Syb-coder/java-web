package com.example.java7.service; // 声明当前类所在的包路径，归类为 service 业务服务层

import com.example.java7.dto.BookRequest; // 引入图书请求 DTO，用于接收 Controller 传入的请求参数
import com.example.java7.dto.BookResponse; // 引入图书响应 DTO，用于对外返回脱敏后的图书数据
import com.example.java7.model.Book; // 引入图书实体类，对应数据库 books 表
import com.example.java7.model.BookCategory; // 引入图书分类枚举，定义图书所属类别
import com.example.java7.repository.BookRepository; // 引入图书 JPA 仓储，封装数据库访问操作
import org.springframework.stereotype.Service; // 引入 Spring 的 @Service 注解，标记服务层组件
import org.springframework.transaction.annotation.Transactional; // 引入 @Transactional 注解，用于声明式事务管理

import java.time.LocalDateTime; // 引入日期时间类，用于记录图书创建时间
import java.util.List; // 引入 List 集合接口，用于返回图书列表
import java.util.stream.Collectors; // 引入 Stream 收集器工具，将 Stream 转换为 List

/**
 * 图书业务服务
 * <p>
 * 职责：封装图书 CRUD、检索等核心业务逻辑，对 Controller 层提供统一 API。
 * 使用 @Transactional 保证事务一致性，避免借阅操作中的数据不一致。
 * </p>
 */
@Service // 声明为 Spring 服务组件，由 IoC 容器管理生命周期并注册为单例 Bean
public class BookService {

    /** 图书仓储，由 Spring 注入 */
    private final BookRepository bookRepository; // 声明为 final 字段，确保依赖注入后不可变，线程安全

    /**
     * 构造方法注入仓储
     *
     * @param bookRepository 图书仓储
     */
    public BookService(BookRepository bookRepository) { // 构造方法注入，推荐方式，便于单元测试与不可变性保证
        this.bookRepository = bookRepository; // 将容器注入的仓储实例赋值给成员变量
    }

    /**
     * 新增图书
     * <p>
     * 新书入库时，可借数量默认等于总数量。
     * </p>
     *
     * @param request 图书请求 DTO
     * @return 新建的图书响应
     */
    @Transactional // 开启事务，保证方法内数据库操作的原子性，异常时自动回滚
    public BookResponse addBook(BookRequest request) { // 新增图书入口方法，接收请求 DTO
        Book book = new Book(); // 创建空的图书实体对象，等待填充字段
        book.setTitle(request.getTitle()); // 设置书名
        book.setAuthor(request.getAuthor()); // 设置作者
        book.setIsbn(request.getIsbn()); // 设置 ISBN 国际标准书号，唯一标识图书版本
        book.setCategory(request.getCategory()); // 设置图书分类枚举
        book.setPublisher(request.getPublisher()); // 设置出版社
        book.setPublishYear(request.getPublishYear()); // 设置出版年份
        book.setDescription(request.getDescription()); // 设置图书简介
        book.setTotalCopies(request.getTotalCopies()); // 设置馆藏总副本数
        // 新书可借数量等于总数量
        book.setAvailableCopies(request.getTotalCopies()); // 新书入库时尚无借出，可借数量等于总数量
        book.setLocation(request.getLocation()); // 设置图书物理存放位置
        book.setCreateTime(LocalDateTime.now()); // 记录入库时间，便于审计追踪
        Book saved = bookRepository.save(book); // 调用 JPA 仓储持久化到数据库，返回带主键的实体
        return new BookResponse(saved); // 将实体转换为响应 DTO 后返回，避免直接暴露实体
    }

    /**
     * 更新图书信息
     * <p>
     * 编辑时不可直接修改 totalCopies，避免与借阅记录冲突；
     * 如需调整馆藏数量，应通过专门的接口处理。
     * </p>
     *
     * @param id      图书 ID
     * @param request 图书请求 DTO
     * @return 更新后的图书响应
     * @throws IllegalArgumentException 图书不存在时抛出
     */
    @Transactional // 开启事务，确保更新操作与库存校验的原子性，避免并发更新导致数据不一致
    public BookResponse updateBook(Long id, BookRequest request) { // 更新图书信息方法
        Book book = bookRepository.findById(id) // 通过主键查询图书实体
                .orElseThrow(() -> new IllegalArgumentException("图书不存在: " + id)); // 不存在则抛出非法参数异常，触发上层 404 响应
        book.setTitle(request.getTitle()); // 更新书名
        book.setAuthor(request.getAuthor()); // 更新作者
        book.setIsbn(request.getIsbn()); // 更新 ISBN
        book.setCategory(request.getCategory()); // 更新分类
        book.setPublisher(request.getPublisher()); // 更新出版社
        book.setPublishYear(request.getPublishYear()); // 更新出版年份
        book.setDescription(request.getDescription()); // 更新简介
        book.setLocation(request.getLocation()); // 更新存放位置
        // 同步更新馆藏数量时需校验：新总数不能小于已借出数量
        int borrowedCount = book.getTotalCopies() - book.getAvailableCopies(); // 计算当前已借出副本数：总数减去可借数
        if (request.getTotalCopies() < borrowedCount) { // 校验新馆藏总数不能小于已借出数量，防止数据不一致
            throw new IllegalStateException( // 抛出状态异常，触发上层 422 业务冲突响应
                    "馆藏数量不能小于已借出数量(" + borrowedCount + ")");
        }
        book.setTotalCopies(request.getTotalCopies()); // 安全校验通过后更新总数量
        book.setAvailableCopies(request.getTotalCopies() - borrowedCount); // 重新计算可借数量：新总数减去已借出数
        book.setUpdateTime(LocalDateTime.now());
        Book saved = bookRepository.save(book); // 持久化更新后的实体
        return new BookResponse(saved); // 转换为响应 DTO 返回
    }

    /**
     * 删除图书
     * <p>
     * 删除前需校验：存在未归还记录的图书不允许删除。
     * </p>
     *
     * @param id 图书 ID
     * @throws IllegalArgumentException 图书不存在
     * @throws IllegalStateException    存在未归还记录
     */
    @Transactional // 开启事务，确保删除校验与删除操作原子性，避免并发借阅导致脏删
    public void deleteBook(Long id) { // 删除图书方法，无返回值
        Book book = bookRepository.findById(id) // 查询待删除图书
                .orElseThrow(() -> new IllegalArgumentException("图书不存在: " + id)); // 不存在则抛出参数异常
        if (book.getAvailableCopies() < book.getTotalCopies()) { // 校验是否存在未归还记录：可借数小于总数说明有副本被借出
            throw new IllegalStateException("存在未归还记录，无法删除"); // 存在未归还则抛出状态异常，触发 422 响应
        }
        bookRepository.deleteById(id); // 通过主键删除图书记录
    }

    /**
     * 根据 ID 查询图书
     *
     * @param id 图书 ID
     * @return 图书响应
     * @throws IllegalArgumentException 图书不存在
     */
    public BookResponse getBookById(Long id) { // 按 ID 查询图书详情方法（只读，无需事务）
        Book book = bookRepository.findById(id) // 通过主键查询
                .orElseThrow(() -> new IllegalArgumentException("图书不存在: " + id)); // 不存在则抛出异常
        return new BookResponse(book); // 转换为响应 DTO 返回
    }

    /**
     * 查询所有图书
     *
     * @return 图书响应列表
     */
    public List<BookResponse> getAllBooks() { // 查询全部图书方法
        return bookRepository.findAll().stream() // 从数据库加载全部图书并转为 Stream 流处理
                .map(BookResponse::new) // 将每个 Book 实体映射为 BookResponse DTO，方法引用写法
                .collect(Collectors.toList()); // 收集为 List 集合返回
    }

    /**
     * 按关键字检索图书（书名或作者）
     *
     * @param keyword 关键字，可为空
     * @return 匹配的图书列表
     */
    public List<BookResponse> searchBooks(String keyword) { // 关键字检索图书方法
        if (keyword == null || keyword.isBlank()) { // 关键字为 null 或空白时降级返回全部图书，保证调用方一致性
            return getAllBooks(); // 返回全部图书列表
        }
        return bookRepository.searchByKeyword(keyword.trim()).stream() // 调用仓储自定义查询，去除关键字首尾空白后检索
                .map(BookResponse::new) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 按分类查询图书
     *
     * @param category 图书分类
     * @return 该分类下的图书列表
     */
    public List<BookResponse> getBooksByCategory(BookCategory category) { // 按分类枚举查询图书
        return bookRepository.findByCategory(category).stream() // 调用仓储按分类查询并开启流处理
                .map(BookResponse::new) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 减少可借数量（借书时调用）
     *
     * @param book 图书实体
     */
    @Transactional // 开启事务，保证扣减与持久化的原子性，配合调用方事务传播
    public void decreaseAvailable(Book book) { // 借书时扣减可借数量的内部方法
        book.setAvailableCopies(book.getAvailableCopies() - 1); // 原子扣减可借数量，配合 @Version 乐观锁防并发超借
        bookRepository.save(book); // 持久化更新后的可借数量
    }

    /**
     * 增加可借数量（还书时调用）
     *
     * @param book 图书实体
     */
    @Transactional // 开启事务，保证恢复与持久化的原子性
    public void increaseAvailable(Book book) { // 还书时恢复可借数量的内部方法
        book.setAvailableCopies(book.getAvailableCopies() + 1); // 可借数量加 1，恢复借出时扣减的库存
        bookRepository.save(book); // 持久化更新后的可借数量
    }

    /**
     * 根据原始实体 ID 获取 Book 实体（内部使用）
     *
     * @param id 图书 ID
     * @return 图书实体
     * @throws IllegalArgumentException 不存在
     */
    public Book getBookEntity(Long id) { // 供其他 Service 调用以获取原始实体，而非 DTO
        return bookRepository.findById(id) // 通过主键查询实体
                .orElseThrow(() -> new IllegalArgumentException("图书不存在: " + id)); // 不存在则抛出异常
    }

    /**
     * 获取图书种类总数（聚合查询）
     *
     * @return 图书种类数量
     */
    public long getTotalBookCount() { // 统计图书种类总数方法
        return bookRepository.count(); // 调用 JPA 内置 count 方法返回记录数
    }

    /**
     * 获取所有图书副本总数（聚合查询）
     *
     * @return 副本总数
     */
    public long sumTotalCopies() { // 统计所有图书副本总数方法
        return bookRepository.sumTotalCopies(); // 调用仓储自定义聚合查询，使用 SUM SQL 函数
    }

    /**
     * 获取所有图书可借副本总数（聚合查询）
     *
     * @return 可借副本总数
     */
    public long sumAvailableCopies() { // 统计所有图书可借副本总数方法
        return bookRepository.sumAvailableCopies(); // 调用仓储自定义聚合查询，用于首页统计展示
    }
}
