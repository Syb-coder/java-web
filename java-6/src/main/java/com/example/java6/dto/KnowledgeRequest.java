package com.example.java6.dto; // 定义 DTO 接口所在包，DTO 用于分层间的数据传输，避免直接暴露实体

import com.example.java6.model.KnowledgeCategory; // 导入知识分类枚举，约束知识文章的类别取值范围

/**
 * 安全知识文章创建/更新请求
 * 用于后台管理员新增或编辑安全知识文章时提交的请求体
 * 服务端根据是否携带 ID 区分新增与更新操作
 *
 * @param title    文章标题
 * @param category 文章分类
 * @param summary  文章摘要
 * @param content  文章正文
 * @param author   作者
 */
public record KnowledgeRequest( // Record 关键字声明不可变 DTO，自动生成构造器与访问方法
        String title, // 文章标题字段，前端列表展示的主标题
        KnowledgeCategory category, // 文章分类字段，枚举类型约束合法取值（如防骗指南、密码安全等）
        String summary, // 文章摘要字段，用于列表页简要展示
        String content, // 文章正文字段，详情页展示的完整内容
        String author // 作者字段，标注文章来源作者
) {
}
