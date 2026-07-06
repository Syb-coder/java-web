package com.example.java3.dto;

import com.example.java3.model.DrawRecord;
import com.example.java3.model.FortuneLevel;

import java.time.LocalDateTime;

/**
 * 抽签历史记录 DTO
 */
public record DrawRecordResponse(
        Long id,
        Long fortuneId,
        Integer fortuneNumber,
        FortuneLevel level,
        String levelLabel,
        String question,
        LocalDateTime drawnAt
) {
    public static DrawRecordResponse from(DrawRecord r) {
        return new DrawRecordResponse(
                r.getId(),
                r.getFortuneId(),
                r.getFortuneNumber(),
                r.getLevel(),
                r.getLevel().getLabel(),
                r.getQuestion(),
                r.getDrawnAt()
        );
    }
}
