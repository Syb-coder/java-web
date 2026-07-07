package com.example.java5.model;

/**
 * 岗位数据来源
 */
public enum JobSource {
    LOCAL("本地种子"),
    V2EX("V2EX酷工作");

    private final String label;

    JobSource(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
