package com.example.demo.service;

import com.example.demo.entity.Attendance;
import com.example.demo.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 考勤业务逻辑层
 * <p>
 * 职责：封装考勤相关 CRUD 业务操作。
 * 为什么直接返回 Attendance 实体：考勤信息无敏感字段，无需 DTO 转换。
 * 为什么用 @Service：标记为 Spring Bean，由容器管理生命周期，便于事务控制和注入。
 * </p>
 */
@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    /**
     * 构造器注入依赖
     *
     * @param attendanceRepository 考勤数据访问层
     */
    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    /**
     * 查询所有考勤
     *
     * @return 考勤列表
     */
    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    /**
     * 创建考勤
     *
     * @param attendance 考勤信息
     * @return 创建后的考勤信息（含自增 ID）
     */
    public Attendance create(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    /**
     * 更新考勤信息
     * <p>
     * 仅更新非空字段，实现部分更新语义，避免误覆盖为空值。
     * </p>
     *
     * @param id 考勤 ID
     * @param attendance 更新数据
     * @return 更新后的考勤信息，考勤不存在时返回 null
     */
    public Attendance update(Long id, Attendance attendance) {
        Attendance existing = attendanceRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (attendance.getStudentNo() != null) {
            existing.setStudentNo(attendance.getStudentNo());
        }
        if (attendance.getCourseId() != null) {
            existing.setCourseId(attendance.getCourseId());
        }
        if (attendance.getDate() != null) {
            existing.setDate(attendance.getDate());
        }
        if (attendance.getStatus() != null) {
            existing.setStatus(attendance.getStatus());
        }
        return attendanceRepository.save(existing);
    }

    /**
     * 删除考勤
     *
     * @param id 考勤 ID
     * @return 删除成功返回 true，考勤不存在返回 false
     */
    public boolean delete(Long id) {
        if (!attendanceRepository.existsById(id)) {
            return false;
        }
        attendanceRepository.deleteById(id);
        return true;
    }
}
