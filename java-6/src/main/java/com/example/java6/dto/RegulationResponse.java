package com.example.java6.dto;

import com.example.java6.model.Regulation;
import com.example.java6.model.RegulationCategory;

import java.time.LocalDate;

/**
 * 政策法规展示响应
 *
 * @param id                主键 ID
 * @param title             法规标题
 * @param issuingAuthority  颁布机构
 * @param category          法规分类
 * @param publishDate       发布日期
 * @param effectiveDate     生效日期
 * @param content           法规正文
 * @param documentNumber    法规文号
 */
public record RegulationResponse(
        Long id,
        String title,
        String issuingAuthority,
        RegulationCategory category,
        LocalDate publishDate,
        LocalDate effectiveDate,
        String content,
        String documentNumber
) {

    /**
     * 从实体构造响应对象
     *
     * @param r 法规实体
     * @return 响应对象
     */
    public static RegulationResponse from(Regulation r) {
        return new RegulationResponse(
                r.getId(),
                r.getTitle(),
                r.getIssuingAuthority(),
                r.getCategory(),
                r.getPublishDate(),
                r.getEffectiveDate(),
                r.getContent(),
                r.getDocumentNumber()
        );
    }
}
