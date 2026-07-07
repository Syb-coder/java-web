package com.example.java6.dto; // 定义 DTO 接口所在包，DTO 用于分层间的数据传输，避免直接暴露实体

import com.example.java6.model.NewsCategory; // 导入新闻分类枚举，约束新闻类别取值范围

import java.time.LocalDateTime; // 导入日期时间类型，用于表示新闻的发布时间

/**
 * 新闻资讯创建/更新请求
 * 用于后台管理员新增或编辑新闻时提交的请求体
 * 服务端根据是否携带 ID 区分新增与更新操作
 *
 * @param title       新闻标题
 * @param category    新闻分类
 * @param summary     新闻摘要
 * @param content     新闻正文
 * @param source      信息来源
 * @param publishTime 发布时间（为 null 时取当前时间）
 * @param top         是否置顶
 */
public record NewsRequest( // Record 关键字声明不可变 DTO，自动生成构造器与访问方法
        String title, // 新闻标题字段，前端列表展示的主标题
        NewsCategory category, // 新闻分类字段，枚举类型约束合法取值（如通知公告、行业动态等）
        String summary, // 新闻摘要字段，用于列表页简要展示
        String content, // 新闻正文字段，详情页展示的完整内容
        String source, // 信息来源字段，标注新闻来源（如"新华网"）
        LocalDateTime publishTime, // 发布时间字段，为 null 时由服务端填充当前时间
        Boolean top // 是否置顶字段，true 表示首页置顶展示，使用包装类型 Boolean 允许为 null
) {
}
