package com.pinyincentre.pinyin.service.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StudentService {
    Map<String, Object> getStudentStats(String studentId);
    List<Map<String, Object>> getStudentClasses(String studentId);
    List<Map<String, Object>> getStudentSchedule(String studentId, LocalDate startDate, LocalDate endDate);
    List<Map<String, Object>> getStudentsByClass(String classId);
}
