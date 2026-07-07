// 声明包路径
package com.example.java2.repository;

// 导入实体类与 Spring Data JPA 接口
import com.example.java2.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

// 导入集合类
import java.util.List;

/**
 * 测试题目仓储
 * <p>
 * 继承 {@link JpaRepository} 自动获得 CRUD 能力。
 * 提供按分类查询题目、随机抽题等查询方法。
 * </p>
 */
// 继承 JpaRepository 即可获得 save/findAll/getById/deleteById 等通用 CRUD 方法，
// Spring Data JPA 启动时通过 JDK 动态代理生成 SimpleJpaRepository 实现类，无需手写 Impl。
// 泛型参数：<Question> 表示管理的实体类型；<Long> 表示主键类型（与 Question.id 字段一致）。
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * 按分类查询全部题目（管理端列表）
     *
     * @param categoryId 分类 ID
     * @return 题目列表
     */
    // 方法名约定解析：findBy + 字段名(CategoryId)
    //   → Spring Data 解析为 SELECT * FROM question WHERE category_id = ?
    // 业务场景：管理端按分类筛选题目列表，便于教研人员维护题库。
    // 返回 List：题库规模通常可控（单分类几十到几百题），暂不分页；如规模增长可加 Pageable。
    List<Question> findByCategoryId(Long categoryId);

    /**
     * 统计某分类下的题目数量（用于删除分类前占用检查）
     *
     * @param categoryId 分类 ID
     * @return 题目数量
     */
    // 方法名约定解析：countBy + 字段名(CategoryId)
    //   → Spring Data 解析为 SELECT COUNT(*) FROM question WHERE category_id = ?
    // 业务场景：删除分类前校验是否还有题目引用，避免出现无分类的孤儿题目。
    long countByCategoryId(Long categoryId);
}
