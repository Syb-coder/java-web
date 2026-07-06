package com.example.java2.dto;

import com.example.java2.model.Picture;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图片响应 DTO
 */
public record PictureResponse(
        Long id,
        String externalId,
        String imageUrl,
        String title,
        String explanation,
        LocalDate pictureDate,
        Integer likes,
        Boolean favorited,
        LocalDateTime createdAt
) {
    public static PictureResponse from(Picture p) {
        return new PictureResponse(
                p.getId(),
                p.getExternalId(),
                p.getImageUrl(),
                p.getTitle(),
                p.getExplanation(),
                p.getPictureDate(),
                p.getLikes(),
                p.getFavorited(),
                p.getCreatedAt()
        );
    }
}
