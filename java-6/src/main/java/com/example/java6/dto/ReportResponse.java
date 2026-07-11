package com.example.java6.dto; // 定义 DTO 接口所在包，DTO 用于分层间的数据传输，避免直接暴露实体

import com.example.java6.model.Report; // 导入举报记录实体类，作为 from 工厂方法的入参类型
import com.example.java6.model.ReportStatus; // 导入举报处理状态枚举，作为响应字段的类型
import com.example.java6.model.ReportType; // 导入举报类型枚举，作为响应字段的类型

import java.time.LocalDateTime; // 导入日期时间类型，用于表示举报提交时间和处置时间

/**
 * 举报记录展示响应
 * 用于向前端或后台返回举报记录详情或列表项数据
 * 包含处置状态、处置备注、处置时间等全量信息
 *
 * @param id              主键 ID
 * @param reportType      举报类型
 * @param targetUrl       举报对象链接
 * @param description     举报描述
 * @param reporterName    举报人姓名
 * @param reporterContact 举报人联系方式
 * @param status          处理状态
 * @param handleNote      处置备注
 * @param createdAt       举报提交时间
 * @param updateTime      更新时间
 * @param handledAt       处置时间
 */
public record ReportResponse( // Record 关键字声明不可变 DTO，自动生成构造器与访问方法
        Long id, // 主键 ID，用于前端定位单条举报记录
        ReportType reportType, // 举报类型，枚举类型
        String targetUrl, // 举报对象链接，被举报的网址
        String description, // 举报描述，举报人填写的违规描述
        String reporterName, // 举报人姓名
        String reporterContact, // 举报人联系方式
        ReportStatus status, // 处理状态，枚举类型（如待处理、处理中、已处理）
        String handleNote, // 处置备注，管理员填写的处置说明
        LocalDateTime createdAt, // 举报提交时间，公众提交举报的时间戳
        LocalDateTime updateTime, // 更新时间，用于判断记录是否修改
        LocalDateTime handledAt // 处置时间，管理员处置举报的时间戳，未处置时为 null
) {

    /**
     * 从实体构造响应对象
     * 采用静态工厂方法封装实体到 DTO 的转换逻辑，集中管理映射规则
     * 隔离实体结构变化对 DTO 的影响
     *
     * @param r 举报记录实体
     * @return 响应对象
     */
    public static ReportResponse from(Report r) { // 静态工厂方法，从 Report 实体创建 ReportResponse
        return new ReportResponse( // 调用 Record 自动生成的全参构造器
                r.getId(), // 提取主键 ID
                r.getReportType(), // 提取举报类型
                r.getTargetUrl(), // 提取举报对象链接
                r.getDescription(), // 提取举报描述
                r.getReporterName(), // 提取举报人姓名
                r.getReporterContact(), // 提取举报人联系方式
                r.getStatus(), // 提取处理状态
                r.getHandleNote(), // 提取处置备注
                r.getCreatedAt(), // 提取举报提交时间
                r.getUpdateTime(), // 提取更新时间
                r.getHandledAt() // 提取处置时间
        );
    }
}
