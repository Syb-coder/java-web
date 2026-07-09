// 声明包路径，存放业务服务层
package com.example.java4.service;

// 导入 DTO 与实体类
import com.example.java4.dto.CartItemResponse;
import com.example.java4.model.Book;
import com.example.java4.model.CartItem;
import com.example.java4.model.Reader;

// 导入 Repository
import com.example.java4.repository.BookRepository;
import com.example.java4.repository.CartItemRepository;
import com.example.java4.repository.ReaderRepository;

// 导入 Spring 工具
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 借阅车业务服务
 * <p>
 * 类似电商购物车，读者将图书加入借阅车后统一提交借阅。
 * 借阅车是临时态，提交借阅后自动清空。
 * </p>
 */
@Service // 声明为 Spring 服务组件
public class CartService {

    /** 借阅车仓储 */
    private final CartItemRepository cartItemRepository;

    /** 图书仓储 */
    private final BookRepository bookRepository;

    /** 读者仓储 */
    private final ReaderRepository readerRepository;

    /** 借阅服务（提交借阅时调用） */
    private final BorrowService borrowService;

    /**
     * 构造方法注入依赖
     */
    public CartService(CartItemRepository cartItemRepository, BookRepository bookRepository,
                       ReaderRepository readerRepository, BorrowService borrowService) {
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.borrowService = borrowService;
    }

    /**
     * 加入借阅车
     *
     * @param readerId 读者 ID
     * @param bookId   图书 ID
     * @return 借阅车项响应
     * @throws IllegalArgumentException 读者/图书不存在、图书无可借副本、已在借阅车中
     */
    public CartItemResponse addToCart(Long readerId, Long bookId) {
        // 校验读者存在
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new IllegalArgumentException("读者不存在"));
        // 校验图书存在
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("图书不存在"));
        // 校验图书有可借副本
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalArgumentException("该书暂无可借副本");
        }
        // 校验是否已在借阅车中（唯一约束防止重复）
        if (cartItemRepository.findByReaderIdAndBookId(readerId, bookId).isPresent()) {
            throw new IllegalArgumentException("该书已在借阅车中");
        }

        // 创建借阅车项
        CartItem item = new CartItem(reader, book);
        cartItemRepository.save(item);
        return toResponse(item);
    }

    /**
     * 从借阅车移除
     *
     * @param readerId 读者 ID（校验归属）
     * @param itemId   借阅车项 ID
     * @throws IllegalArgumentException 项不存在或非本人项
     */
    public void removeFromCart(Long readerId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("借阅车项不存在"));
        // 校验归属
        if (!item.getReader().getId().equals(readerId)) {
            throw new IllegalArgumentException("无权操作他人借阅车");
        }
        cartItemRepository.delete(item);
    }

    /**
     * 查看借阅车
     *
     * @param readerId 读者 ID
     * @return 借阅车项列表
     */
    public List<CartItemResponse> getCart(Long readerId) {
        return cartItemRepository.findByReaderIdOrderByCreateTimeDesc(readerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 统计借阅车数量
     *
     * @param readerId 读者 ID
     * @return 借阅车项数量
     */
    public long getCartCount(Long readerId) {
        return cartItemRepository.countByReaderId(readerId);
    }

    /**
     * 提交借阅：将借阅车中的全部图书批量借阅
     * <p>
     * 逐本调用 BorrowService.borrowBook，单本失败不影响其他图书（容错处理）。
     * 借阅成功后清空借阅车。
     * </p>
     *
     * @param readerId 读者 ID
     * @return 借阅成功数量
     */
    @Transactional
    public int submitBorrow(Long readerId) {
        List<CartItem> cartItems = cartItemRepository.findByReaderIdOrderByCreateTimeDesc(readerId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("借阅车为空");
        }

        int successCount = 0;
        // 逐本借阅，单本失败不影响其他（容错处理，避免批量回滚）
        for (CartItem item : cartItems) {
            try {
                borrowService.borrowBook(readerId, item.getBook().getId());
                successCount++;
            } catch (IllegalArgumentException e) {
                // 单本借阅失败（如达到上限、无副本），跳过继续处理下一本
                // 失败原因可通过返回的 successCount 与总数差异推断
            }
        }

        // 借阅成功后清空借阅车
        if (successCount > 0) {
            cartItemRepository.deleteByReaderId(readerId);
        }

        return successCount;
    }

    /**
     * 实体转响应 DTO
     *
     * @param item 借阅车项实体
     * @return 借阅车项响应 DTO
     */
    private CartItemResponse toResponse(CartItem item) {
        CartItemResponse resp = new CartItemResponse();
        resp.setId(item.getId());

        Book book = item.getBook();
        if (book != null) {
            resp.setBookId(book.getId());
            resp.setBookTitle(book.getTitle());
            resp.setBookAuthor(book.getAuthor());
            resp.setAvailableCopies(book.getAvailableCopies());
            if (book.getCategory() != null) {
                resp.setCategoryName(book.getCategory().getName());
            }
            if (book.getCoverImage() != null) {
                resp.setCoverImage(book.getCoverImage());
            }
        }

        resp.setCreateTime(item.getCreateTime() != null ? item.getCreateTime().toString() : null);
        return resp;
    }
}
