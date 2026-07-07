// 声明包路径，归类为 controller 控制器层，承接 HTTP 请求并调用 service
package com.example.java1.controller;

// 以下导入本模块内的 DTO 与服务，遵循分层架构：controller 不直接访问 repository
import com.example.java1.dto.StatsResponse; // 引入统计响应 DTO，封装首页看板所需的聚合数据
import com.example.java1.service.StatsService; // 引入统计服务，封装首页数据聚合逻辑
import org.springframework.http.ResponseEntity; // 引入响应实体，可灵活控制状态码与响应体
import org.springframework.web.bind.annotation.GetMapping; // 引入 @GetMapping，映射 HTTP GET 请求
import org.springframework.web.bind.annotation.RequestMapping; // 引入 @RequestMapping，定义控制器根路径
import org.springframework.web.bind.annotation.RestController; // 引入 @RestController，声明 RESTful 控制器

/**
 * 统计控制器
 * <p>
 * 提供首页数据看板聚合接口。公开访问（GET 请求）。
 * </p>
 * <p>
 * 设计说明：首页看板需展示图书总数、借阅/归还/逾期数量等汇总指标，
 * 聚合计算下沉至 StatsService 统一处理，避免控制器承载业务逻辑。
 * </p>
 */
@RestController // 声明为 RESTful 控制器，返回值自动序列化为 JSON，不渲染视图
@RequestMapping("/api/stats") // 统一前缀 /api/stats，遵循 RESTful 资源命名约定
public class StatsController {

    private final StatsService statsService; // 统计服务依赖，final 保证构造后不可变

    /**
     * 构造方法注入
     * <p>采用构造方法注入而非字段注入，便于单元测试 Mock 且符合 Spring 推荐实践。</p>
     *
     * @param statsService 统计服务
     */
    public StatsController(StatsService statsService) { // 构造方法注入依赖
        this.statsService = statsService; // 赋值统计服务
    }

    /**
     * 获取首页统计数据
     *
     * @return 统计响应
     */
    @GetMapping // 映射 GET /api/stats，查询为幂等读操作，由拦截器放行，首页无需登录即可浏览概览
    public ResponseEntity<StatsResponse> stats() { // 无参数，返回聚合统计数据
        return ResponseEntity.ok(statsService.getStats()); // 委托服务层聚合查询并以 200 返回
    }
}
