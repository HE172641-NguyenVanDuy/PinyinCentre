package com.pinyincentre.pinyin.service.user;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public interface UserResponseProjection {
    String getId();
    String getUsername();
    String getEmail();
    String getPhoneNumber();
    Date getDob();          // ✅ đổi từ Timestamp
    String getCic();
    String getFullName();
    LocalDateTime   getUpdateDate();   // ✅ đổi từ Timestamp
    Timestamp   getCreateDate();   // ✅ đổi từ Timestamp
    LocalDateTime   getExpireDate();   // ✅ đổi từ Timestamp
    String getStatus();
    String getAddress();
}