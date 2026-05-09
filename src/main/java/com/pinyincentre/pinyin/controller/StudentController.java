package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.service.user.StudentService;
import com.pinyincentre.pinyin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final UserRepository userRepository;

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

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(Authentication authentication) {
        String studentId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .status(200)
                .result(studentService.getStudentStats(studentId))
                .build());
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getClasses(Authentication authentication) {
        String studentId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder()
                .status(200)
                .result(studentService.getStudentClasses(studentId))
                .build());
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/schedule")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSchedule(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String studentId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder()
                .status(200)
                .result(studentService.getStudentSchedule(studentId, startDate, endDate))
                .build());
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/classes/{classId}/students")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getStudents(@PathVariable String classId) {
        return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder()
                .status(200)
                .result(studentService.getStudentsByClass(classId))
                .build());
    }
}
