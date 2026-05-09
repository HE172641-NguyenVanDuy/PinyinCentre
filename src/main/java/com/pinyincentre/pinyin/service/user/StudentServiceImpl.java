package com.pinyincentre.pinyin.service.user;

import com.pinyincentre.pinyin.repository.ClassRepository;
import com.pinyincentre.pinyin.repository.ScheduleRepository;
import com.pinyincentre.pinyin.repository.UserClassRepository;
import com.pinyincentre.pinyin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final UserClassRepository userClassRepository;
    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;

    @Override
    public Map<String, Object> getStudentStats(String studentId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Total classes
        long totalClasses = userClassRepository.countByUserId(studentId);
        stats.put("totalClasses", totalClasses);
        
        // Today classes
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> todaySchedules = scheduleRepository.findSchedulesByStudentIdAndDateRange(
                studentId, today, today);
        stats.put("todayClasses", todaySchedules.size());

        // Weekly classes
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        
        List<Map<String, Object>> weeklySchedules = scheduleRepository.findSchedulesByStudentIdAndDateRange(
                studentId, startOfWeek, endOfWeek);
        stats.put("weeklyClasses", weeklySchedules.size());
        
        // Active classes (placeholder: same as total for now, or filter by date)
        stats.put("activeClasses", totalClasses);

        // Completed exams (placeholder)
        stats.put("completedExams", 0);
        
        // Classmates
        List<String> classIds = userClassRepository.findByUserId(studentId)
                .stream()
                .map(uc -> uc.getClassId())
                .collect(Collectors.toList());
        
        long classmatesCount = 0;
        if (!classIds.isEmpty()) {
            classmatesCount = userClassRepository.findAll().stream()
                    .filter(uc -> classIds.contains(uc.getClassId()) && !uc.getUserId().equals(studentId))
                    .map(uc -> uc.getUserId())
                    .distinct()
                    .count();
        }
        stats.put("classmates", classmatesCount);
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getStudentClasses(String studentId) {
        return classRepository.findClassesByStudentId(studentId);
    }

    @Override
    public List<Map<String, Object>> getStudentSchedule(String studentId, LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findSchedulesByStudentIdAndDateRange(studentId, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getStudentsByClass(String classId) {
        return userRepository.getStudentsInClass(classId).stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("fullName", p.getFullName());
                    map.put("email", p.getEmail());
                    map.put("username", p.getUsername());
                    return map;
                })
                .collect(Collectors.toList());
    }
}
