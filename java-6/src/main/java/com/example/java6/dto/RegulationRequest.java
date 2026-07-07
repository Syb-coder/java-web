package com.example.java6.dto;

import com.example.java6.model.RegulationCategory;

import java.time.LocalDate;

/**
 * 政策法规创建/更新请求
 *
 * @param title             法规标题
 * @param issuingAuthority  颁布机构
 * @param category          法规分类
 * @param publishDate       发布日期
 * @param effectiveDate     生效日期
 * @param content           法规正文
 * @param documentNumber    法规文号
 */
public record RegulationRequest(
        String title,
        String issuingAuthority,
        RegulationCategory category,
        LocalDate publishDate,
        LocalDate effectiveDate,
        String content,
        String documentNumber
) {
}
