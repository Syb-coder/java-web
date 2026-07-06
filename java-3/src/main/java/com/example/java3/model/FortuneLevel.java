package com.example.java3.model;

/**
 * 签文等级
 */
public enum FortuneLevel {
    SUPREME("上上签"),
    GOOD("上签"),
    NEUTRAL("中签"),
    BAD("下签"),
    WORST("下下签");

    private final String label;

    FortuneLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
