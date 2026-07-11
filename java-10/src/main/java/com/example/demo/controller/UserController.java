package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器：提供用户 CRUD 接口
 * <p>
 * RESTful 设计：
 * - GET    /api/users      查询所有用户
 * - POST   /api/users      创建用户
 * - PUT    /api/users/{id} 更新用户
 * - DELETE /api/users/{id} 删除用户
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表（不含密码字段）
     */
    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    /**
     * 创建用户
     * <p>
     * 创建前校验密码非空和用户名唯一性：
     * - 密码为空返回 400（请求错误）
     * - 用户名重复返回 400（请求错误）
     * </p>
     *
     * @param dto 用户信息
     * @return 200 + 创建后的用户信息，或 400 + 错误信息
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserDTO dto) {
        // 手动校验密码：UserDTO 不加 @NotBlank 是因为更新时密码可选
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "密码不能为空"));
        }
        if (userService.existsByUsername(dto.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户名已存在"));
        }
        return ResponseEntity.ok(userService.create(dto));
    }

    /**
     * 更新用户信息
     * <p>
     * 支持部分更新：仅更新请求体中提供的字段。
     * 用户不存在时返回 400。
     * </p>
     *
     * @param id 用户 ID
     * @param dto 更新数据
     * @return 200 + 更新后的用户信息，或 400 + 错误信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        UserDTO updated = userService.update(id, dto);
        if (updated == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户不存在"));
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除用户
     *
     * @param id 用户 ID
     * @return 200 + { "success": true }，或 400 + 错误信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!userService.delete(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户不存在"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}
