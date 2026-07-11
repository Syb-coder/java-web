package com.example.java6.dto; // 定义 DTO 接口所在包，DTO 用于分层间的数据传输，避免直接暴露实体

import com.example.java6.model.News; // 导入新闻实体类，作为 from 工厂方法的入参类型
import com.example.java6.model.NewsCategory; // 导入新闻分类枚举，作为响应字段的类型

import java.time.LocalDateTime; // 导入日期时间类型，用于表示发布时间和创建时间

/**
 * 新闻资讯展示响应
 * 用于向前端返回新闻详情或列表项数据
 * 包含阅读量、创建时间等由服务端维护的字段
 *
 * @param id          主键 ID
 * @param title       新闻标题
 * @param category    新闻分类
 * @param summary     新闻摘要
 * @param content     新闻正文
 * @param source      信息来源
 * @param publishTime 发布时间
 * @param viewCount   阅读量
 * @param top         是否置顶
 * @param createdAt   记录创建时间
 * @param updateTime  更新时间
 */
public record NewsResponse( // Record 关键字声明不可变 DTO，自动生成构造器与访问方法
        Long id, // 主键 ID，用于前端定位单条新闻详情
        String title, // 新闻标题
        NewsCategory category, // 新闻分类，枚举类型
        String summary, // 新闻摘要
        String content, // 新闻正文
        String source, // 信息来源
        LocalDateTime publishTime, // 发布时间，前端展示时间标签
        Integer viewCount, // 阅读量，服务端统计的浏览次数
        Boolean top, // 是否置顶，前端据此调整展示样式
        LocalDateTime createdAt, // 记录创建时间，用于后台审计排序
        LocalDateTime updateTime // 更新时间，用于判断内容是否修改
) {

    /**
     * 从实体构造响应对象
     * 采用静态工厂方法封装实体到 DTO 的转换逻辑，集中管理映射规则
     * 隔离实体结构变化对 DTO 的影响
     *
     * @param n 新闻实体
     * @return 响应对象
     */
    public static NewsResponse from(News n) { // 静态工厂方法，从 News 实体创建 NewsResponse
        return new NewsResponse( // 调用 Record 自动生成的全参构造器
                n.getId(), // 提取主键 ID
                n.getTitle(), // 提取标题
                n.getCategory(), // 提取分类
                n.getSummary(), // 提取摘要
                n.getContent(), // 提取正文
                n.getSource(), // 提取来源
                n.getPublishTime(), // 提取发布时间
                n.getViewCount(), // 提取阅读量
                n.getTop(), // 提取置顶标识
                n.getCreatedAt(), // 提取创建时间
                n.getUpdateTime() // 提取更新时间
        );
    }
}
