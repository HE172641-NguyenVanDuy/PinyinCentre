package com.pinyincentre.pinyin.dto.response;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RegistrationInfoResponse {

    private String id;

    private String fullName;

    private String phoneNumber;

    private String email;

    private String courseName;

    private Timestamp createdDate;

    private boolean isRegistered;
}
