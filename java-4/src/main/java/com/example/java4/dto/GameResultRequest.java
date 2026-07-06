package com.example.java4.dto;

import com.example.java4.model.Difficulty;

/**
 * 提交游戏成绩请求 DTO
 */
public record GameResultRequest(
        String playerName,
        Difficulty difficulty,
        Integer moves,
        Integer durationSeconds
) {
}
