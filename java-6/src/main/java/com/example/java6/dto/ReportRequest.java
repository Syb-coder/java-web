package com.example.java6.dto; // 定义 DTO 接口所在包，DTO 用于分层间的数据传输，避免直接暴露实体

import com.example.java6.model.ReportType; // 导入举报类型枚举，约束举报类别的合法取值

/**
 * 举报提交请求
 * 用于公众用户在官网举报中心提交网络违法/不良信息举报
 * 该接口无需登录认证，允许匿名举报
 *
 * @param reportType      举报类型
 * @param targetUrl       举报对象链接
 * @param description     举报描述
 * @param reporterName    举报人姓名
 * @param reporterContact 举报人联系方式
 */
public record ReportRequest( // Record 关键字声明不可变 DTO，自动生成构造器与访问方法
        ReportType reportType, // 举报类型字段，枚举类型约束合法取值（如网络诈骗、信息泄露、恶意软件等）
        String targetUrl, // 举报对象链接字段，被举报的网址或资源 URL
        String description, // 举报描述字段，举报人对违规行为的详细描述
        String reporterName, // 举报人姓名字段，便于后续跟进核实
        String reporterContact // 举报人联系方式字段，便于反馈处理结果
) {
}
