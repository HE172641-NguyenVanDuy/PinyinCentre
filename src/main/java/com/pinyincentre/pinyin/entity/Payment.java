package com.pinyincentre.pinyin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends UUIDBaseEntity {

    @Column(name = "order_code", unique = true, nullable = false)
    private Long orderCode;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "status", nullable = false)
    private String status; // PENDING, PAID, CANCELLED

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;
}
