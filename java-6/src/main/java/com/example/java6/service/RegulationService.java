package com.example.java6.service; // 声明本类所在的包，位于 service 业务层

import com.example.java6.dto.RegulationRequest; // 导入法规请求 DTO，封装创建/更新的入参
import com.example.java6.dto.RegulationResponse; // 导入法规响应 DTO，用于向前端返回法规数据
import com.example.java6.model.Regulation; // 导入法规实体类，对应数据库 regulation 表
import com.example.java6.model.RegulationCategory; // 导入法规分类枚举，如法律、行政法规等
import com.example.java6.repository.RegulationRepository; // 导入法规仓库接口，提供数据库访问能力
import org.springframework.stereotype.Service; // 导入 @Service 注解，标记为 Spring Service 组件

import java.util.List; // 导入 List 集合，用于返回列表数据
import java.util.stream.Collectors; // 导入 Stream 收集器，用于将 Stream 转为 List

/**
 * 政策法规业务服务
 *
 * <p>封装政策法规的查询、创建、更新、删除等业务逻辑。</p>
 */
@Service // 标记为 Spring Service 组件，由容器管理生命周期，业务层的核心注解
public class RegulationService {

    private final RegulationRepository repository; // 注入法规仓库，用于数据库 CRUD 操作

    public RegulationService(RegulationRepository repository) { // 构造函数注入，Spring 自动注入 repository 依赖
        this.repository = repository; // 完成依赖赋值
    }

    /**
     * 查询法规列表（可按分类过滤）
     *
     * @param category 分类，为 null 时返回全部
     * @return 法规响应列表
     */
    public List<RegulationResponse> list(RegulationCategory category) { // 查询法规列表方法，支持按分类过滤
        List<Regulation> list = (category == null) // 根据分类参数是否为空选择不同查询方式
                ? repository.findAllByOrderByPublishDateDesc() // 分类为空时查询全部，按发布日期倒序
                : repository.findByCategoryOrderByPublishDateDesc(category); // 分类非空时按分类过滤，按发布日期倒序
        return list.stream().map(RegulationResponse::from).collect(Collectors.toList()); // 将实体列表通过 Stream 转换为响应 DTO 列表
    }

    /**
     * 查询首页最新法规
     *
     * @return 最新 6 条法规
     */
    public List<RegulationResponse> latest() { // 查询首页最新法规方法
        return repository.findTop6ByOrderByPublishDateDesc().stream() // 查询最新 6 条法规，按发布日期倒序，并转为 Stream
                .map(RegulationResponse::from) // 将每个 Regulation 实体映射为 RegulationResponse 响应对象
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 获取法规详情
     *
     * @param id 法规 ID
     * @return 法规响应，不存在返回 null
     */
    public RegulationResponse get(Long id) { // 获取法规详情方法
        return repository.findById(id).map(RegulationResponse::from).orElse(null); // 根据 ID 查询法规并转换为响应对象，不存在返回 null
    }

    /**
     * 创建法规
     *
     * @param req 创建请求
     * @return 创建后的法规响应
     */
    public RegulationResponse create(RegulationRequest req) { // 创建法规方法
        Regulation r = new Regulation(); // 创建新的法规实体
        applyRequest(r, req); // 将请求字段应用到实体，复用私有方法避免重复赋值
        return RegulationResponse.from(repository.save(r)); // 持久化法规到数据库并返回响应对象
    }

    /**
     * 更新法规
     *
     * @param id  法规 ID
     * @param req 更新请求
     * @return 更新后的法规响应，不存在返回 null
     */
    public RegulationResponse update(Long id, RegulationRequest req) { // 更新法规方法
        return repository.findById(id).map(r -> { // 根据 ID 查询法规，存在则执行 map 内逻辑
            applyRequest(r, req); // 将请求字段应用到实体
            return RegulationResponse.from(repository.save(r)); // 持久化更新后的法规并返回响应对象
        }).orElse(null); // 法规不存在时返回 null
    }

    /**
     * 删除法规
     *
     * @param id 法规 ID
     * @return 是否删除成功
     */
    public boolean delete(Long id) { // 删除法规方法
        if (repository.existsById(id)) { // 判断法规是否存在
            repository.deleteById(id); // 根据主键删除法规
            return true; // 返回 true 表示删除成功
        }
        return false; // 法规不存在，返回 false 表示删除失败
    }

    /**
     * 将请求字段应用到实体
     *
     * @param r   法规实体
     * @param req 请求对象
     */
    private void applyRequest(Regulation r, RegulationRequest req) { // 私有方法，将请求字段统一应用到实体，避免 create/update 重复赋值
        r.setTitle(req.title()); // 设置标题
        r.setIssuingAuthority(req.issuingAuthority()); // 设置发布机构
        r.setCategory(req.category()); // 设置分类
        r.setPublishDate(req.publishDate()); // 设置发布日期
        r.setEffectiveDate(req.effectiveDate()); // 设置生效日期
        r.setContent(req.content()); // 设置正文内容
        r.setDocumentNumber(req.documentNumber()); // 设置文号
    }
}
