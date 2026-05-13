package com.pinyincentre.pinyin.dto.response;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HskCategoryResponse {
    private String id;
    private String name;
    private String description;
    private Timestamp createdDate;
    private List<CourseResponse> courses;
}
