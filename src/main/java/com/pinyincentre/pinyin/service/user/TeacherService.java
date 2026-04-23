package com.pinyincentre.pinyin.service.user;

import com.pinyincentre.pinyin.service.user.UserResponseProjection;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TeacherService {
    Map<String, Object> getTeacherStats(String teacherId);
    List<Map<String, Object>> getTeacherClasses(String teacherId);
    List<Map<String, Object>> getTeacherSchedule(String teacherId, LocalDate startDate, LocalDate endDate);
    List<UserResponseProjection> getStudentsByClass(String classId);
}
