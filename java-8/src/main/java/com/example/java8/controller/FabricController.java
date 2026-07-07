package com.example.java8.controller;

import com.example.java8.dto.FabricRequest;
import com.example.java8.dto.FabricResponse;
import com.example.java8.service.FabricService;
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
 * 面料控制器
 * <p>
 * GET 接口放行（前台浏览），POST/PUT/DELETE 接口需管理员登录（由拦截器统一校验）。
 * </p>
 */
@RestController
@RequestMapping("/api/fabrics")
public class FabricController {

    private final FabricService fabricService;

    public FabricController(FabricService fabricService) {
        this.fabricService = fabricService;
    }

    /**
     * 查询面料列表（支持筛选）
     *
     * @param material 材质
     * @param color    颜色
     * @param maxPrice 单价上限
     * @return 面料响应列表
     */
    @GetMapping
    public ResponseEntity<List<FabricResponse>> list(
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Double maxPrice) {
        return ResponseEntity.ok(fabricService.list(material, color, maxPrice));
    }

    /**
     * 新增面料
     *
     * @param req 面料请求
     * @return 201 + 新建响应
     */
    @PostMapping
    public ResponseEntity<FabricResponse> create(@Valid @RequestBody FabricRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fabricService.create(req));
    }

    /**
     * 更新面料
     *
     * @param id  面料 ID
     * @param req 面料请求
     * @return 200（成功） / 404（不存在）
     */
    @PutMapping("/{id}")
    public ResponseEntity<FabricResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody FabricRequest req) {
        FabricResponse resp = fabricService.update(id, req);
        if (resp == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resp);
    }

    /**
     * 删除面料
     *
     * @param id 面料 ID
     * @return 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fabricService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
