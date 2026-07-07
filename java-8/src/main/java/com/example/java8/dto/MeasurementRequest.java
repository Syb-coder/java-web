package com.example.java8.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 量体数据请求 DTO（新增/编辑共用）
 * <p>所有尺寸字段允许为空，由客户按需录入。</p>
 *
 * @param name                数据名称
 * @param height              身高（cm）
 * @param weight              体重（kg）
 * @param neckCircumference   颈围（cm）
 * @param shoulderWidth       肩宽（cm）
 * @param chestCircumference  胸围（cm）
 * @param waistCircumference  腰围（cm）
 * @param hipCircumference    臀围（cm）
 * @param clothesLength       衣长（cm）
 * @param sleeveLength        袖长（cm）
 * @param pantsLength         裤长（cm）
 * @param thighCircumference  大腿围（cm）
 * @param remark              备注
 */
public record MeasurementRequest(
        @NotBlank(message = "名称不能为空") String name,
        Double height,
        Double weight,
        Double neckCircumference,
        Double shoulderWidth,
        Double chestCircumference,
        Double waistCircumference,
        Double hipCircumference,
        Double clothesLength,
        Double sleeveLength,
        Double pantsLength,
        Double thighCircumference,
        String remark
) {
}
