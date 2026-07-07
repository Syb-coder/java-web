package com.example.java8.controller;

import com.example.java8.dto.StyleRequest;
import com.example.java8.dto.StyleResponse;
import com.example.java8.service.StyleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

import java.util.List;

/**
 * 款式控制器
 */
@RestController
@RequestMapping("/api/styles")
public class StyleController {

    private final StyleService styleService;

    public StyleController(StyleService styleService) {
        this.styleService = styleService;
    }

    /**
     * 查询款式列表（支持筛选）
     *
     * @param category 类别
     * @param keyword  名称关键词
     * @return 款式响应列表
     */
    @GetMapping
    public ResponseEntity<List<StyleResponse>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(styleService.list(category, keyword));
    }

    /**
     * 新增款式
     *
     * @param req 款式请求
     * @return 201 + 新建响应
     */
    @PostMapping
    public ResponseEntity<StyleResponse> create(@Valid @RequestBody StyleRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(styleService.create(req));
    }

    /**
     * 更新款式
     *
     * @param id  款式 ID
     * @param req 款式请求
     * @return 200（成功） / 404（不存在）
     */
    @PutMapping("/{id}")
    public ResponseEntity<StyleResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody StyleRequest req) {
        StyleResponse resp = styleService.update(id, req);
        if (resp == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resp);
    }

    /**
     * 删除款式
     *
     * @param id 款式 ID
     * @return 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        styleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
