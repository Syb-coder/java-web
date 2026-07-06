package com.example.java3.controller;

import com.example.java3.dto.DrawRecordResponse;
import com.example.java3.dto.DrawRequest;
import com.example.java3.dto.FortuneResponse;
import com.example.java3.service.FortuneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 抽签 REST 接口
 */
@RestController
@RequestMapping("/api/fortunes")
public class FortuneController {

    private final FortuneService fortuneService;

    public FortuneController(FortuneService fortuneService) {
        this.fortuneService = fortuneService;
    }

    /** 摇签抽签（可附带所问之事） */
    @PostMapping("/draw")
    public FortuneResponse draw(@RequestBody(required = false) DrawRequest request) {
        return fortuneService.draw(request);
    }

    /** 查看指定签 */
    @GetMapping("/{id}")
    public FortuneResponse get(@PathVariable Long id) {
        return fortuneService.get(id);
    }

    /** 抽签历史 */
    @GetMapping("/history")
    public List<DrawRecordResponse> history() {
        return fortuneService.history();
    }

    /** 删除一条历史 */
    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        fortuneService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    /** 签文库统计 */
    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return Map.of("total", fortuneService.totalFortunes());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handle(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
