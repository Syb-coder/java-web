// 声明包路径
package com.example.java2.controller;

// 导入 DTO 与实体类
import com.example.java2.dto.CategoryRequest;
import com.example.java2.dto.CategoryResponse;
import com.example.java2.service.CategoryService;

// 导入 Spring Web 注解
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 导入集合类
import java.util.List;

/**
 * 分类控制器
 * <p>
 * GET 接口对所有用户开放（前台导览+后台列表共用）；POST/PUT/DELETE 仅管理员可调用（由拦截器保证）。
 * </p>
 */
// @RestController = @Controller + @ResponseBody：返回值自动经 Jackson 序列化为 JSON，无需逐方法标注 @ResponseBody
@RestController
// 路径设计遵循 RESTful 约定：/api/categories 以"分类"资源集合为根，
// 子路径用 HTTP 方法区分操作语义（GET 查询、POST 新增、PUT 更新、DELETE 删除），/{id} 定位单个资源
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 查询全部分类
     *
     * @param withArticleCount 是否附带文章数
     * @return 分类列表
     */
    @GetMapping
    // GET 查询资源集合：无副作用幂等读，符合 RESTful 用 GET 表达列表获取的约定
    public ResponseEntity<List<CategoryResponse>> list(
            @RequestParam(defaultValue = "false") boolean withArticleCount) {
        // withArticleCount 默认 false：前台导航仅需分类名，避免每次查询都触发文章数 COUNT 聚合的开销
        return ResponseEntity.ok(categoryService.listAll(withArticleCount));
    }

    /**
     * 根据 ID 查询分类
     *
     * @param id 分类 ID
     * @return 分类响应（不存在返回 404）
     */
    @GetMapping("/{id}")
    // GET 查询单个资源：/{id} 路径变量定位具体分类，符合 RESTful 用路径表达资源定位的约定
    public ResponseEntity<CategoryResponse> get(@PathVariable Long id) {
        com.example.java2.model.Category c = categoryService.getById(id);
        if (c == null) {
            // 资源不存在返回 404，符合 RESTful 语义；不返回 200+空对象避免前端空指针
            return ResponseEntity.notFound().build();
        }
        // 200 OK 携带分类详情返回
        return ResponseEntity.ok(CategoryResponse.from(c));
    }

    /**
     * 新增分类（管理员）
     *
     * @param req 分类请求
     * @return 分类响应（成功）或 400（名称冲突）
     */
    @PostMapping
    // POST 新增资源：向集合根路径 POST 创建新分类，符合 RESTful 用 POST 表达资源创建的约定
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest req) {
        // @Valid 在方法进入前触发 Bean Validation，校验 CategoryRequest 字段约束（如分类名 @NotBlank），
        // 失败抛 MethodArgumentNotValidException 返回 400，拦截非法参数进入 service 层
        try {
            // 200 OK 携带新建分类返回（严格 RESTful 应返回 201 Created，此处简化统一用 200）
            return ResponseEntity.ok(categoryService.create(req));
        } catch (IllegalArgumentException e) {
            // 分类名重复等业务约束冲突返回 400，区别于 409：service 用 IllegalArgumentException 表达参数级错误
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新分类（管理员）
     *
     * @param id  分类 ID
     * @param req 分类请求
     * @return 分类响应（成功）或 400（参数错误）
     */
    @PutMapping("/{id}")
    // PUT 更新资源：向 /{id} 路径 PUT 整体替换分类属性，符合 RESTful 用 PUT 表达资源完整更新的约定
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody CategoryRequest req) {
        try {
            // 200 OK 携带更新后的分类返回
            return ResponseEntity.ok(categoryService.update(id, req));
        } catch (IllegalArgumentException e) {
            // 名称冲突或分类不存在均归为参数错误返回 400，避免暴露内部资源存在性
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除分类（管理员）
     *
     * @param id 分类 ID
     * @return 204（成功） / 400（分类下仍有数据） / 404（分类不存在）
     */
    @DeleteMapping("/{id}")
    // DELETE 删除资源：向 /{id} 路径 DELETE 移除分类，符合 RESTful 用 DELETE 表达资源删除的约定
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            // 删除成功返回 204 No Content，符合 RESTful 删除资源语义
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            // 分类下仍有文章或题目
            // 选用 400 而非 409 Conflict：state 异常表示业务前置条件未满足，需要先迁移子数据
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            // 分类不存在返回 404，区分"资源缺失"与"业务约束冲突"
            return ResponseEntity.notFound().build();
        }
    }
}
