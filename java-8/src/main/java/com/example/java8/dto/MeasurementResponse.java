package com.example.java8.dto;

import java.time.LocalDateTime;

/**
 * 量体数据响应 DTO
 */
public record MeasurementResponse(Long id, String name, Double height, Double weight,
                                  Double neckCircumference, Double shoulderWidth,
                                  Double chestCircumference, Double waistCircumference,
                                  Double hipCircumference, Double clothesLength,
                                  Double sleeveLength, Double pantsLength,
                                  Double thighCircumference, String remark,
                                  LocalDateTime createTime) {
}
