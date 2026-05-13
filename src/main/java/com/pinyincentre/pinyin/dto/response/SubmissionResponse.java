package com.pinyincentre.pinyin.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponse {
    private String id;
    private String assignmentId;
    private String studentId;
    private String studentName;
    private String fileUrl;
    private String fileId;
    private LocalDateTime submittedAt;
    private Double score;
    private String comment;
    private String feedbackFileUrl;
    private LocalDateTime gradedAt;
    private String status;
}
