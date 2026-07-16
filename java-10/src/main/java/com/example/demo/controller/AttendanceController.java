package com.example.demo.controller;

import com.example.demo.entity.Attendance;
import com.example.demo.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 考勤管理控制器：提供考勤 CRUD 接口
 * <p>
 * RESTful 设计：
 * - GET    /api/attendances      查询所有考勤
 * - POST   /api/attendances      创建考勤
 * - PUT    /api/attendances/{id} 更新考勤
 * - DELETE /api/attendances/{id} 删除考勤
 * </p>
 */
@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * 查询所有考勤
     *
     * @return 考勤列表
     */
    @GetMapping
    public List<Attendance> findAll() {
        return attendanceService.findAll();
    }

    /**
     * 创建考勤
     *
     * @param attendance 考勤信息
     * @return 200 + 创建后的考勤信息
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Attendance attendance) {
        return ResponseEntity.ok(attendanceService.create(attendance));
    }

    /**
     * 更新考勤信息
     * <p>
     * 支持部分更新：仅更新请求体中提供的字段。
     * 考勤不存在时返回 400。
     * </p>
     *
     * @param id 考勤 ID
     * @param attendance 更新数据
     * @return 200 + 更新后的考勤信息，或 400 + 错误信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Attendance attendance) {
        Attendance updated = attendanceService.update(id, attendance);
        if (updated == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "考勤记录不存在"));
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除考勤
     *
     * @param id 考勤 ID
     * @return 200 + { "success": true }，或 400 + 错误信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!attendanceService.delete(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "考勤记录不存在"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}
