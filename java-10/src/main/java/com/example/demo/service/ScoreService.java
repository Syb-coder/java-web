package com.example.demo.service;

import com.example.demo.entity.Score;
import com.example.demo.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 成绩业务逻辑层
 * <p>
 * 职责：封装成绩相关 CRUD 业务操作。
 * 为什么直接返回 Score 实体：成绩信息无敏感字段，无需 DTO 转换。
 * 为什么用 @Service：标记为 Spring Bean，由容器管理生命周期，便于事务控制和注入。
 * </p>
 */
@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;

    /**
     * 构造器注入依赖
     *
     * @param scoreRepository 成绩数据访问层
     */
    @Autowired
    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    /**
     * 查询所有成绩
     *
     * @return 成绩列表
     */
    public List<Score> findAll() {
        return scoreRepository.findAll();
    }

    /**
     * 创建成绩
     *
     * @param score 成绩信息
     * @return 创建后的成绩信息（含自增 ID）
     */
    public Score create(Score score) {
        return scoreRepository.save(score);
    }

    /**
     * 更新成绩信息
     * <p>
     * 仅更新非空字段，实现部分更新语义，避免误覆盖为空值。
     * </p>
     *
     * @param id 成绩 ID
     * @param score 更新数据
     * @return 更新后的成绩信息，成绩不存在时返回 null
     */
    public Score update(Long id, Score score) {
        Score existing = scoreRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (score.getCourseId() != null) {
            existing.setCourseId(score.getCourseId());
        }
        if (score.getStudentNo() != null) {
            existing.setStudentNo(score.getStudentNo());
        }
        if (score.getScore() != null) {
            existing.setScore(score.getScore());
        }
        return scoreRepository.save(existing);
    }

    /**
     * 删除成绩
     *
     * @param id 成绩 ID
     * @return 删除成功返回 true，成绩不存在返回 false
     */
    public boolean delete(Long id) {
        if (!scoreRepository.existsById(id)) {
            return false;
        }
        scoreRepository.deleteById(id);
        return true;
    }
}
