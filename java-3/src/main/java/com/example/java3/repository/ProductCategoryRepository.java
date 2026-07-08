// 声明该接口所在的包路径，归属于 java3 项目的 repository 持久层
package com.example.java3.repository;

// 导入 ProductCategory 实体类，对应商品分类表
import com.example.java3.model.ProductCategory;
// 导入 Spring Data JPA 仓储接口，提供基础 CRUD 能力
import org.springframework.data.jpa.repository.JpaRepository;
// 导入 @Repository 注解，标识为持久层组件，由 Spring 容器管理
import org.springframework.stereotype.Repository;

// 导入 List 集合类，用于承载多条查询结果
import java.util.List;
// 导入 Optional 容器类，用于安全包装可能为 null 的单条查询结果
import java.util.Optional;

/**
 * 商品分类仓储
 */
// 标识为持久层组件，Spring 启动时扫描并生成代理实现
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    /**
     * 按排序序号升序查询全部分类
     *
     * @return 分类列表
     */
    // 查询全部分类，按 sortOrder 升序排列，便于前端按预设顺序展示分类导航
    List<ProductCategory> findAllByOrderBySortOrderAsc();

    /**
     * 按名称查询分类
     *
     * @param name 分类名称
     * @return 分类 Optional
     */
    // 按分类名称查询分类，用于校验分类名是否已存在或获取特定分类信息
    // 返回 Optional 防止空指针
    Optional<ProductCategory> findByName(String name);
}
