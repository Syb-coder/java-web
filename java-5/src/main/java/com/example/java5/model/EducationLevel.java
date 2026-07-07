package com.example.java5.model;

/**
 * 学历要求
 */
public enum EducationLevel {
    NONE("学历不限"),
    HIGH_SCHOOL("高中"),
    COLLEGE("大专"),
    BACHELOR("本科"),
    MASTER("硕士"),
    PHD("博士");

    private final String label;

    EducationLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
