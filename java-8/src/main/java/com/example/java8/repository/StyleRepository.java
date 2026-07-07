// 声明包路径：repository 层封装数据访问
package com.example.java8.repository;

// 导入款式实体：包含类别、工费、基础价格等属性
import com.example.java8.model.Style;
// 导入 Spring Data JPA 基础接口：自动提供基础 CRUD
import org.springframework.data.jpa.repository.JpaRepository;

// 导入 List：用于返回多条记录的结果集
import java.util.List;

/**
 * 款式仓储
 *
 * <p>提供款式的持久化访问与按类别筛选能力。
 * 款式与面料组合后决定订单总价（款式基础价 + 面料价 + 工费）。</p>
 */
public interface StyleRepository extends JpaRepository<Style, Long> {

    /**
     * 按类别查询款式
     *
     * <p>用于前台按"西装/衬衫/西裤"等类别浏览款式。
     * 框架自动翻译为 {@code SELECT * FROM style WHERE category = ?}。</p>
     *
     * @param category 类别（如西装、衬衫、西裤）
     * @return 款式列表
     */
    // 方法名约定：findBy + 字段名，框架据此生成 WHERE category = ? 条件
    List<Style> findByCategory(String category);
}
