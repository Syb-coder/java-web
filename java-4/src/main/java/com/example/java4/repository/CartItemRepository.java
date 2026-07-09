// 声明包路径，存放 Spring Data JPA Repository 接口
package com.example.java4.repository;

// 导入实体类与 JPA 注解
import com.example.java4.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 借阅车数据访问层
 * <p>
 * 提供借阅车项的 CRUD、按读者查询、判重查询、清空等能力。
 * </p>
 */
@Repository // 声明本接口为 Spring Bean
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * 按读者 ID 查询借阅车全部项（按加入时间降序）
     *
     * @param readerId 读者 ID
     * @return 借阅车项列表
     */
    List<CartItem> findByReaderIdOrderByCreateTimeDesc(Long readerId);

    /**
     * 按读者 ID 和图书 ID 查询借阅车项（判重使用）
     *
     * @param readerId 读者 ID
     * @param bookId   图书 ID
     * @return 借阅车项（可能为空）
     */
    Optional<CartItem> findByReaderIdAndBookId(Long readerId, Long bookId);

    /**
     * 统计读者借阅车中的图书数量
     *
     * @param readerId 读者 ID
     * @return 借阅车项数量
     */
    long countByReaderId(Long readerId);

    /**
     * 删除指定读者的全部借阅车项（提交借阅后清空使用）
     *
     * @param readerId 读者 ID
     */
    void deleteByReaderId(Long readerId);
}
