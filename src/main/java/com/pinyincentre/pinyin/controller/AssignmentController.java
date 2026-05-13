package com.pinyincentre.pinyin.controller;

import com.pinyincentre.pinyin.dto.request.AssignmentRequest;
import com.pinyincentre.pinyin.dto.request.SubmissionRequest;
import com.pinyincentre.pinyin.dto.response.ApiResponse;
import com.pinyincentre.pinyin.dto.response.AssignmentResponse;
import com.pinyincentre.pinyin.dto.response.SubmissionResponse;
import com.pinyincentre.pinyin.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
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

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<AssignmentResponse>> createAssignment(
            @Valid @ModelAttribute AssignmentRequest request,
            Authentication authentication) throws IOException {
        String teacherId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<AssignmentResponse>builder()
                .status(200)
                .result(assignmentService.createAssignment(request, teacherId))
                .build());
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignmentsByClass(
            @PathVariable String classId,
            Authentication authentication) {
        String userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<List<AssignmentResponse>>builder()
                .status(200)
                .result(assignmentService.getAssignmentsByClass(classId, userId))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignmentById(
            @PathVariable String id,
            Authentication authentication) {
        String userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<AssignmentResponse>builder()
                .status(200)
                .result(assignmentService.getAssignmentById(id, userId))
                .build());
    }

    @PostMapping(value = "/submit", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submitAssignment(
            @Valid @ModelAttribute SubmissionRequest request,
            Authentication authentication) throws IOException {
        String studentId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<SubmissionResponse>builder()
                .status(200)
                .result(assignmentService.submitAssignment(request, studentId))
                .build());
    }

    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissions(
            @PathVariable String assignmentId) {
        return ResponseEntity.ok(ApiResponse.<List<SubmissionResponse>>builder()
                .status(200)
                .result(assignmentService.getSubmissionsByAssignment(assignmentId))
                .build());
    }

    @GetMapping("/{assignmentId}/my-submission")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getMySubmission(
            @PathVariable String assignmentId,
            Authentication authentication) {
        String studentId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<SubmissionResponse>builder()
                .status(200)
                .result(assignmentService.getStudentSubmission(assignmentId, studentId))
                .build());
    }

    @PutMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<ApiResponse<SubmissionResponse>> gradeSubmission(
            @PathVariable String submissionId,
            @RequestParam Double score,
            @RequestParam String comment,
            Authentication authentication) {
        String teacherId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<SubmissionResponse>builder()
                .status(200)
                .result(assignmentService.gradeSubmission(submissionId, score, comment, teacherId))
                .build());
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<AssignmentResponse>> updateAssignment(
            @PathVariable String id,
            @Valid @ModelAttribute AssignmentRequest request,
            Authentication authentication) throws IOException {
        String teacherId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.<AssignmentResponse>builder()
                .status(200)
                .result(assignmentService.updateAssignment(id, request, teacherId))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(
            @PathVariable String id,
            Authentication authentication) {
        String teacherId = getUserId(authentication);
        assignmentService.deleteAssignment(id, teacherId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Đã xóa bài tập thành công")
                .build());
    }
}
