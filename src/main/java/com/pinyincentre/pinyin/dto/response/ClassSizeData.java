package com.pinyincentre.pinyin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSizeData {
    private String className;
    private long currentStudents;
    private int maxStudents;
}
