package com.example.java6.service; // 声明本类所在的包，位于 service 业务层

import com.example.java6.dto.ReportHandleRequest; // 导入举报处置请求 DTO，封装处置状态与处置备注
import com.example.java6.dto.ReportRequest; // 导入举报请求 DTO，封装用户提交举报时的字段
import com.example.java6.dto.ReportResponse; // 导入举报响应 DTO，用于向前端返回举报数据
import com.example.java6.model.Report; // 导入举报实体类，对应数据库 report 表
import com.example.java6.model.ReportStatus; // 导入举报状态枚举，如待处理、已处置、已忽略等
import com.example.java6.model.ReportType; // 导入举报类型枚举，如诈骗网站、钓鱼链接等
import com.example.java6.repository.ReportRepository; // 导入举报仓库接口，提供数据库访问能力
import org.springframework.stereotype.Service; // 导入 @Service 注解，标记为 Spring Service 组件

import java.time.LocalDateTime; // 导入时间类型，用于记录处置时间
import java.util.HashMap; // 导入 HashMap，用于构建统计结果
import java.util.List; // 导入 List 集合，用于返回列表数据
import java.util.Map; // 导入 Map 接口，用于返回键值对形式的统计结果
import java.util.stream.Collectors; // 导入 Stream 收集器，用于将 Stream 转为 List

/**
 * 举报记录业务服务
 *
 * <p>封装举报记录的提交、查询、处置以及统计分析等业务逻辑。</p>
 */
@Service // 标记为 Spring Service 组件，由容器管理生命周期，业务层的核心注解
public class ReportService {

    private final ReportRepository repository; // 注入举报仓库，用于数据库 CRUD 操作

    public ReportService(ReportRepository repository) { // 构造函数注入，Spring 自动注入 repository 依赖
        this.repository = repository; // 完成依赖赋值
    }

    /**
     * 查询举报列表（可按状态过滤）
     *
     * @param status 处理状态，为 null 时返回全部
     * @return 举报响应列表
     */
    public List<ReportResponse> list(ReportStatus status) { // 查询举报列表方法，支持按状态过滤
        List<Report> list = (status == null) // 根据状态参数是否为空选择不同查询方式
                ? repository.findAllByOrderByCreatedAtDesc() // 状态为空时查询全部，按创建时间倒序
                : repository.findByStatusOrderByCreatedAtDesc(status); // 状态非空时按状态过滤，按创建时间倒序
        return list.stream().map(ReportResponse::from).collect(Collectors.toList()); // 将实体列表通过 Stream 转换为响应 DTO 列表
    }

    /**
     * 获取举报详情
     *
     * @param id 举报 ID
     * @return 举报响应，不存在返回 null
     */
    public ReportResponse get(Long id) { // 获取举报详情方法
        return repository.findById(id).map(ReportResponse::from).orElse(null); // 根据 ID 查询举报并转换为响应对象，不存在返回 null
    }

    /**
     * 提交举报
     *
     * @param req 举报请求
     * @return 提交后的举报响应
     */
    public ReportResponse submit(ReportRequest req) { // 提交举报方法，由普通用户在前端发起
        Report r = new Report(); // 创建新的举报实体
        r.setReportType(req.reportType()); // 设置举报类型（如诈骗、钓鱼等）
        r.setTargetUrl(req.targetUrl()); // 设置举报目标 URL
        r.setDescription(req.description()); // 设置举报描述
        r.setReporterName(req.reporterName()); // 设置举报人姓名
        r.setReporterContact(req.reporterContact()); // 设置举报人联系方式
        return ReportResponse.from(repository.save(r)); // 持久化举报到数据库并返回响应对象，初始状态由实体默认值决定（通常为 PENDING）
    }

    /**
     * 处置举报（后台管理用）
     *
     * @param id  举报 ID
     * @param req 处置请求
     * @return 处置后的举报响应，不存在返回 null
     */
    public ReportResponse handle(Long id, ReportHandleRequest req) { // 处置举报方法，由后台管理员操作
        return repository.findById(id).map(r -> { // 根据 ID 查询举报，存在则执行 map 内逻辑
            r.setStatus(req.status()); // 更新举报处理状态（如 RESOLVED、IGNORED）
            r.setHandleNote(req.handleNote()); // 更新处置备注，记录管理员的处理说明
            // 状态流转到终态时记录处置时间
            if (req.status() == ReportStatus.RESOLVED || req.status() == ReportStatus.IGNORED) { // 若状态流转到终态（已处置或已忽略）
                r.setHandledAt(LocalDateTime.now()); // 记录处置时间为当前时刻
            }
            return ReportResponse.from(repository.save(r)); // 持久化更新后的举报并返回响应对象
        }).orElse(null); // 举报不存在时返回 null
    }

    /**
     * 删除举报记录
     *
     * @param id 举报 ID
     * @return 是否删除成功
     */
    public boolean delete(Long id) { // 删除举报记录方法
        if (repository.existsById(id)) { // 判断举报是否存在
            repository.deleteById(id); // 根据主键删除举报
            return true; // 返回 true 表示删除成功
        }
        return false; // 举报不存在，返回 false 表示删除失败
    }

    /**
     * 举报数据统计
     *
     * @return 统计结果（总数、按类型、按状态）
     */
    public Map<String, Object> stats() { // 举报数据统计方法，供后台仪表盘展示
        Map<String, Object> stats = new HashMap<>(); // 创建统计结果 Map，键为统计维度名，值为统计数值
        stats.put("total", repository.count()); // 统计举报总数并放入结果

        Map<String, Long> byType = new HashMap<>(); // 创建按类型统计的 Map
        for (ReportType type : ReportType.values()) { // 遍历所有举报类型枚举
            byType.put(type.name(), repository.countByReportType(type)); // 按类型统计数量并放入 Map
        }
        stats.put("byType", byType); // 将按类型统计结果放入总结果

        Map<String, Long> byStatus = new HashMap<>(); // 创建按状态统计的 Map
        for (ReportStatus status : ReportStatus.values()) { // 遍历所有举报状态枚举
            byStatus.put(status.name(), repository.countByStatus(status)); // 按状态统计数量并放入 Map
        }
        stats.put("byStatus", byStatus); // 将按状态统计结果放入总结果

        return stats; // 返回完整的统计结果
    }
}
