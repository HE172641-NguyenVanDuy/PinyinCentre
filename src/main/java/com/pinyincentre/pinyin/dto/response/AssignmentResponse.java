package com.pinyincentre.pinyin.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponse {
    private String id;
    private String title;
    private String description;
    private String fileUrl;
    private String fileId;
    private LocalDateTime deadline;
    private Double totalScore;
    private String classId;
    private String teacherId;
    private LocalDateTime createdDate;
    private String status; // Added for UI status (Submitted, Graded, etc.)
}
