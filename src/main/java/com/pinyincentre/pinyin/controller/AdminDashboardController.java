package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.response.AdminDashboardResponse;
import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.dto.response.ClassSizeData;
import com.pinyincentre.pinyin.dto.response.TimeSeriesData;
import com.pinyincentre.pinyin.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.<AdminDashboardResponse>builder()
                .status(200)
                .result(adminDashboardService.getDashboardStats())
                .build());
    }

    @GetMapping("/registration-stats")
    public ResponseEntity<ApiResponse<List<TimeSeriesData>>> getRegistrationStats(@RequestParam(defaultValue = "30d") String period) {
        return ResponseEntity.ok(ApiResponse.<List<TimeSeriesData>>builder()
                .status(200)
                .result(adminDashboardService.getRegistrationStats(period))
                .build());
    }

    @GetMapping("/course-stats")
    public ResponseEntity<ApiResponse<List<TimeSeriesData>>> getCourseEnrollmentStats() {
        return ResponseEntity.ok(ApiResponse.<List<TimeSeriesData>>builder()
                .status(200)
                .result(adminDashboardService.getCourseEnrollmentStats())
                .build());
    }

    @GetMapping("/student-status-stats")
    public ResponseEntity<ApiResponse<List<TimeSeriesData>>> getStudentStatusStats() {
        return ResponseEntity.ok(ApiResponse.<List<TimeSeriesData>>builder()
                .status(200)
                .result(adminDashboardService.getStudentStatusStats())
                .build());
    }

    @GetMapping("/class-size-stats")
    public ResponseEntity<ApiResponse<List<ClassSizeData>>> getClassSizeStats() {
        return ResponseEntity.ok(ApiResponse.<List<ClassSizeData>>builder()
                .status(200)
                .result(adminDashboardService.getClassSizeStats())
                .build());
    }
}
