package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pinyincentre.pinyin.repository.UserRepository;
import com.pinyincentre.pinyin.repository.UserClassRepository;
import com.pinyincentre.pinyin.repository.ScheduleRepository;
import com.pinyincentre.pinyin.entity.UserEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final UserClassRepository userClassRepository;
    private final ScheduleRepository scheduleRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Map<String, Object>> getStats() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        String studentId = user.getId();

        long totalClasses = userClassRepository.countByUserId(studentId);
        
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        java.time.LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);

        long todayClasses = scheduleRepository.findSchedulesByStudentIdAndDateRange(studentId, today, today).size();
        long weeklyClasses = scheduleRepository.findSchedulesByStudentIdAndDateRange(studentId, startOfWeek, endOfWeek).size();

        Map<String, Object> stats = Map.of(
                "totalClasses", totalClasses,
                "todayClasses", todayClasses,
                "weeklyClasses", weeklyClasses,
                "activeClasses", totalClasses,
                "completedExams", 0 // Placeholder
        );

        return ApiResponse.<Map<String, Object>>builder()
                .status(200)
                .result(stats)
                .build();
    }

    @GetMapping("/classes")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<Map<String, Object>>> getMyClasses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        
        return ApiResponse.<List<Map<String, Object>>>builder()
                .status(200)
                .result(classRepository.findClassesByStudentId(user.getId()))
                .build();
    }

    @GetMapping("/classes/{classId}/students")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<Map<String, Object>>> getStudentsInClass(@org.springframework.web.bind.annotation.PathVariable("classId") String classId) {
        return ApiResponse.<List<Map<String, Object>>>builder()
                .status(200)
                .result(userRepository.findStudentsByClassId(classId))
                .build();
    }

    @GetMapping("/schedule")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<Map<String, Object>>> getMySchedule(
            @org.springframework.web.bind.annotation.RequestParam("startDate") String startDate,
            @org.springframework.web.bind.annotation.RequestParam("endDate") String endDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        
        java.time.LocalDate start = java.time.LocalDate.parse(startDate);
        java.time.LocalDate end = java.time.LocalDate.parse(endDate);
        
        return ApiResponse.<List<Map<String, Object>>>builder()
                .status(200)
                .result(scheduleRepository.findSchedulesByStudentIdAndDateRange(user.getId(), start, end))
                .build();
    }

    @GetMapping("/classes/{classId}/schedules")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<Map<String, Object>>> getClassSchedules(@org.springframework.web.bind.annotation.PathVariable("classId") String classId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        
        return ApiResponse.<List<Map<String, Object>>>builder()
                .status(200)
                .result(scheduleRepository.findSchedulesByClassIdAndStudentId(classId, user.getId()))
                .build();
    }
}
