package com.pinyincentre.pinyin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment extends UUIDBaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_id")
    private String fileId;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(name = "class_id", nullable = false)
    private String classId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private Classroom classroom;

    @Column(name = "teacher_id", nullable = false)
    private String teacherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private UserEntity teacher;
}
