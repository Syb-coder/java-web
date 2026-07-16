package com.example.demo.controller;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证控制器：处理登录请求
 * <p>
 * 为什么用 /api/auth 前缀：与用户管理 API (/api/users) 分离，职责清晰。
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录
     * <p>
     * 为什么登录失败返回 401 而非 400：401 Unauthorized 语义更准确，
     * 表示"身份验证失败"，前端可根据状态码区分处理。
     * 不区分"用户名不存在"和"密码错误"，防止用户枚举攻击。
     * </p>
     *
     * @param request 登录请求（含用户名和密码，经 @Valid 校验非空）
     * @return 200 + LoginResponse 成功，401 + 错误信息失败
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request.getUsername(), request.getPassword());
        if (response == null) {
            return ResponseEntity.status(401).body(Map.of("error", "用户名或密码错误"));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 修改密码
     * <p>
     * 验证原密码后更新为新密码。
     * 为什么原密码错误返回 400 而非 401：此处用户已通过身份认证，
     * 错误原因为"输入的原密码不正确"，属于请求参数错误，400 语义更准确。
     * </p>
     *
     * @param request 修改密码请求（含用户名、原密码、新密码，经 @Valid 校验非空）
     * @return 200 + { "success": true } 成功，400 + 错误信息 原密码错误
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        boolean success = userService.changePassword(
                request.getUsername(),
                request.getOldPassword(),
                request.getNewPassword()
        );
        if (!success) {
            return ResponseEntity.badRequest().body(Map.of("error", "原密码错误"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}
