// 声明当前类所在的包路径，归类为 service 业务服务层
package com.example.java1.service;

import com.example.java1.dto.BookRequest; // 引入图书请求 DTO，封装新增/修改参数
import com.example.java1.dto.BookResponse; // 引入图书响应 DTO，用于对外返回图书信息
import com.example.java1.model.Book; // 引入图书实体类，对应数据库 book 表
import com.example.java1.repository.BookRepository; // 引入图书 JPA 仓储
import jakarta.persistence.OptimisticLockException; // 引入 JPA 乐观锁异常，用于捕获并发冲突
import org.springframework.stereotype.Service; // 引入 @Service 注解，标记服务层组件
import org.springframework.transaction.annotation.Transactional; // 引入 @Transactional 注解，用于声明式事务管理

import java.time.LocalDateTime; // 引入时间类，用于记录创建时间
import java.util.List; // 引入 List 集合接口
import java.util.stream.Collectors; // 引入 Stream 收集器工具

/**
 * 图书业务服务
 * <p>
 * 封装图书的增删改查与库存操作。关键设计：
 * <ul>
 *   <li>借还书时通过 decreaseAvailable / increaseAvailable 操作库存，保证一致性；</li>
 *   <li>@Version 乐观锁由 JPA 自动校验，并发冲突抛出 OptimisticLockException。</li>
 * </ul>
 * </p>
 */
@Service // 声明为 Spring 服务组件，由 IoC 容器管理为单例 Bean
public class BookService {

    /** 图书仓储 */
    private final BookRepository repository; // 图书仓储，final 保证不可变

    /**
     * 构造方法注入
     */
    public BookService(BookRepository repository) { // 构造方法注入仓储依赖
        this.repository = repository; // 赋值图书仓储
    }

    /**
     * 查询全部图书
     *
     * @return 图书响应列表
     */
    public List<BookResponse> getAllBooks() { // 查询全部图书方法（只读）
        return repository.findAll().stream() // 调用仓储加载全部图书实体
                .map(BookResponse::from) // 实体转 DTO，避免直接暴露实体
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 按 ID 查询图书
     *
     * @param id 图书 ID
     * @return 图书响应（可能为空）
     */
    public BookResponse getBookById(Long id) { // 按 ID 查询图书方法
        return repository.findById(id) // 调用仓储按主键查询
                .map(BookResponse::from) // 存在则转换为 DTO
                .orElse(null); // 不存在返回 null，由上层判空处理
    }

    /**
     * 按关键字检索（书名或作者）
     *
     * @param keyword 关键字
     * @return 匹配的图书列表
     */
    public List<BookResponse> searchBooks(String keyword) { // 按关键字模糊检索图书
        if (keyword == null || keyword.isBlank()) { // 关键字为空时降级返回全部图书
            return getAllBooks(); // 返回全部图书列表
        }
        return repository.searchByKeyword(keyword.trim()).stream() // 调用仓储自定义查询，去除关键字首尾空白
                .map(BookResponse::from) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 按分类查询
     *
     * @param category 分类
     * @return 图书列表
     */
    public List<BookResponse> getBooksByCategory(String category) { // 按分类查询图书
        return repository.findByCategory(category).stream() // 调用仓储按分类精确查询
                .map(BookResponse::from) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 获取图书实体（供 BorrowService 调用）
     *
     * @param id 图书 ID
     * @return 图书实体
     * @throws IllegalArgumentException 图书不存在
     */
    public Book getBookEntity(Long id) { // 按图书 ID 加载实体方法（供内部服务调用）
        return repository.findById(id) // 调用仓储按主键查询
                .orElseThrow(() -> new IllegalArgumentException("图书不存在: " + id)); // 不存在则抛出参数异常，触发 400 响应
    }

    /**
     * 新增图书
     *
     * @param req 图书请求
     * @return 新建的图书响应
     */
    @Transactional // 开启事务，保证新增操作的原子性
    public BookResponse createBook(BookRequest req) { // 新增图书业务入口方法
        Book book = new Book(); // 创建新的图书实体
        applyRequestToEntity(req, book); // 将请求字段应用到实体
        book.setCreateTime(LocalDateTime.now()); // 记录创建时间，用于审计
        Book saved = repository.save(book); // 持久化图书到数据库
        return BookResponse.from(saved); // 转换为响应 DTO 返回
    }

    /**
     * 修改图书
     *
     * @param id  图书 ID
     * @param req 图书请求
     * @return 更新后的图书响应
     * @throws IllegalArgumentException 图书不存在
     */
    @Transactional // 开启事务，保证修改操作的原子性
    public BookResponse updateBook(Long id, BookRequest req) { // 修改图书业务入口方法
        Book book = getBookEntity(id); // 加载现有图书实体（不存在则抛异常）
        applyRequestToEntity(req, book); // 将请求字段应用到实体
        Book saved = repository.save(book); // 持久化更新，JPA 自动校验 @Version 乐观锁
        return BookResponse.from(saved); // 转换为响应 DTO 返回
    }

    /**
     * 删除图书
     *
     * @param id 图书 ID
     * @throws IllegalArgumentException 图书不存在
     */
    @Transactional // 开启事务，保证删除操作的原子性
    public void deleteBook(Long id) { // 删除图书业务入口方法
        if (!repository.existsById(id)) { // 校验图书是否存在，避免删除不存在记录
            throw new IllegalArgumentException("图书不存在: " + id); // 抛出参数异常，触发 400 响应
        }
        repository.deleteById(id); // 按主键删除图书
    }

    /**
     * 扣减可借数量（借书时调用）
     * <p>乐观锁冲突时重试一次，仍失败则抛出异常。</p>
     *
     * @param book 图书实体
     * @throws IllegalStateException 库存不足
     */
    @Transactional // 开启事务，保证库存扣减的原子性
    public void decreaseAvailable(Book book) { // 扣减可借数量方法（借书时调用）
        if (book.getAvailableCopies() <= 0) { // 校验库存：可借数量为 0 时禁止借出，防止超借
            throw new IllegalStateException("图书《" + book.getTitle() + "》已无可借副本"); // 抛出状态异常，触发 422 业务冲突响应
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1); // 可借数量减 1
        try {
            repository.save(book); // 持久化扣减，JPA 自动校验 @Version
        } catch (OptimisticLockException e) { // 捕获乐观锁异常，处理并发借书冲突
            // 乐观锁冲突，重新加载并重试一次
            Book fresh = repository.findById(book.getId()).orElseThrow(); // 重新加载最新版本图书实体
            if (fresh.getAvailableCopies() <= 0) { // 重试时再次校验库存，可能已被其他事务扣减完
                throw new IllegalStateException("图书《" + book.getTitle() + "》已无可借副本"); // 库存不足抛出状态异常
            }
            fresh.setAvailableCopies(fresh.getAvailableCopies() - 1); // 基于最新版本扣减可借数量
            repository.save(fresh); // 持久化扣减
        }
    }

    /**
     * 恢复可借数量（还书时调用）
     *
     * @param book 图书实体
     */
    @Transactional // 开启事务，保证库存恢复的原子性
    public void increaseAvailable(Book book) { // 恢复可借数量方法（还书时调用）
        book.setAvailableCopies(book.getAvailableCopies() + 1); // 可借数量加 1
        repository.save(book); // 持久化恢复
    }

    /**
     * 图书种类总数
     *
     * @return 种类数
     */
    public long getTotalBookCount() { // 图书种类总数聚合方法（只读）
        return repository.count(); // 调用仓储 count 聚合查询
    }

    /**
     * 副本总数
     *
     * @return 副本总数
     */
    public long sumTotalCopies() { // 副本总数聚合方法（只读）
        return repository.sumTotalCopies(); // 调用仓储 sum 聚合查询
    }

    /**
     * 可借副本总数
     *
     * @return 可借副本总数
     */
    public long sumAvailableCopies() { // 可借副本总数聚合方法（只读）
        return repository.sumAvailableCopies(); // 调用仓储 sum 聚合查询
    }

    /**
     * 将请求 DTO 的字段应用到实体（新增/修改共用）
     *
     * @param req  请求
     * @param book 实体
     */
    private void applyRequestToEntity(BookRequest req, Book book) { // 请求字段映射到实体的私有方法（新增/修改共用）
        book.setTitle(req.title()); // 设置书名
        book.setAuthor(req.author()); // 设置作者
        book.setIsbn(req.isbn()); // 设置 ISBN 国际标准书号
        book.setCategory(req.category()); // 设置分类
        book.setPublisher(req.publisher()); // 设置出版社
        book.setPublishYear(req.publishYear()); // 设置出版年份
        book.setDescription(req.description()); // 设置描述信息
        book.setTotalCopies(req.totalCopies()); // 设置副本总数
        book.setAvailableCopies(req.availableCopies()); // 设置可借副本数
        book.setLocation(req.location()); // 设置馆藏位置
    }
}
