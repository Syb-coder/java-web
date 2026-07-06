package com.example.java3.dto;

import com.example.java3.model.Fortune;
import com.example.java3.model.FortuneLevel;

/**
 * 签文响应 DTO
 */
public record FortuneResponse(
        Long id,
        Integer number,
        String title,
        String poem,
        String interpretation,
        FortuneLevel level,
        String levelLabel
) {
    public static FortuneResponse from(Fortune f) {
        return new FortuneResponse(
                f.getId(),
                f.getNumber(),
                f.getTitle(),
                f.getPoem(),
                f.getInterpretation(),
                f.getLevel(),
                f.getLevel().getLabel()
        );
    }
}
