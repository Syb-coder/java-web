package com.example.java5.model; // 声明包路径，归属 model（模型层）

/**
 * 岗位分类枚举
 * <p>
 * 职责：定义招聘岗位的分类维度，供实体字段 category 使用，
 * 前端筛选和首页统计也依赖此枚举的中文标签。
 * </p>
 * 设计要点：每个枚举值携带中文 label，避免前端维护映射表，
 * 由 @Enumerated(EnumType.STRING) 以字符串形式存入数据库。
 */
public enum JobCategory {
    DEVELOPMENT("研发类"),   // 后端/前端/移动端等开发岗
    PRODUCT("产品类"),       // 产品经理、产品运营
    DESIGN("设计类"),        // UI/UX/视觉设计
    OPERATIONS("运营类"),    // 用户运营、内容运营、活动运营
    QA("测试类"),            // 测试工程师、自动化测试
    DEVOPS("运维类"),        // DevOps/SRE/基础设施
    DATA("数据类"),          // 数据分析、数据仓库、ETL
    ALGORITHM("算法类");     // 机器学习、推荐系统、CV/NLP

    /** 中文标签，供前端直接展示，避免前端维护枚举映射表 */
    private final String label;

    /**
     * 枚举构造函数
     *
     * @param label 中文标签
     */
    JobCategory(String label) {
        this.label = label; // 赋值中文标签
    }

    /**
     * 获取中文标签
     *
     * @return 分类中文，如"研发类"
     */
    public String getLabel() {
        return label; // 返回标签
    }
}
