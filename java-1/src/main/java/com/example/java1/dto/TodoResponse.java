package com.example.java1.dto;

import com.example.java1.model.Priority;
import com.example.java1.model.Todo;

import java.time.LocalDateTime;

/**
 * 待办事项响应 DTO
 */
public record TodoResponse(
        Long id,
        String title,
        String description,
        Priority priority,
        String category,
        Boolean completed,
        LocalDateTime dueDate,
        LocalDateTime createdAt
) {
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getPriority(),
                todo.getCategory(),
                todo.getCompleted(),
                todo.getDueDate(),
                todo.getCreatedAt()
        );
    }
}
