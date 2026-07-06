package com.example.java5.model;

/**
 * 经验要求
 */
public enum ExperienceLevel {
    NONE("经验不限"),
    FRESH("应届生"),
    ONE_TO_THREE("1-3年"),
    THREE_TO_FIVE("3-5年"),
    FIVE_TO_TEN("5-10年"),
    TEN_PLUS("10年以上");

    private final String label;

    ExperienceLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
