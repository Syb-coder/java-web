package com.example.java6.service; // 声明本类所在的包，位于 service 业务层

import com.example.java6.dto.KnowledgeRequest; // 导入知识文章请求 DTO，封装创建/更新的入参
import com.example.java6.dto.KnowledgeResponse; // 导入知识文章响应 DTO，用于向前端返回文章数据
import com.example.java6.model.KnowledgeArticle; // 导入知识文章实体类，对应数据库 knowledge_article 表
import com.example.java6.model.KnowledgeCategory; // 导入知识分类枚举，如防诈骗、个人信息保护等
import com.example.java6.repository.KnowledgeArticleRepository; // 导入知识文章仓库接口，提供数据库访问能力
import org.springframework.stereotype.Service; // 导入 @Service 注解，标记为 Spring Service 组件

import java.util.List; // 导入 List 集合，用于返回列表数据
import java.util.stream.Collectors; // 导入 Stream 收集器，用于将 Stream 转为 List

/**
 * 安全知识文章业务服务
 *
 * <p>封装安全知识文章的查询、创建、更新、删除以及阅读量统计等业务逻辑。</p>
 */
@Service // 标记为 Spring Service 组件，由容器管理生命周期，业务层的核心注解
public class KnowledgeService {

    private final KnowledgeArticleRepository repository; // 注入知识文章仓库，用于数据库 CRUD 操作

    public KnowledgeService(KnowledgeArticleRepository repository) { // 构造函数注入，Spring 自动注入 repository 依赖
        this.repository = repository; // 完成依赖赋值
    }

    /**
     * 查询知识文章列表（可按分类过滤）
     *
     * @param category 分类，为 null 时返回全部
     * @return 文章响应列表
     */
    public List<KnowledgeResponse> list(KnowledgeCategory category) { // 查询知识文章列表方法，支持按分类过滤
        List<KnowledgeArticle> list = (category == null) // 根据分类参数是否为空选择不同查询方式
                ? repository.findAllByOrderByCreatedAtDesc() // 分类为空时查询全部，按创建时间倒序
                : repository.findByCategoryOrderByCreatedAtDesc(category); // 分类非空时按分类过滤，按创建时间倒序
        return list.stream().map(KnowledgeResponse::from).collect(Collectors.toList()); // 将实体列表通过 Stream 转换为响应 DTO 列表
    }

    /**
     * 查询首页推荐文章
     *
     * @return 最新 6 篇文章
     */
    public List<KnowledgeResponse> latest() { // 查询首页推荐文章方法
        return repository.findTop6ByOrderByCreatedAtDesc().stream() // 查询最新 6 篇文章，按创建时间倒序，并转为 Stream
                .map(KnowledgeResponse::from) // 将每个 KnowledgeArticle 实体映射为 KnowledgeResponse 响应对象
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 获取文章详情（同时累加阅读量）
     *
     * @param id 文章 ID
     * @return 文章响应，不存在返回 null
     */
    public KnowledgeResponse get(Long id) { // 获取文章详情方法，同时累加阅读量
        return repository.findById(id).map(k -> { // 根据 ID 查询文章，存在则执行 map 内逻辑
            k.setViewCount(k.getViewCount() + 1); // 阅读量累加 1，体现该文章被访问一次
            repository.save(k); // 持久化更新后的阅读量到数据库
            return KnowledgeResponse.from(k); // 转换为 KnowledgeResponse 响应对象返回
        }).orElse(null); // 文章不存在时返回 null
    }

    /**
     * 创建知识文章
     *
     * @param req 创建请求
     * @return 创建后的文章响应
     */
    public KnowledgeResponse create(KnowledgeRequest req) { // 创建知识文章方法
        KnowledgeArticle k = new KnowledgeArticle(); // 创建新的知识文章实体
        applyRequest(k, req); // 将请求字段应用到实体，复用私有方法避免重复赋值
        return KnowledgeResponse.from(repository.save(k)); // 持久化文章到数据库并返回响应对象
    }

    /**
     * 更新知识文章
     *
     * @param id  文章 ID
     * @param req 更新请求
     * @return 更新后的文章响应，不存在返回 null
     */
    public KnowledgeResponse update(Long id, KnowledgeRequest req) { // 更新知识文章方法
        return repository.findById(id).map(k -> { // 根据 ID 查询文章，存在则执行 map 内逻辑
            applyRequest(k, req); // 将请求字段应用到实体
            return KnowledgeResponse.from(repository.save(k)); // 持久化更新后的文章并返回响应对象
        }).orElse(null); // 文章不存在时返回 null
    }

    /**
     * 删除知识文章
     *
     * @param id 文章 ID
     * @return 是否删除成功
     */
    public boolean delete(Long id) { // 删除知识文章方法
        if (repository.existsById(id)) { // 判断文章是否存在
            repository.deleteById(id); // 根据主键删除文章
            return true; // 返回 true 表示删除成功
        }
        return false; // 文章不存在，返回 false 表示删除失败
    }

    /**
     * 将请求字段应用到实体
     *
     * @param k   文章实体
     * @param req 请求对象
     */
    private void applyRequest(KnowledgeArticle k, KnowledgeRequest req) { // 私有方法，将请求字段统一应用到实体，避免 create/update 重复赋值
        k.setTitle(req.title()); // 设置标题
        k.setCategory(req.category()); // 设置分类
        k.setSummary(req.summary()); // 设置摘要
        k.setContent(req.content()); // 设置正文内容
        k.setAuthor(req.author()); // 设置作者
    }
}
