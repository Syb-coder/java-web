// 声明当前类所在包路径
package com.example.java3.service;

// 导入分类请求 DTO
import com.example.java3.dto.CategoryRequest;
// 导入分类响应 DTO
import com.example.java3.dto.CategoryResponse;
// 导入商品分类实体模型
import com.example.java3.model.ProductCategory;
// 导入商品分类仓储接口（Spring Data JPA）
import com.example.java3.repository.ProductCategoryRepository;
// 导入 @Service 注解，标记为业务层 Bean
import org.springframework.stereotype.Service;

// 导入 List 集合
import java.util.List;

/**
 * 商品分类服务
 */
// 标识为业务层组件，由 Spring 容器管理为单例
@Service
public class CategoryService {

    // 商品分类仓储，通过构造方法注入
    private final ProductCategoryRepository categoryRepository;

    // 构造方法注入：Spring 自动注入分类仓储 Bean
    public CategoryService(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 查询全部分类（按排序序号升序）
     *
     * @return 分类响应列表
     */
    public List<CategoryResponse> findAll() {
        // 调用自定义查询方法，按 sortOrder 升序返回，确保前台展示顺序可控
        return categoryRepository.findAllByOrderBySortOrderAsc()
                // 转 Stream 以便逐个映射
                .stream()
                // 用构造方法引用将每个实体转为响应 DTO
                .map(CategoryResponse::new)
                // 收集为 List
                .toList();
    }

    /**
     * 新增分类
     *
     * @param req 分类请求
     * @return 分类响应
     */
    public CategoryResponse create(CategoryRequest req) {
        // 校验分类名称唯一，避免重名导致前台展示混乱
        if (categoryRepository.findByName(req.getName()).isPresent()) {
            // 已存在则抛异常
            throw new IllegalArgumentException("分类名称已存在");
        }
        // 构造分类实体并填入名称、图标、排序序号
        ProductCategory c = new ProductCategory(req.getName(), req.getIcon(), req.getSortOrder());
        // 持久化到数据库
        categoryRepository.save(c);
        // 返回响应 DTO
        return new CategoryResponse(c);
    }

    /**
     * 更新分类
     *
     * @param id  分类 ID
     * @param req 分类请求
     * @return 分类响应
     */
    public CategoryResponse update(Long id, CategoryRequest req) {
        // 根据 ID 查询分类
        ProductCategory c = categoryRepository.findById(id)
                // 不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        // 更新名称
        c.setName(req.getName());
        // 更新图标
        c.setIcon(req.getIcon());
        // 更新排序序号
        c.setSortOrder(req.getSortOrder());
        // 持久化更新
        categoryRepository.save(c);
        // 返回响应 DTO
        return new CategoryResponse(c);
    }

    /**
     * 删除分类
     *
     * @param id 分类 ID
     */
    public void delete(Long id) {
        // 调用 JPA 默认方法按 ID 删除分类
        categoryRepository.deleteById(id);
    }

    /**
     * 按 ID 查询分类
     *
     * @param id 分类 ID
     * @return 分类实体
     */
    public ProductCategory findById(Long id) {
        // 根据 ID 查询分类
        return categoryRepository.findById(id)
                // 不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
    }
}
