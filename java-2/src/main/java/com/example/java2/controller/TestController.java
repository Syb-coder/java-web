// 声明包路径
package com.example.java2.controller;

// 导入 DTO 与实体类
import com.example.java2.dto.SubmitAnswerRequest;
import com.example.java2.dto.TestRecordResponse;
import com.example.java2.dto.TestResultResponse;
import com.example.java2.model.User;
import com.example.java2.service.TestService;

// 导入 Spring Web 注解
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * 自测答题控制器
 * <p>
 * 提交答案判分接口与答题历史查询接口。所有接口均需前台用户登录。
 * </p>
 */
// @RestController = @Controller + @ResponseBody：返回值自动经 Jackson 序列化为 JSON，无需逐方法标注 @ResponseBody
@RestController
// 路径设计遵循 RESTful 约定：/api/test 以"自测答题"资源域为根，
// 子路径 submit/records 表达提交答题与查询历史的子操作，所有接口均需前台用户登录
@RequestMapping("/api/test")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    /**
     * 提交答题并判分
     *
     * @param req  答题提交请求
     * @param user 当前登录用户
     * @return 答题结果响应
     */
    @PostMapping("/submit")
    // POST 提交答题：状态变更操作（写答题记录、更新正确率统计），符合 RESTful 用 POST 表达动作执行的约定
    public ResponseEntity<TestResultResponse> submit(
            @Valid @RequestBody SubmitAnswerRequest req,
            // @SessionAttribute 工作原理：Spring MVC 从 HttpSession 按 key 取属性注入参数，
            // required = false 让未登录用户进入方法体注入 null，便于方法内统一返回 401
            @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user) {
        // 双重身份校验：答题记录需归属登录用户，未登录返回 401，确保成绩可追溯且防刷分
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        // 200 OK 携带判分结果返回（含正确数、错误数、各题解析）
        return ResponseEntity.ok(testService.submit(user.getId(), req));
    }

    /**
     * 查询当前用户答题历史
     *
     * @param page 页码
     * @param size 每页条数
     * @param user 当前登录用户
     * @return 答题记录分页
     */
    @GetMapping("/records")
    // GET 查询答题历史：幂等读操作，查询当前用户的答题记录分页，符合 RESTful 用 GET 表达资源获取的约定
    public ResponseEntity<Page<TestRecordResponse>> records(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            // @SessionAttribute 工作原理：从 HttpSession 按 key 取属性注入参数，
            // required = false 让未登录用户进入方法体注入 null，便于方法内统一返回 401
            @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user) {
        // 双重身份校验：答题历史属个人数据，未登录返回 401，防止匿名枚举他人成绩
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        // 200 OK 携带答题记录分页返回（仅当前用户自己的记录，service 按 userId 过滤）
        return ResponseEntity.ok(testService.listByUser(user.getId(), page, size));
    }
}
