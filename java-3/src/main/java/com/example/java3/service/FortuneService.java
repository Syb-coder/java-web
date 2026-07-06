package com.example.java3.service;

import com.example.java3.dto.DrawRecordResponse;
import com.example.java3.dto.DrawRequest;
import com.example.java3.dto.FortuneResponse;
import com.example.java3.model.DrawRecord;
import com.example.java3.model.Fortune;
import com.example.java3.repository.DrawRecordRepository;
import com.example.java3.repository.FortuneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 抽签业务层
 */
@Service
@Transactional
public class FortuneService {

    private final FortuneRepository fortuneRepository;
    private final DrawRecordRepository drawRecordRepository;

    public FortuneService(FortuneRepository fortuneRepository, DrawRecordRepository drawRecordRepository) {
        this.fortuneRepository = fortuneRepository;
        this.drawRecordRepository = drawRecordRepository;
    }

    /**
     * 随机抽一支签，并记录此次抽取
     */
    public FortuneResponse draw(DrawRequest request) {
        List<Fortune> all = fortuneRepository.findAll();
        if (all.isEmpty()) {
            throw new IllegalStateException("签文库为空");
        }
        Fortune picked = all.get(ThreadLocalRandom.current().nextInt(all.size()));

        // 记录此次抽取
        DrawRecord record = new DrawRecord();
        record.setFortuneId(picked.getId());
        record.setFortuneNumber(picked.getNumber());
        record.setLevel(picked.getLevel());
        record.setQuestion(request == null ? null : request.question());
        drawRecordRepository.save(record);

        return FortuneResponse.from(picked);
    }

    /**
     * 查看指定签
     */
    public FortuneResponse get(Long id) {
        Fortune f = fortuneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("签不存在: " + id));
        return FortuneResponse.from(f);
    }

    /**
     * 抽签历史
     */
    public List<DrawRecordResponse> history() {
        return drawRecordRepository.findAllByOrderByDrawnAtDesc()
                .stream().map(DrawRecordResponse::from).toList();
    }

    /**
     * 删除一条历史记录
     */
    public void deleteRecord(Long id) {
        if (!drawRecordRepository.existsById(id)) {
            throw new IllegalArgumentException("记录不存在: " + id);
        }
        drawRecordRepository.deleteById(id);
    }

    /**
     * 签文库统计
     */
    public long totalFortunes() {
        return fortuneRepository.count();
    }
}
