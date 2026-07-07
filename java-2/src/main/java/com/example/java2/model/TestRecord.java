// 声明包路径
package com.example.java2.model;

// 导入 JPA 注解
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// 导入时间类型
import java.time.LocalDateTime;

/**
 * 答题记录实体
 * <p>
 * 记录用户每次自测的得分与答题细节。用户可在个人中心查看历史记录，复盘学习效果。
 * answersJson 字段以 JSON 字符串保存"题目ID:用户答案序号"映射，便于一次记录多题答案。
 * </p>
 * <p>
 * 实体关系：与 User 为多对一（一个用户多条答题记录）；与 Question 通过 answersJson 逻辑关联（非外键）；
 * 与 Category 为多对一（按分类组卷时记录分类来源，0 表示跨分类综合测试）。
 * </p>
 */
@Entity  // 标识为 JPA 实体
@Table(name = "test_record")
public class TestRecord {

    /** 主键 ID，自增 */
    // IDENTITY 策略：依赖数据库自增列，便于个人中心分页查询历史记录
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 答题用户 ID */
    // nullable=false：记录必须归属用户，否则无法在个人中心展示且数据失去业务意义
    @Column(nullable = false)
    private Long userId;

    /** 本次答题涉及分类 ID（0 表示跨分类综合测试） */
    // nullable=false：分类 ID 必须明确，0 作为哨兵值区分综合测试，避免 null 在查询与判分中的歧义
    @Column(nullable = false)
    private Long categoryId;

    /** 本次答题题目总数 */
    // nullable=false：总数必须有值，用于计算正确率（correctCount/totalCount），缺失会导致除零异常
    @Column(nullable = false)
    private int totalCount;

    /** 本次答对题数 */
    // nullable=false：答对数必须有值，用于得分计算与正确率展示；写入前需保证 ≤ totalCount
    @Column(nullable = false)
    private int correctCount;

    /** 本次得分（满分 = 总题数 × 题目分值，默认每题 10 分） */
    // nullable=false：得分必须有值，个人中心按得分排序展示历史；由调用方计算后写入
    @Column(nullable = false)
    private int score;

    /** 答题明细 JSON，格式如 {"1":0,"2":3,"3":1}，键为题目 ID，值为用户答案序号 */
    // length=2000：单次答题通常 10~50 题，每条约 10 字符，2000 字符留足余量；nullable 默认 true：兼容只记录得分不存明细的场景
    @Column(length = 2000)
    private String answersJson;

    /** 答题时间 */
    private LocalDateTime createTime;

    /** 无参构造方法：JPA 规范要求 */
    // JPA 通过反射调用无参构造实例化实体，缺失会导致查询结果映射失败
    public TestRecord() {
    }

    /**
     * 全参构造方法
     *
     * @param userId       用户 ID
     * @param categoryId   分类 ID
     * @param totalCount   总题数
     * @param correctCount 答对题数
     * @param score        得分
     * @param answersJson  答题明细 JSON
     */
    public TestRecord(Long userId, Long categoryId, int totalCount, int correctCount,
                      int score, String answersJson) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.totalCount = totalCount;
        this.correctCount = correctCount;
        this.score = score;
        this.answersJson = answersJson;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    /**
     * 获取答题明细 JSON。
     * 业务约束：返回值为 JSON 字符串，调用方需通过 JSON 库解析而非字符串拼接，
     * 避免格式错误导致回放失败；为 null 时表示仅记录得分未存明细。
     */
    public String getAnswersJson() { return answersJson; }

    /**
     * 设置答题明细 JSON。
     * 业务约束：调用方需保证 JSON 格式合法（{"题目ID":答案序号}）且答案序号在 0~3 范围内；
     * 题目总数应与 totalCount 一致，避免回放时数据不匹配。
     */
    public void setAnswersJson(String answersJson) { this.answersJson = answersJson; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
