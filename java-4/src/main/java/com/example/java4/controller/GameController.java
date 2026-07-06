package com.example.java4.controller;

import com.example.java4.dto.GameRecordResponse;
import com.example.java4.dto.GameResultRequest;
import com.example.java4.model.Difficulty;
import com.example.java4.service.GameService;
import org.springframework.http.ResponseEntity;
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
 * 游戏成绩 REST 接口
 */
@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /** 提交一局成绩 */
    @PostMapping
    public GameRecordResponse submit(@RequestBody GameResultRequest request) {
        return gameService.submit(request);
    }

    /** 按难度查排行榜 */
    @GetMapping("/leaderboard/{difficulty}")
    public List<GameRecordResponse> leaderboard(@PathVariable Difficulty difficulty) {
        return gameService.leaderboard(difficulty);
    }

    /** 全局统计 */
    @GetMapping("/stats")
    public GameService.Stats stats() {
        return gameService.stats();
    }

    /** 难度配置（供前端获取牌面布局） */
    @GetMapping("/difficulties")
    public Map<String, Object> difficulties() {
        return Map.of(
                "EASY", Map.of("label", Difficulty.EASY.getLabel(), "cols", Difficulty.EASY.getCols(), "rows", Difficulty.EASY.getRows(), "pairs", Difficulty.EASY.getPairs()),
                "NORMAL", Map.of("label", Difficulty.NORMAL.getLabel(), "cols", Difficulty.NORMAL.getCols(), "rows", Difficulty.NORMAL.getRows(), "pairs", Difficulty.NORMAL.getPairs()),
                "HARD", Map.of("label", Difficulty.HARD.getLabel(), "cols", Difficulty.HARD.getCols(), "rows", Difficulty.HARD.getRows(), "pairs", Difficulty.HARD.getPairs())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handle(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
