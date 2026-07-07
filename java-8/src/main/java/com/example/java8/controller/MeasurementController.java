package com.example.java8.controller;

import com.example.java8.controller.UserController;
import com.example.java8.dto.MeasurementRequest;
import com.example.java8.dto.MeasurementResponse;
import com.example.java8.model.User;
import com.example.java8.service.MeasurementService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

/**
 * 量体数据控制器
 * <p>所有接口均要求前台用户登录（由拦截器放行 GET，写操作需要登录）。</p>
 */
@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    /**
     * 查询当前用户的全部量体数据
     *
     * @param user 当前登录用户
     * @return 量体数据响应列表
     */
    @GetMapping
    public ResponseEntity<List<MeasurementResponse>> list(
            @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(measurementService.listByUser(user.getId()));
    }

    /**
     * 新增量体数据
     *
     * @param user 当前登录用户
     * @param req  量体请求
     * @return 201 + 新建响应
     */
    @PostMapping
    public ResponseEntity<MeasurementResponse> create(
            @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user,
            @Valid @RequestBody MeasurementRequest req) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(measurementService.create(user, req));
    }

    /**
     * 更新当前用户的量体数据
     *
     * @param id        量体数据 ID
     * @param user      当前登录用户
     * @param req       量体请求
     * @return 200（成功） / 401（未登录） / 404（不存在或越权）
     */
    @PutMapping("/{id}")
    public ResponseEntity<MeasurementResponse> update(
            @PathVariable Long id,
            @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user,
            @Valid @RequestBody MeasurementRequest req) {
        if (user == null) return ResponseEntity.status(401).build();
        MeasurementResponse resp = measurementService.update(id, req, user.getId());
        if (resp == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resp);
    }

    /**
     * 删除当前用户的量体数据
     *
     * @param id   量体数据 ID
     * @param user 当前登录用户
     * @return 204（成功） / 401（未登录） / 404（不存在或越权）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @SessionAttribute(value = UserController.SESSION_USER_KEY, required = false) User user) {
        if (user == null) return ResponseEntity.status(401).build();
        boolean ok = measurementService.delete(id, user.getId());
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
