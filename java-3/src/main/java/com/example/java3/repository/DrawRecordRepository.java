package com.example.java3.repository;

import com.example.java3.model.DrawRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrawRecordRepository extends JpaRepository<DrawRecord, Long> {
    List<DrawRecord> findAllByOrderByDrawnAtDesc();
}
