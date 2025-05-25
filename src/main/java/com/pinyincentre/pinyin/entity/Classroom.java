package com.pinyincentre.pinyin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Classroom extends UUIDBaseEntity implements Serializable {

    @Column(name = "class_name")
    private String className;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(name = "started_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "max_students")
    private int maxStudents;
}
