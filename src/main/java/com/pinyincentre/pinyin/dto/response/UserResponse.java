package com.pinyincentre.pinyin.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserResponse {
    String id;
    String username;
    String email;
    String phoneNumber;
    LocalDateTime birthday;
    String cic;
    String fullName;
    LocalDateTime updateDate;
    LocalDateTime createDate;
    LocalDateTime expireDate;
    int status;
}
