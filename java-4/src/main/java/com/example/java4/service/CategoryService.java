// 声明包路径，存放业务服务层
package com.example.java4.service;

// 导入 DTO 与实体类
import com.example.java4.dto.CategoryRequest;
import com.example.java4.dto.CategoryResponse;
import com.example.java4.model.BookCategory;

// 导入 Repository
import com.example.java4.repository.BookCategoryRepository;
import com.example.java4.repository.BookRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书分类业务服务
 * <p>
 * 提供分类的 CRUD 与列表查询能力，查询时附带各分类下图书数量。
 * </p>
 */
@Service // 声明为 Spring 服务组件
public class CategoryService {

    /** 分类仓储 */
    private final BookCategoryRepository categoryRepository;

    /** 图书仓储（用于统计分类下图书数量） */
    private final BookRepository bookRepository;

    /**
     * 构造方法注入依赖
     */
    public CategoryService(BookCategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * 查询全部分类（按排序序号升序）
     *
     * @return 分类响应列表
     */
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据 ID 获取分类
     *
     * @param id 分类 ID
     * @return 分类实体（不存在返回 null）
     */
    public BookCategory getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    /**
     * 新增分类（管理端）
     *
     * @param req 分类请求
     * @return 新创建的分类响应
     * @throws IllegalArgumentException 分类名称已存在
     */
    public CategoryResponse createCategory(CategoryRequest req) {
        // 检查分类名称是否重复
        if (categoryRepository.existsByName(req.getName())) {
            throw new IllegalArgumentException("分类名称已存在");
        }
        BookCategory category = new BookCategory(
                req.getName(),
                req.getDescription(),
                req.getSortOrder() != null ? req.getSortOrder() : 0
        );
        return toResponse(categoryRepository.save(category));
    }

    /**
     * 修改分类（管理端）
     *
     * @param id  分类 ID
     * @param req 分类请求
     * @return 修改后的分类响应
     * @throws IllegalArgumentException 分类不存在或名称重复
     */
    public CategoryResponse updateCategory(Long id, CategoryRequest req) {
        BookCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        // 如果修改了名称，检查新名称是否与其他分类重复
        if (!category.getName().equals(req.getName()) && categoryRepository.existsByName(req.getName())) {
            throw new IllegalArgumentException("分类名称已存在");
        }
        category.setName(req.getName());
        category.setDescription(req.getDescription());
        if (req.getSortOrder() != null) {
            category.setSortOrder(req.getSortOrder());
        }
        return toResponse(categoryRepository.save(category));
    }

    /**
     * 删除分类（管理端）
     *
     * @param id 分类 ID
     * @throws IllegalArgumentException 分类不存在或分类下仍有图书
     */
    public void deleteCategory(Long id) {
        BookCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        // 检查分类下是否还有图书，有则不允许删除
        long bookCount = bookRepository.findByCategoryId(id, Pageable.ofSize(1)).getTotalElements();
        if (bookCount > 0) {
            throw new IllegalArgumentException("该分类下仍有图书，无法删除");
        }
        categoryRepository.delete(category);
    }

    /**
     * 实体转响应 DTO
     *
     * @param category 分类实体
     * @return 分类响应 DTO
     */
    private CategoryResponse toResponse(BookCategory category) {
        CategoryResponse resp = new CategoryResponse();
        resp.setId(category.getId());
        resp.setName(category.getName());
        resp.setDescription(category.getDescription());
        resp.setSortOrder(category.getSortOrder());
        // 统计该分类下图书数量
        resp.setBookCount(bookRepository.findByCategoryId(category.getId(), Pageable.ofSize(1)).getTotalElements());
        resp.setUpdateTime(category.getUpdateTime());
        return resp;
    }
}
