package com.pinyincentre.pinyin.dto.response;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String phoneNumber;
    private Timestamp dob;
    private String cic;
    private String fullName;
    private Timestamp updateDate;
    private Timestamp createDate;
    private Timestamp expireDate;
    private String status;
}
