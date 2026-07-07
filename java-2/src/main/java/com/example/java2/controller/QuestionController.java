// 声明包路径
package com.example.java2.controller;

// 导入 DTO 与实体类
import com.example.java2.dto.QuestionForTest;
import com.example.java2.dto.QuestionRequest;
import com.example.java2.dto.QuestionResponse;
import com.example.java2.service.QuestionService;

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
 * 题目控制器
 * <p>
 * 提供：
 * <ul>
 *   <li>前台抽题接口（GET /api/questions/test）：用户答题前拉取随机题目；</li>
 *   <li>后台题库 CRUD 接口（/api/questions/admin/**）：管理员维护题库。</li>
 * </ul>
 * </p>
 */
// @RestController = @Controller + @ResponseBody：返回值自动经 Jackson 序列化为 JSON，无需逐方法标注 @ResponseBody
@RestController
// 路径设计遵循 RESTful 约定：/api/questions 以"题目"资源集合为根，
// 前台抽题接口挂在 /test 子路径，后台 CRUD 挂在 /admin 子路径下区分权限边界
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * 前台抽题（自测用）
     *
     * @param categoryId 分类 ID（0 或不传表示跨分类综合测试）
     * @param size       抽题数量（默认 5）
     * @return 答题用题目列表
     */
    @GetMapping("/test")
    // GET 前台抽题：无副作用幂等读，从题库随机抽取题目供用户自测，符合 RESTful 用 GET 表达资源获取的约定
    public ResponseEntity<List<QuestionForTest>> pickForTest(
            @RequestParam(defaultValue = "0") Long categoryId,
            @RequestParam(defaultValue = "5") int size) {
        // 200 OK 携带抽题结果返回（不含答案，防用户提前查看）
        return ResponseEntity.ok(questionService.pickForTest(categoryId, size));
    }

    // ===== 后台管理接口 =====

    /**
     * 后台题库列表
     *
     * @param categoryId 分类 ID（可选，按分类筛选）
     * @return 题目列表
     */
    @GetMapping("/admin")
    // GET 后台题库列表：/admin 子路径区分后台权限边界，由拦截器 requireAdmin 强制校验管理员登录
    public ResponseEntity<List<QuestionResponse>> adminList(
            @RequestParam(required = false) Long categoryId) {
        // 200 OK 携带题目列表返回（含答案，供管理员维护）
        return ResponseEntity.ok(questionService.listAll(categoryId));
    }

    /**
     * 后台根据 ID 查询题目
     *
     * @param id 题目 ID
     * @return 题目响应
     */
    @GetMapping("/admin/{id}")
    // GET 后台查询单个题目：/{id} 路径变量定位具体题目，符合 RESTful 用路径表达资源定位的约定
    public ResponseEntity<QuestionResponse> adminGet(@PathVariable Long id) {
        com.example.java2.model.Question q = questionService.getById(id);
        if (q == null) {
            // 题目不存在返回 404
            return ResponseEntity.notFound().build();
        }
        // 200 OK 携带题目详情返回（第二个参数 null 表示不附带答题统计）
        return ResponseEntity.ok(QuestionResponse.from(q, null));
    }

    /**
     * 新增题目
     *
     * @param req 题目请求
     * @return 题目响应
     */
    @PostMapping("/admin")
    // POST 后台新增题目：向 /admin 集合根 POST 创建新题目，符合 RESTful 用 POST 表达资源创建的约定
    public ResponseEntity<QuestionResponse> adminCreate(@Valid @RequestBody QuestionRequest req) {
        // @Valid 触发 Bean Validation，校验 QuestionRequest 字段约束（如题干 @NotBlank、答案选项完整性），失败返回 400
        try {
            // 200 OK 携带新建题目响应返回
            return ResponseEntity.ok(questionService.create(req));
        } catch (IllegalArgumentException e) {
            // 参数错误（如分类不存在或答案索引越界）返回 400
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新题目
     */
    @PutMapping("/admin/{id}")
    // PUT 后台更新题目：向 /admin/{id} PUT 整体替换题目属性，符合 RESTful 用 PUT 表达资源完整更新的约定
    public ResponseEntity<QuestionResponse> adminUpdate(
            @PathVariable Long id, @Valid @RequestBody QuestionRequest req) {
        try {
            // 200 OK 携带更新后的题目响应返回
            return ResponseEntity.ok(questionService.update(id, req));
        } catch (IllegalArgumentException e) {
            // 参数错误（如题目不存在或分类无效）返回 400
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除题目
     *
     * @param id 题目 ID
     * @return 204（成功） / 404（题目不存在）
     */
    @DeleteMapping("/admin/{id}")
    // DELETE 后台删除题目：向 /admin/{id} DELETE 移除题目，符合 RESTful 用 DELETE 表达资源删除的约定
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) {
        try {
            questionService.delete(id);
            // 204 No Content 表示删除成功且无响应体，符合 RESTful 删除资源语义
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            // 题目不存在返回 404
            return ResponseEntity.notFound().build();
        }
    }
}
