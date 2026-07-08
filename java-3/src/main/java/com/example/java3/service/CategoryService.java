package com.example.java3.service;

import com.example.java3.dto.CategoryRequest;
import com.example.java3.dto.CategoryResponse;
import com.example.java3.model.ProductCategory;
import com.example.java3.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品分类服务
 */
@Service
public class CategoryService {

    private final ProductCategoryRepository categoryRepository;

    public CategoryService(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 查询全部分类（按排序序号升序）
     *
     * @return 分类响应列表
     */
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(CategoryResponse::new)
                .toList();
    }

    /**
     * 新增分类
     *
     * @param req 分类请求
     * @return 分类响应
     */
    public CategoryResponse create(CategoryRequest req) {
        if (categoryRepository.findByName(req.getName()).isPresent()) {
            throw new IllegalArgumentException("分类名称已存在");
        }
        ProductCategory c = new ProductCategory(req.getName(), req.getIcon(), req.getSortOrder());
        categoryRepository.save(c);
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
        ProductCategory c = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        c.setName(req.getName());
        c.setIcon(req.getIcon());
        c.setSortOrder(req.getSortOrder());
        categoryRepository.save(c);
        return new CategoryResponse(c);
    }

    /**
     * 删除分类
     *
     * @param id 分类 ID
     */
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    /**
     * 按 ID 查询分类
     *
     * @param id 分类 ID
     * @return 分类实体
     */
    public ProductCategory findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
    }
}
