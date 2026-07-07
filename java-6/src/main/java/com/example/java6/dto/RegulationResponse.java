package com.example.java6.dto; // 定义 DTO 接口所在包，DTO 用于分层间的数据传输，避免直接暴露实体

import com.example.java6.model.Regulation; // 导入法规实体类，作为 from 工厂方法的入参类型
import com.example.java6.model.RegulationCategory; // 导入法规分类枚举，作为响应字段的类型

import java.time.LocalDate; // 导入日期类型（不含时间），用于表示发布日期和生效日期

/**
 * 政策法规展示响应
 * 用于向前端返回法规详情或列表项数据
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
public record RegulationResponse( // Record 关键字声明不可变 DTO，自动生成构造器与访问方法
        Long id, // 主键 ID，用于前端定位单条法规详情
        String title, // 法规标题
        String issuingAuthority, // 颁布机构
        RegulationCategory category, // 法规分类，枚举类型
        LocalDate publishDate, // 发布日期
        LocalDate effectiveDate, // 生效日期
        String content, // 法规正文
        String documentNumber // 法规文号
) {

    /**
     * 从实体构造响应对象
     * 采用静态工厂方法封装实体到 DTO 的转换逻辑，集中管理映射规则
     * 隔离实体结构变化对 DTO 的影响
     *
     * @param r 法规实体
     * @return 响应对象
     */
    public static RegulationResponse from(Regulation r) { // 静态工厂方法，从 Regulation 实体创建 RegulationResponse
        return new RegulationResponse( // 调用 Record 自动生成的全参构造器
                r.getId(), // 提取主键 ID
                r.getTitle(), // 提取标题
                r.getIssuingAuthority(), // 提取颁布机构
                r.getCategory(), // 提取分类
                r.getPublishDate(), // 提取发布日期
                r.getEffectiveDate(), // 提取生效日期
                r.getContent(), // 提取正文
                r.getDocumentNumber() // 提取法规文号
        );
    }
}
