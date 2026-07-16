package com.example.demo.repository;

import com.example.demo.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 考勤数据访问层
 * <p>
 * 为什么继承 JpaRepository：自动提供 save、findById、findAll、deleteById 等基础 CRUD 方法，
 * 无需手写 SQL。
 * </p>
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
