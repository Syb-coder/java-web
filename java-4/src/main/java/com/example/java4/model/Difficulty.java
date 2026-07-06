package com.example.java4.model;

/**
 * 游戏难度枚举
 */
public enum Difficulty {
    EASY("简单", 4, 4),    // 4×4 = 16 张牌 = 8 对
    NORMAL("普通", 6, 4),  // 6×4 = 24 张牌 = 12 对
    HARD("困难", 6, 6);    // 6×6 = 36 张牌 = 18 对

    private final String label;
    private final int cols;
    private final int rows;

    Difficulty(String label, int cols, int rows) {
        this.label = label;
        this.cols = cols;
        this.rows = rows;
    }

    public String getLabel() {
        return label;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    /** 牌对数 */
    public int getPairs() {
        return (cols * rows) / 2;
    }
}
