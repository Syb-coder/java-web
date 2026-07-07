package com.example.java5.model; // 声明包路径，归属 model（模型层）

/**
 * 经验要求枚举
 * <p>
 * 职责：定义岗位的经验要求档位，供实体字段 experience 使用，
 * 前端筛选和岗位卡片展示依赖此枚举的中文标签。
 * </p>
 */
public enum ExperienceLevel {
    NONE("经验不限"),       // 不限经验要求
    FRESH("应届生"),        // 校招应届生
    ONE_TO_THREE("1-3年"),  // 1-3 年经验
    THREE_TO_FIVE("3-5年"), // 3-5 年经验
    FIVE_TO_TEN("5-10年"),  // 5-10 年经验
    TEN_PLUS("10年以上");   // 10 年以上经验

    /** 中文标签，供前端直接展示 */
    private final String label;

    /**
     * 枚举构造函数
     *
     * @param label 中文标签
     */
    ExperienceLevel(String label) {
        this.label = label; // 赋值中文标签
    }

    /**
     * 获取中文标签
     *
     * @return 经验中文，如"3-5年"
     */
    public String getLabel() {
        return label; // 返回标签
    }
}
