package com.pinyincentre.pinyin.dto.response;

import java.sql.Timestamp;

public interface RegistrationInfoProjection {
    String getId();
    String getFullname();       // maps column "fullname"
    String getPhoneNumber();    // maps column "phone_number"
    String getEmail();          // maps column "email"
    String getCourseName();     // maps column "course_name"
    Timestamp getCreatedDate(); // maps column "created_date"
    Boolean getIsRegistered();  // maps column "is_registered"
}
