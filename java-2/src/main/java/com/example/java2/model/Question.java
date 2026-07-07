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
 * 测试题目实体
 * <p>
 * 平台配套自测题库。每道题包含题干、4 个选项、正确答案序号（0~3）、所属分类、分值。
 * 用于前台在线自测与学习效果检验。
 * </p>
 * <p>
 * 实体关系：与 Category 为多对一（与 Article 共用分类，便于按主题组卷）；
 * 题目本身不与用户直接关联，用户答题行为通过 TestRecord 承载。
 * </p>
 */
@Entity  // 标识为 JPA 实体
@Table(name = "question")
public class Question {

    /** 主键 ID，自增 */
    // IDENTITY 策略：依赖数据库自增列，便于 TestRecord.answersJson 以题目 ID 为 key 引用
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属分类 ID（与文章分类共用，便于按主题组卷） */
    // nullable=false：题目必须归属分类，否则无法按主题抽题；与 Article.categoryId 共用同一分类表
    @Column(nullable = false)
    private Long categoryId;

    /** 题干 */
    // length=1000：题干需容纳场景描述与代码片段，比选项更长；不用 TEXT 是为保留长度约束防滥用
    @Column(nullable = false, length = 1000)
    private String stem;

    /** 选项 A */
    // length=500：选项为短文本，500 字符覆盖含代码片段的选项；nullable=false：四选项均必填，缺项会导致题目无法作答
    @Column(nullable = false, length = 500)
    private String optionA;

    /** 选项 B */
    // 同 optionA：四选项长度一致，便于前端统一渲染
    @Column(nullable = false, length = 500)
    private String optionB;

    /** 选项 C */
    // 同 optionA
    @Column(nullable = false, length = 500)
    private String optionC;

    /** 选项 D */
    // 同 optionA
    @Column(nullable = false, length = 500)
    private String optionD;

    /** 正确答案序号：0=A, 1=B, 2=C, 3=D */
    // nullable=false：答案序号必须明确，否则判分逻辑无法执行；用 int 而非 String 节省存储且便于比较
    @Column(nullable = false)
    private int answerIndex;

    /** 题目分值（默认 10 分） */
    // nullable=false+默认 10：分值必须有值，默认 10 便于统一计分；可按题目难度调整
    @Column(nullable = false)
    private int score = 10;

    /** 题目解析（可选），答题结束后展示 */
    // length=1000：解析可包含知识点说明，与 stem 同长度；nullable 默认 true：解析为可选，新建题目可暂不填写
    @Column(length = 1000)
    private String analysis;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 无参构造方法：JPA 规范要求 */
    // JPA 通过反射调用无参构造实例化实体，缺失会导致 LazyInitializationException 等运行时异常
    public Question() {
    }

    /**
     * 全参构造方法
     *
     * @param categoryId  所属分类 ID
     * @param stem        题干
     * @param optionA     选项 A
     * @param optionB     选项 B
     * @param optionC     选项 C
     * @param optionD     选项 D
     * @param answerIndex 正确答案序号
     */
    public Question(Long categoryId, String stem, String optionA, String optionB,
                    String optionC, String optionD, int answerIndex) {
        this.categoryId = categoryId;
        this.stem = stem;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.answerIndex = answerIndex;
        this.createTime = LocalDateTime.now();
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getStem() { return stem; }
    public void setStem(String stem) { this.stem = stem; }
    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    /**
     * 获取正确答案序号。
     * 业务约束：返回值范围必须为 0~3，分别对应 A/B/C/D；
     * 调用方判分时应做边界校验，防止脏数据导致数组越界。
     */
    public int getAnswerIndex() { return answerIndex; }

    /**
     * 设置正确答案序号。
     * 业务约束：调用方需保证入参在 0~3 范围内，超出范围将导致判分逻辑异常。
     */
    public void setAnswerIndex(int answerIndex) { this.answerIndex = answerIndex; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
