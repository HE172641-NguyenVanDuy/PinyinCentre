package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.service.user.TeacherService;
import com.pinyincentre.pinyin.service.user.UserResponseProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final com.pinyincentre.pinyin.repository.UserRepository userRepository;

    private String getUserId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaimAsString("userId");
        if (userId == null) {
            String username = jwt.getSubject();
            return userRepository.findByUsername(username)
                    .map(u -> u.getId())
                    .orElse(null);
        }
        return userId;
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(Authentication authentication) {
        String teacherId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .status(200)
                .result(teacherService.getTeacherStats(teacherId))
                .build());
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<Object>> getClasses(Authentication authentication) {
        String teacherId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.builder()
                .status(200)
                .result(teacherService.getTeacherClasses(teacherId))
                .build());
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/schedule")
    public ResponseEntity<ApiResponse<Object>> getSchedule(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String teacherId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.builder()
                .status(200)
                .result(teacherService.getTeacherSchedule(teacherId, startDate, endDate))
                .build());
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/classes/{classId}/students")
    public ResponseEntity<ApiResponse<Object>> getStudents(@PathVariable String classId) {
        return ResponseEntity.ok(ApiResponse.builder()
                .status(200)
                .result(teacherService.getStudentsByClass(classId))
                .build());
    }
}
