package com.pinyincentre.pinyin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name="expired_date")
    private LocalDateTime expiredDate;

    @Column(name="status")
    private int status;

    @Column(name="address")
    private String address;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles", // bảng trung gian
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_name")
    )
    private Set<Role> roles;
}
