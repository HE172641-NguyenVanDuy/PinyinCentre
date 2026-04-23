package com.pinyincentre.pinyin.service.user;

import com.pinyincentre.pinyin.entity.Classroom;
import com.pinyincentre.pinyin.entity.Schedule;
import com.pinyincentre.pinyin.repository.*;
import com.pinyincentre.pinyin.service.user.UserResponseProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final ClassRepository classRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserClassRepository userClassRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public Map<String, Object> getTeacherStats(String teacherId) {
        if (teacherId == null) return new HashMap<>();

        List<Classroom> teacherClasses = classRepository.findAll().stream()
                .filter(c -> Objects.equals(teacherId, c.getTeacherId()) && (c.getIsDelete() == null || !c.getIsDelete()))
                .collect(Collectors.toList());

        long totalClasses = teacherClasses.size();
        long totalStudents = teacherClasses.stream()
                .mapToLong(c -> userClassRepository.countByClassId(c.getId()))
                .sum();

        LocalDate today = LocalDate.now();
        long todayClasses = 0;
        double weeklyHours = 0;

        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);

        for (Classroom cls : teacherClasses) {
            List<Schedule> schedules = scheduleRepository.findActiveSchedulesByClassId(cls.getId());
            for (Schedule s : schedules) {
                if (s.getClassDate().equals(today)) {
                    todayClasses++;
                }
                if (!s.getClassDate().isBefore(startOfWeek) && !s.getClassDate().isAfter(endOfWeek)) {
                    if (s.getStartTime() != null && s.getEndTime() != null) {
                        long minutes = java.time.Duration.between(s.getStartTime(), s.getEndTime()).toMinutes();
                        weeklyHours += minutes / 60.0;
                    }
                }
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalClasses", totalClasses);
        stats.put("totalStudents", totalStudents);
        stats.put("todayClasses", todayClasses);
        stats.put("weeklyHours", Math.round(weeklyHours * 10.0) / 10.0);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getTeacherClasses(String teacherId) {
        if (teacherId == null) return Collections.emptyList();

        return classRepository.findAll().stream()
                .filter(c -> Objects.equals(teacherId, c.getTeacherId()) && (c.getIsDelete() == null || !c.getIsDelete()))
                .map(cls -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", cls.getId());
                    map.put("name", cls.getName());
                    map.put("course_name", courseRepository.findById(cls.getCourseId())
                            .map(co -> co.getCourseName()).orElse("N/A"));
                    map.put("student_count", userClassRepository.countByClassId(cls.getId()));
                    map.put("start_date", cls.getStartDate());
                    map.put("end_date", cls.getEndDate());
                    
                    // Add students detail for each class as requested
                    List<UserResponseProjection> students = getStudentsByClass(cls.getId());
                    map.put("students", students);
                    
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTeacherSchedule(String teacherId, LocalDate startDate, LocalDate endDate) {
        if (teacherId == null) return Collections.emptyList();

        List<Schedule> allSchedules = scheduleRepository.findSchedulesBetweenDates(startDate, endDate);
        return allSchedules.stream()
                .filter(s -> s.getClassroom() != null && Objects.equals(teacherId, s.getClassroom().getTeacherId()))
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", s.getId());
                    map.put("class_date", s.getClassDate());
                    map.put("start_time", s.getStartTime());
                    map.put("end_time", s.getEndTime());
                    map.put("classroom_id", s.getClassId());
                    map.put("classroom_name", s.getClassroom().getName());
                    map.put("course_name", courseRepository.findById(s.getClassroom().getCourseId())
                            .map(co -> co.getCourseName()).orElse("N/A"));
                    map.put("link", s.getLink());
                    map.put("description", s.getDescription());
                    map.put("student_count", userClassRepository.countByClassId(s.getClassId()));
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseProjection> getStudentsByClass(String classId) {
        return userRepository.getStudentsInClass(classId);
    }
}
