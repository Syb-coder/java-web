package com.example.java6.dto; // 定义 DTO 接口所在包，DTO 用于分层间的数据传输，避免直接暴露实体

import com.example.java6.model.ReportStatus; // 导入举报处理状态枚举，约束状态取值（如待处理、处理中、已处理）

/**
 * 举报处置请求（后台管理用）
 * 用于后台管理员对公众提交的举报记录进行处置时提交的请求体
 * 包括更新处理状态和填写处置备注
 *
 * @param status     处理状态
 * @param handleNote 处置备注
 */
public record ReportHandleRequest( // Record 关键字声明不可变 DTO，自动生成构造器与访问方法
        ReportStatus status, // 处理状态字段，枚举类型约束合法取值，表示处置后的最新状态
        String handleNote // 处置备注字段，管理员填写的处置说明（如"已转交相关部门"）
) {
}
