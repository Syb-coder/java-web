package com.example.java11.repository;

import com.example.java11.model.SensitiveWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 敏感词数据访问层
 * <p>
 * 提供对 sensitive_words 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface SensitiveWordRepository extends JpaRepository<SensitiveWord, Long> {

    /**
     * 获取所有敏感词，按创建时间倒序
     *
     * @return 敏感词列表
     */
    List<SensitiveWord> findAllByOrderByCreatedAtDesc();

    /**
     * 按敏感词内容查找
     *
     * @param word 敏感词
     * @return 敏感词对象，不存在时返回 empty
     */
    Optional<SensitiveWord> findByWord(String word);
}
