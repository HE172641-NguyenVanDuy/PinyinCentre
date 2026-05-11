package com.pinyincentre.pinyin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    // 1. Quản lý học viên
    private long totalStudents;
    private long newStudents;
    private long activeStudents;
    private long inactiveStudents;

    // 2. Quản lý giáo viên
    private long totalTeachers;
    private long teachingTeachers;
    private List<TeacherClassCount> classesPerTeacher;

    // 3. Quản lý khóa học / lớp học
    private long totalCourses;
    private long totalOpenClasses;
    private long upcomingClasses;
    private long fullClasses;

    @Data
    @AllArgsConstructor
    public static class TeacherClassCount {
        private String teacherName;
        private long classCount;
    }
}
