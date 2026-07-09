// 声明包路径，存放 Spring Data JPA Repository 接口
package com.example.java4.repository;

// 导入实体类与 JPA 注解
import com.example.java4.model.Reader;
import com.example.java4.model.ReaderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 读者数据访问层
 * <p>
 * 提供读者的 CRUD 与按学号/工号查询、关键字搜索、按类型统计等能力。
 * </p>
 */
@Repository // 声明本接口为 Spring Bean
public interface ReaderRepository extends JpaRepository<Reader, Long> {

    /**
     * 根据学号/工号查找读者（登录校验使用）
     *
     * @param readerNo 学号/工号
     * @return 读者实体（可能为空）
     */
    Optional<Reader> findByReaderNo(String readerNo);

    /**
     * 检查学号/工号是否已注册
     *
     * @param readerNo 学号/工号
     * @return true 表示已注册
     */
    boolean existsByReaderNo(String readerNo);

    /**
     * 按姓名或学号关键字分页搜索读者（后台管理使用）
     *
     * @param name     姓名关键字
     * @param readerNo 学号关键字
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Reader> findByNameContainingOrReaderNoContaining(String name, String readerNo, Pageable pageable);

    /**
     * 按读者类型统计人数（数据统计使用）
     *
     * @param type 读者类型
     * @return 该类型的读者数量
     */
    long countByType(ReaderType type);
}
