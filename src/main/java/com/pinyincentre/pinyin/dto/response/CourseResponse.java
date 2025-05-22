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
public class CourseResponse {

    private String id;

    private String courseName;

    private int slotNumber;
//    private Timestamp startTime;
//
//    private Timestamp endTime;


}
