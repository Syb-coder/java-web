// 声明包路径，存放控制器层
package com.example.java4.controller;

// 导入 DTO 与 Service
import com.example.java4.dto.CategoryResponse;
import com.example.java4.service.CategoryService;

// 导入 Spring 工具
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类控制器（用户端查询）
 * <p>
 * 提供分类列表查询接口，无需登录即可访问。
 * </p>
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    /** 分类服务 */
    private final CategoryService categoryService;

    /**
     * 构造方法注入依赖
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 获取全部分类列表（按排序序号升序）
     *
     * @return 分类响应列表
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}
