package com.pinyincentre.pinyin.entity;

import jakarta.persistence.*;
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

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hsk_category_id")
    private HskCategory hskCategory;
}
