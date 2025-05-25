package com.pinyincentre.pinyin.dto.response;

import java.time.LocalDateTime;

public interface ClassResponseProjection {
    String getClassName();
    String getCourseName();
    String getTeacherName();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    int getMaxStudents();
}
