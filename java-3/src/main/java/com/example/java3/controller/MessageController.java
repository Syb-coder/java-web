// 声明当前类所在的包路径
package com.example.java3.controller;

// 导入消息请求 DTO
import com.example.java3.dto.MessageRequest;
// 导入消息响应 DTO
import com.example.java3.dto.MessageResponse;
// 导入学生用户实体
import com.example.java3.model.User;
// 导入消息服务
import com.example.java3.service.MessageService;
// 导入 HttpSession
import jakarta.servlet.http.HttpSession;
// 导入 @Valid
import jakarta.validation.Valid;
// 导入 HttpStatus
import org.springframework.http.HttpStatus;
// 导入 ResponseEntity
import org.springframework.http.ResponseEntity;
// 导入 Spring MVC 注解
import org.springframework.web.bind.annotation.*;

// 导入 List
import java.util.List;
// 导入 Map
import java.util.Map;

/**
 * 私信消息控制器
 */
// @RestController：REST 控制器，返回 JSON
@RestController
// @RequestMapping("/api/messages")：基础路径 /api/messages
@RequestMapping("/api/messages")
public class MessageController {

    // 注入的消息服务
    private final MessageService messageService;

    // 构造器注入消息服务
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 发送私信
     *
     * @param req     消息请求
     * @param session 会话
     * @return 消息响应
     */
    // @PostMapping：处理 POST /api/messages 请求
    @PostMapping
    public ResponseEntity<?> send(@Valid @RequestBody MessageRequest req, HttpSession session) {
        // 从会话获取当前学生
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            // 调用服务发送私信，发送者为当前用户
            return ResponseEntity.ok(messageService.send(u.getId(), req));
        } catch (IllegalArgumentException e) {
            // 接收者不存在等，返回 422
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 查询与某用户的对话
     *
     * @param otherId 对方用户 ID
     * @param session 会话
     * @return 消息列表
     */
    // @GetMapping("/conversation/{otherId}")：处理 GET /api/messages/conversation/{otherId} 请求
    @GetMapping("/conversation/{otherId}")
    public ResponseEntity<?> conversation(@PathVariable Long otherId, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 查询当前用户与对方的双向私信记录
        List<MessageResponse> list = messageService.conversation(u.getId(), otherId);
        // 返回 200 OK
        return ResponseEntity.ok(list);
    }

    /**
     * 查询最近会话列表
     *
     * @param session 会话
     * @return 会话列表
     */
    // @GetMapping("/recent")：处理 GET /api/messages/recent 请求
    @GetMapping("/recent")
    public ResponseEntity<?> recent(HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录，返回 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        // 返回 200 OK 和最近会话列表（按时间倒序）
        return ResponseEntity.ok(messageService.recentConversations(u.getId()));
    }

    /**
     * 查询未读消息数
     *
     * @param session 会话
     * @return 未读数
     */
    // @GetMapping("/unread/count")：处理 GET /api/messages/unread/count 请求
    @GetMapping("/unread/count")
    public ResponseEntity<?> unreadCount(HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            // 未登录返回 count=0（避免前端报错）
            return ResponseEntity.ok(Map.of("count", 0));
        }
        // 返回 200 OK 和未读消息数
        return ResponseEntity.ok(Map.of("count", messageService.unreadCount(u.getId())));
    }
}
