package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.entity.Attendance;
import com.pinyincentre.pinyin.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/by-schedule/{scheduleId}")
    public ResponseEntity<Map<String, Object>> getBySchedule(@PathVariable Long scheduleId) {
        List<Attendance> list = attendanceRepository.findByScheduleId(scheduleId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "result", list
        ));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Map<String, Object>> getByUser(@PathVariable String userId) {
        List<Map<String, Object>> list = attendanceRepository.findDetailedByUserId(userId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "result", list
        ));
    }

    @PostMapping("/save-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRE_OWNER', 'TEACHER')")
    public ResponseEntity<Map<String, Object>> saveAll(@RequestBody List<AttendanceRequest> requests) {
        try {
            for (AttendanceRequest req : requests) {
                Optional<Attendance> existing = attendanceRepository.findByUserIdAndScheduleId(req.getUserId(), req.getScheduleId());
                Attendance attendance;
                if (existing.isPresent()) {
                    attendance = existing.get();
                } else {
                    attendance = new Attendance();
                    attendance.setUserId(req.getUserId());
                    attendance.setScheduleId(req.getScheduleId());
                }
                attendance.setStatus(req.getStatus());
                attendanceRepository.save(attendance);
            }
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Lưu điểm danh thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", "Lỗi khi lưu điểm danh: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/class-summary/{classId}")
    public ResponseEntity<Map<String, Object>> getSummaryByClass(@PathVariable String classId) {
        List<Map<String, Object>> summary = attendanceRepository.getClassSummary(classId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "result", summary
        ));
    }

    @lombok.Data
    public static class AttendanceRequest {
        private String userId;
        private Long scheduleId;
        private Boolean status;
    }
}
