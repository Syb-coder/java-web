package com.example.java6.dto; // 定义 DTO 接口所在包，DTO 用于分层间的数据传输，避免直接暴露实体

import com.example.java6.model.RegulationCategory; // 导入法规分类枚举，约束政策法规的类别取值范围

import java.time.LocalDate; // 导入日期类型（不含时间），用于表示发布日期和生效日期

/**
 * 政策法规创建/更新请求
 * 用于后台管理员新增或编辑政策法规时提交的请求体
 * 服务端根据是否携带 ID 区分新增与更新操作
 *
 * @param title             法规标题
 * @param issuingAuthority  颁布机构
 * @param category          法规分类
 * @param publishDate       发布日期
 * @param effectiveDate     生效日期
 * @param content           法规正文
 * @param documentNumber    法规文号
 */
public record RegulationRequest( // Record 关键字声明不可变 DTO，自动生成构造器与访问方法
        String title, // 法规标题字段，前端列表展示的主标题
        String issuingAuthority, // 颁布机构字段，标注法规的发布主体（如"全国人大常委会"）
        RegulationCategory category, // 法规分类字段，枚举类型约束合法取值（如法律、行政法规、部门规章等）
        LocalDate publishDate, // 发布日期字段，法规正式公布的日期
        LocalDate effectiveDate, // 生效日期字段，法规开始施行的日期
        String content, // 法规正文字段，详情页展示的完整内容
        String documentNumber // 法规文号字段，法规的唯一编号（如"中华人民共和国主席令第X号"）
) {
}
