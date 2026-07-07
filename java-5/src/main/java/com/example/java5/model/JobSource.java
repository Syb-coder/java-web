package com.example.java5.model; // 声明包路径，归属 model（模型层）

/**
 * 岗位数据来源枚举
 * <p>
 * 职责：区分岗位数据来源，便于运营筛选和首页统计。
 * </p>
 * 为什么需要来源标识：
 * - LOCAL 数据可编辑删除，V2EX 数据由同步生成
 * - 去重依赖 source + externalId 组合判断
 */
public enum JobSource {
    LOCAL("本地种子"),   // 本地录入或种子数据，externalId 为 null
    V2EX("V2EX酷工作"); // V2EX 酷工作节点同步，externalId 为 V2EX 主题 ID

    /** 中文标签，供前端展示来源标签 */
    private final String label;

    /**
     * 枚举构造函数
     *
     * @param label 中文标签
     */
    JobSource(String label) {
        this.label = label; // 赋值中文标签
    }

    /**
     * 获取中文标签
     *
     * @return 来源中文，如"本地种子"
     */
    public String getLabel() {
        return label; // 返回标签
    }
}
