package com.example.java4.dto;

import com.example.java4.model.GameRecord;

import java.time.LocalDateTime;

/**
 * 游戏记录响应 DTO
 */
public record GameRecordResponse(
        Long id,
        String playerName,
        String difficulty,
        String difficultyLabel,
        Integer moves,
        Integer durationSeconds,
        LocalDateTime completedAt
) {
    public static GameRecordResponse from(GameRecord r) {
        return new GameRecordResponse(
                r.getId(),
                r.getPlayerName(),
                r.getDifficulty().name(),
                r.getDifficulty().getLabel(),
                r.getMoves(),
                r.getDurationSeconds(),
                r.getCompletedAt()
        );
    }
}
