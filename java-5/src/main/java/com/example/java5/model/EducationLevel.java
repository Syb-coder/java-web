package com.example.java5.model; // 声明包路径，归属 model（模型层）

/**
 * 学历要求枚举
 * <p>
 * 职责：定义岗位的学历要求档位，供实体字段 education 使用，
 * 前端筛选和岗位卡片展示依赖此枚举的中文标签。
 * </p>
 */
public enum EducationLevel {
    NONE("学历不限"),      // 不限学历
    HIGH_SCHOOL("高中"),   // 高中及以上
    COLLEGE("大专"),       // 大专及以上
    BACHELOR("本科"),      // 本科及以上
    MASTER("硕士"),        // 硕士及以上
    PHD("博士");           // 博士

    /** 中文标签，供前端直接展示 */
    private final String label;

    /**
     * 枚举构造函数
     *
     * @param label 中文标签
     */
    EducationLevel(String label) {
        this.label = label; // 赋值中文标签
    }

    /**
     * 获取中文标签
     *
     * @return 学历中文，如"本科"
     */
    public String getLabel() {
        return label; // 返回标签
    }
}
