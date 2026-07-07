// 声明包路径：repository 层封装数据访问
package com.example.java8.repository;

// 导入量体数据实体：包含用户 11 项身体尺寸数据
import com.example.java8.model.Measurement;
// 导入 Spring Data JPA 基础接口：自动提供基础 CRUD
import org.springframework.data.jpa.repository.JpaRepository;

// 导入 List：用于返回多条记录的结果集
import java.util.List;

/**
 * 量体数据仓储
 *
 * <p>提供量体数据的持久化访问与按用户筛选能力。
 * 一个用户可有多条量体记录（如不同时间点、不同体型变化），
 * 通过外键 user_id 关联至 user 表。</p>
 */
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    /**
     * 按用户 ID 查询量体数据
     *
     * <p>框架自动翻译为 {@code SELECT * FROM measurement WHERE user_id = ?}。
     * 由于 Measurement 实体中已通过 @ManyToOne 定义与 User 的关联，
     * 框架会自动识别 user_id 为关联字段。</p>
     *
     * @param userId 用户 ID
     * @return 该用户的所有量体数据
     */
    // 方法名约定：findBy + 关联实体字段名 + Id（首字母大写），框架据此生成基于外键的查询
    List<Measurement> findByUserId(Long userId);
}
