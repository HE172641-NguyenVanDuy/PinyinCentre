package com.pinyincentre.pinyin.dto.response;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ClassResponse {

    String className;
    String courseName;
    String teacherName;
    Timestamp startDate;
    Timestamp endDate;
    int maxStudents;
}
