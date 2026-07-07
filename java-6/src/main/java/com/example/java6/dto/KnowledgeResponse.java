package com.example.java6.dto; // 定义 DTO 接口所在包，DTO 用于分层间的数据传输，避免直接暴露实体

import com.example.java6.model.KnowledgeArticle; // 导入知识文章实体类，作为 from 工厂方法的入参类型
import com.example.java6.model.KnowledgeCategory; // 导入知识分类枚举，作为响应字段的类型

import java.time.LocalDateTime; // 导入日期时间类型，用于表示创建时间

/**
 * 安全知识文章展示响应
 * 用于向前端返回知识文章详情或列表项数据
 * 包含阅读量、创建时间等由服务端维护的字段
 *
 * @param id        主键 ID
 * @param title     文章标题
 * @param category  文章分类
 * @param summary   文章摘要
 * @param content   文章正文
 * @param author    作者
 * @param viewCount 阅读量
 * @param createdAt 创建时间
 */
public record KnowledgeResponse( // Record 关键字声明不可变 DTO，自动生成构造器与访问方法
        Long id, // 主键 ID，用于前端定位单条文章详情
        String title, // 文章标题
        KnowledgeCategory category, // 文章分类，枚举类型
        String summary, // 文章摘要
        String content, // 文章正文
        String author, // 作者
        Integer viewCount, // 阅读量，服务端统计的浏览次数
        LocalDateTime createdAt // 创建时间，前端展示时间标签
) {

    /**
     * 从实体构造响应对象
     * 采用静态工厂方法封装实体到 DTO 的转换逻辑，集中管理映射规则
     * 隔离实体结构变化对 DTO 的影响
     *
     * @param k 知识文章实体
     * @return 响应对象
     */
    public static KnowledgeResponse from(KnowledgeArticle k) { // 静态工厂方法，从 KnowledgeArticle 实体创建 KnowledgeResponse
        return new KnowledgeResponse( // 调用 Record 自动生成的全参构造器
                k.getId(), // 提取主键 ID
                k.getTitle(), // 提取标题
                k.getCategory(), // 提取分类
                k.getSummary(), // 提取摘要
                k.getContent(), // 提取正文
                k.getAuthor(), // 提取作者
                k.getViewCount(), // 提取阅读量
                k.getCreatedAt() // 提取创建时间
        );
    }
}
