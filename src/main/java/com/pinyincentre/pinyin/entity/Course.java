package com.pinyincentre.pinyin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Course extends UUIDBaseEntity {
    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "slot_quantity", nullable = false)
    private int slotNumber;
}
