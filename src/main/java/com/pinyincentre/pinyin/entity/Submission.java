package com.pinyincentre.pinyin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission extends UUIDBaseEntity {

    @Column(name = "assignment_id", nullable = false)
    private String assignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    private Assignment assignment;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private UserEntity student;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_id")
    private String fileId;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "score")
    private Double score;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "feedback_file_url")
    private String feedbackFileUrl;

    @Column(name = "feedback_file_id")
    private String feedbackFileId;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SubmissionStatus status;

    public enum SubmissionStatus {
        SUBMITTED,
        GRADED,
        LATE
    }
}
