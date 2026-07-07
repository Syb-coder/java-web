package com.example.java6.service; // 声明本类所在的包，位于 service 业务层

import com.example.java6.dto.NewsRequest; // 导入新闻请求 DTO，封装新闻创建/更新的入参
import com.example.java6.dto.NewsResponse; // 导入新闻响应 DTO，用于向前端返回新闻数据
import com.example.java6.model.News; // 导入新闻实体类，对应数据库 news 表
import com.example.java6.model.NewsCategory; // 导入新闻分类枚举，如时政、行业等
import com.example.java6.repository.NewsRepository; // 导入新闻仓库接口，提供数据库访问能力
import org.springframework.stereotype.Service; // 导入 @Service 注解，标记为 Spring Service 组件

import java.time.LocalDateTime; // 导入时间类型，用于设置默认发布时间
import java.util.List; // 导入 List 集合，用于返回列表数据
import java.util.stream.Collectors; // 导入 Stream 收集器，用于将 Stream 转为 List

/**
 * 新闻资讯业务服务
 *
 * <p>封装新闻资讯的查询、创建、更新、删除以及阅读量统计等业务逻辑。</p>
 */
@Service // 标记为 Spring Service 组件，由容器管理生命周期，业务层的核心注解
public class NewsService {

    private final NewsRepository repository; // 注入新闻仓库，用于数据库 CRUD 操作

    public NewsService(NewsRepository repository) { // 构造函数注入，Spring 自动注入 repository 依赖
        this.repository = repository; // 完成依赖赋值
    }

    /**
     * 查询新闻列表（可按分类过滤）
     *
     * @param category 分类，为 null 时返回全部
     * @return 新闻响应列表
     */
    public List<NewsResponse> list(NewsCategory category) { // 查询新闻列表方法，支持按分类过滤
        List<News> list = (category == null) // 根据分类参数是否为空选择不同查询方式
                ? repository.findAllByOrderByPublishTimeDesc() // 分类为空时查询全部，按发布时间倒序
                : repository.findByCategoryOrderByPublishTimeDesc(category); // 分类非空时按分类过滤，按发布时间倒序
        return list.stream().map(NewsResponse::from).collect(Collectors.toList()); // 将实体列表通过 Stream 转换为响应 DTO 列表
    }

    /**
     * 查询首页最新新闻
     *
     * @return 最新 8 条新闻
     */
    public List<NewsResponse> latest() { // 查询首页最新新闻方法
        return repository.findTop8ByOrderByPublishTimeDesc().stream() // 查询最新 8 条新闻，按发布时间倒序，并转为 Stream
                .map(NewsResponse::from) // 将每个 News 实体映射为 NewsResponse 响应对象
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 查询置顶新闻
     *
     * @return 置顶新闻列表
     */
    public List<NewsResponse> topNews() { // 查询置顶新闻方法
        return repository.findByTopTrueOrderByPublishTimeDesc().stream() // 查询 top 字段为 true 的新闻，按发布时间倒序
                .map(NewsResponse::from) // 将每个 News 实体映射为 NewsResponse 响应对象
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 获取新闻详情（同时累加阅读量）
     *
     * @param id 新闻 ID
     * @return 新闻响应，不存在返回 null
     */
    public NewsResponse get(Long id) { // 获取新闻详情方法，同时累加阅读量
        return repository.findById(id).map(n -> { // 根据 ID 查询新闻，存在则执行 map 内逻辑
            n.setViewCount(n.getViewCount() + 1); // 阅读量累加 1，体现该新闻被访问一次
            repository.save(n); // 持久化更新后的阅读量到数据库
            return NewsResponse.from(n); // 转换为 NewsResponse 响应对象返回
        }).orElse(null); // 新闻不存在时返回 null
    }

    /**
     * 创建新闻
     *
     * @param req 创建请求
     * @return 创建后的新闻响应
     */
    public NewsResponse create(NewsRequest req) { // 创建新闻方法
        News n = new News(); // 创建新的新闻实体
        applyRequest(n, req); // 将请求字段应用到实体，复用私有方法避免重复赋值
        if (n.getPublishTime() == null) { // 若请求未指定发布时间
            n.setPublishTime(LocalDateTime.now()); // 默认设置为当前时间
        }
        return NewsResponse.from(repository.save(n)); // 持久化新闻到数据库并返回响应对象
    }

    /**
     * 更新新闻
     *
     * @param id  新闻 ID
     * @param req 更新请求
     * @return 更新后的新闻响应，不存在返回 null
     */
    public NewsResponse update(Long id, NewsRequest req) { // 更新新闻方法
        return repository.findById(id).map(n -> { // 根据 ID 查询新闻，存在则执行 map 内逻辑
            applyRequest(n, req); // 将请求字段应用到实体
            return NewsResponse.from(repository.save(n)); // 持久化更新后的新闻并返回响应对象
        }).orElse(null); // 新闻不存在时返回 null
    }

    /**
     * 删除新闻
     *
     * @param id 新闻 ID
     * @return 是否删除成功
     */
    public boolean delete(Long id) { // 删除新闻方法
        if (repository.existsById(id)) { // 判断新闻是否存在
            repository.deleteById(id); // 根据主键删除新闻
            return true; // 返回 true 表示删除成功
        }
        return false; // 新闻不存在，返回 false 表示删除失败
    }

    /**
     * 将请求字段应用到实体（避免重复赋值代码）
     *
     * @param n   新闻实体
     * @param req 请求对象
     */
    private void applyRequest(News n, NewsRequest req) { // 私有方法，将请求字段统一应用到实体，避免 create/update 重复赋值
        n.setTitle(req.title()); // 设置标题
        n.setCategory(req.category()); // 设置分类
        n.setSummary(req.summary()); // 设置摘要
        n.setContent(req.content()); // 设置正文内容
        n.setSource(req.source()); // 设置来源
        n.setPublishTime(req.publishTime()); // 设置发布时间
        n.setTop(Boolean.TRUE.equals(req.top())); // 设置是否置顶，使用 Boolean.TRUE.equals 避免 NPE，null 时为 false
    }
}
