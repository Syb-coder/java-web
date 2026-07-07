// 声明包路径：repository 层负责数据访问抽象
package com.example.java8.repository;

// 导入面料实体：包含材质、颜色、价格等属性
import com.example.java8.model.Fabric;
// 导入 Spring Data JPA 基础接口：提供基础 CRUD 与分页查询能力
import org.springframework.data.jpa.repository.JpaRepository;

// 导入 List：用于返回多条记录的查询结果集
import java.util.List;

/**
 * 面料仓储
 *
 * <p>提供面料的持久化访问与按属性筛选能力。
 * 自定义查询方法遵循 Spring Data JPA 方法名约定，框架自动生成 SQL。</p>
 */
public interface FabricRepository extends JpaRepository<Fabric, Long> {

    /**
     * 按材质筛选面料
     *
     * <p>例如查询所有"羊毛"材质的面料。
     * 框架自动翻译为 {@code SELECT * FROM fabric WHERE material = ?}。</p>
     *
     * @param material 材质（如羊毛、棉、丝绸）
     * @return 面料列表
     */
    // 方法名约定：findBy + 字段名，框架据此生成 WHERE material = ? 条件
    List<Fabric> findByMaterial(String material);

    /**
     * 按材质与颜色筛选面料
     *
     * <p>支持多维度联合筛选，例如"羊毛+黑色"。
     * 方法名中的 And 表示 AND 逻辑，框架翻译为：
     * {@code SELECT * FROM fabric WHERE material = ? AND color = ?}。</p>
     *
     * @param material 材质
     * @param color    颜色
     * @return 面料列表
     */
    // 方法名约定：findBy + 字段1 + And + 字段2，框架生成多条件 AND 查询
    List<Fabric> findByMaterialAndColor(String material, String color);
}
