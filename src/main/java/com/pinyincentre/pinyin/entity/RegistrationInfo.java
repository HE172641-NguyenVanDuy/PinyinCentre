package com.pinyincentre.pinyin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "registration_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RegistrationInfo extends UUIDBaseEntity implements Serializable {

    @Column(name="fullname", nullable=false)
    private String fullName;

    @Column(name="email", nullable=false)
    private String email;

    @Column(name="phone_number", nullable=false, length = 20)
    private String phoneNumber;

    @Column(name="description")
    private String description;

    @Column(name="location")
    private String address;

    @Column(name="is_registered")
    private boolean isRegistered;

    @Column(name="course_id", nullable=false)
    private String courseId;
}
