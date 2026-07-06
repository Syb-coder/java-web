package com.example.java4.service;

import com.example.java4.dto.GameRecordResponse;
import com.example.java4.dto.GameResultRequest;
import com.example.java4.model.Difficulty;
import com.example.java4.model.GameRecord;
import com.example.java4.repository.GameRecordRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 游戏成绩业务层
 */
@Service
@Transactional
public class GameService {

    private static final int LEADERBOARD_SIZE = 10;
    private final GameRecordRepository recordRepository;

    public GameService(GameRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    /**
     * 提交一局成绩
     */
    public GameRecordResponse submit(GameResultRequest request) {
        if (request.playerName() == null || request.playerName().isBlank()) {
            throw new IllegalArgumentException("玩家名不能为空");
        }
        if (request.moves() == null || request.moves() <= 0) {
            throw new IllegalArgumentException("步数必须大于 0");
        }
        if (request.durationSeconds() == null || request.durationSeconds() <= 0) {
            throw new IllegalArgumentException("用时必须大于 0");
        }
        GameRecord record = new GameRecord();
        record.setPlayerName(request.playerName().trim());
        record.setDifficulty(request.difficulty());
        record.setMoves(request.moves());
        record.setDurationSeconds(request.durationSeconds());
        return GameRecordResponse.from(recordRepository.save(record));
    }

    /**
     * 按难度查排行榜
     */
    public List<GameRecordResponse> leaderboard(Difficulty difficulty) {
        return recordRepository
                .findByDifficultyOrderByMovesAscDurationSecondsAsc(difficulty, PageRequest.of(0, LEADERBOARD_SIZE))
                .stream().map(GameRecordResponse::from).toList();
    }

    /**
     * 全局统计
     */
    public Stats stats() {
        long total = recordRepository.count();
        long easy = recordRepository.countByDifficulty(Difficulty.EASY);
        long normal = recordRepository.countByDifficulty(Difficulty.NORMAL);
        long hard = recordRepository.countByDifficulty(Difficulty.HARD);
        return new Stats(total, easy, normal, hard);
    }

    /**
     * 统计信息记录
     */
    public record Stats(long totalGames, long easyGames, long normalGames, long hardGames) {
    }
}
