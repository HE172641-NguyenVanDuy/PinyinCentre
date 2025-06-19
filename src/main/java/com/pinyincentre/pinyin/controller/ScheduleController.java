package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.entity.Schedule;
import com.pinyincentre.pinyin.entity.User;
import com.pinyincentre.pinyin.repository.ScheduleRepository;
import com.pinyincentre.pinyin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "https://www.pinyincentre.com")
public class ScheduleController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @PreAuthorize("hasAnyRole('ADMIN','CENTRE_OWNER')")
    @PostMapping("/available-teachers")
    public ResponseEntity<Map<String, Object>> getAvailableTeachers(@RequestBody Map<String, String> request) {
        LocalDate classDate = LocalDate.parse(request.get("class_date"));
        LocalTime startTime = LocalTime.parse(request.get("start_time"));
        LocalTime endTime = LocalTime.parse(request.get("end_time"));

        List<User> teachers = userRepository.findByRolesName("TEACHER");

        List<User> availableTeachers = teachers.stream()
                .filter(teacher -> !scheduleRepository.existsByTeacherIdAndDateTime(
                        teacher.getId(), classDate, startTime, endTime))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "data", availableTeachers
        ));
    }

    @GetMapping("/by-class/{classId}")
    public ResponseEntity<Map<String, Object>> getSchedulesByClassId(@PathVariable String classId) {
        try {
            List<Schedule> schedules = scheduleRepository.findScheduleByClassId(classId);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "data", schedules
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", "Lỗi khi lấy lịch: " + e.getMessage()
            ));
        }
    }

//    @PreAuthorize("hasAnyRole('TEACHER')")
    @GetMapping("/between")
    public ResponseEntity<Map<String, Object>> getSchedulesBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Schedule> schedules = scheduleRepository.findSchedulesBetweenDates(startDate, endDate);
            List<Map<String, Object>> scheduleDetails = schedules.stream()
                    .map(schedule -> Map.of(
                            "id", schedule.getId(),
                            "classDate", schedule.getClassDate(),
                            "startTime", schedule.getStartTime(),
                            "endTime", schedule.getEndTime(),
                            "className", schedule.getClassroom() != null ? schedule.getClassroom().getName() : "",
                            "teacher", schedule.getClassroom() != null && schedule.getClassroom().getTeacher() != null
                                    ? Map.of(
                                    "id", schedule.getClassroom().getTeacher().getId(),
                                    "name", schedule.getClassroom().getTeacher().getFullName()
                            )
                                    : Map.of(),
                            "description", schedule.getDescription(),
                            "link", schedule.getLink()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "data", scheduleDetails
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", "Lỗi khi lấy lịch: " + e.getMessage()
            ));
        }
    }
}
