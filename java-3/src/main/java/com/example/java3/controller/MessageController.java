package com.example.java3.controller;

import com.example.java3.dto.MessageRequest;
import com.example.java3.dto.MessageResponse;
import com.example.java3.model.User;
import com.example.java3.service.MessageService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 私信消息控制器
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

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
    @PostMapping
    public ResponseEntity<?> send(@Valid @RequestBody MessageRequest req, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        try {
            return ResponseEntity.ok(messageService.send(u.getId(), req));
        } catch (IllegalArgumentException e) {
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
    @GetMapping("/conversation/{otherId}")
    public ResponseEntity<?> conversation(@PathVariable Long otherId, HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        List<MessageResponse> list = messageService.conversation(u.getId(), otherId);
        return ResponseEntity.ok(list);
    }

    /**
     * 查询最近会话列表
     *
     * @param session 会话
     * @return 会话列表
     */
    @GetMapping("/recent")
    public ResponseEntity<?> recent(HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登录"));
        }
        return ResponseEntity.ok(messageService.recentConversations(u.getId()));
    }

    /**
     * 查询未读消息数
     *
     * @param session 会话
     * @return 未读数
     */
    @GetMapping("/unread/count")
    public ResponseEntity<?> unreadCount(HttpSession session) {
        User u = UserController.currentUser(session);
        if (u == null) {
            return ResponseEntity.ok(Map.of("count", 0));
        }
        return ResponseEntity.ok(Map.of("count", messageService.unreadCount(u.getId())));
    }
}
