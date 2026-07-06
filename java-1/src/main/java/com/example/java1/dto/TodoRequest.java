package com.example.java1.dto;

import com.example.java1.model.Priority;

import java.time.LocalDateTime;

/**
 * 待办事项创建/更新请求 DTO
 */
public record TodoRequest(
        String title,
        String description,
        Priority priority,
        String category,
        Boolean completed,
        LocalDateTime dueDate
) {
}
