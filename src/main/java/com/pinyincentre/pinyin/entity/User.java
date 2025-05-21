package com.pinyincentre.pinyin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User extends UUIDBaseEntity implements Serializable {

    @Column(name="username", nullable=false)
    private String username;

    @Column(name="hashed_password", nullable=false)
    private String password;

    @Column(name="email")
    private String email;

    @Column(name="full_name")
    private String fullName;

    @Column(name="dob")
    private LocalDateTime dob;

    @Column(name="cic")
    private String cic;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name="expired_date")
    private LocalDateTime expiredDate;

    @Column(name="status")
    private int status;

}
