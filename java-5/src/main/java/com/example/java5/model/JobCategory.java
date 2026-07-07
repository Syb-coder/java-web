package com.example.java5.model;

/**
 * 岗位类型
 */
public enum JobCategory {
    DEVELOPMENT("研发类"),
    PRODUCT("产品类"),
    DESIGN("设计类"),
    OPERATIONS("运营类"),
    QA("测试类"),
    DEVOPS("运维类"),
    DATA("数据类"),
    ALGORITHM("算法类");

    private final String label;

    JobCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
