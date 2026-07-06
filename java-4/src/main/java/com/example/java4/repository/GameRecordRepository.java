package com.example.java4.repository;

import com.example.java4.model.Difficulty;
import com.example.java4.model.GameRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRecordRepository extends JpaRepository<GameRecord, Long> {

    /** 按难度查询排行榜（步数升序，步数相同则用时升序），取前 N */
    List<GameRecord> findByDifficultyOrderByMovesAscDurationSecondsAsc(Difficulty difficulty, Pageable pageable);

    long countByDifficulty(Difficulty difficulty);
}
