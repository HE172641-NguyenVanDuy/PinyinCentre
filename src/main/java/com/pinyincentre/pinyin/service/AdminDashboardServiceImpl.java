package com.pinyincentre.pinyin.service;

import com.pinyincentre.pinyin.dto.response.AdminDashboardResponse;
import com.pinyincentre.pinyin.dto.response.ClassSizeData;
import com.pinyincentre.pinyin.dto.response.TimeSeriesData;
import com.pinyincentre.pinyin.repository.ClassRepository;
import com.pinyincentre.pinyin.repository.CourseRepository;
import com.pinyincentre.pinyin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final CourseRepository courseRepository;

    @Override
    public AdminDashboardResponse getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        // 1. Student Stats
        long totalStudents = userRepository.countTotalStudents();
        long newStudents = userRepository.countNewStudents(thirtyDaysAgo);
        long activeStudents = userRepository.countStudentsByStatus(1); // ACTIVE
        long inactiveStudents = userRepository.countStudentsByStatus(2); // BAN/INACTIVE

        // 2. Teacher Stats
        long totalTeachers = userRepository.countTotalTeachers();
        long teachingTeachers = userRepository.countActiveTeachers();
        List<Object[]> classesPerTeacherRaw = userRepository.countClassesPerTeacher();
        List<AdminDashboardResponse.TeacherClassCount> classesPerTeacher = classesPerTeacherRaw.stream()
                .map(obj -> new AdminDashboardResponse.TeacherClassCount((String) obj[0], ((Number) obj[1]).longValue()))
                .collect(Collectors.toList());

        // 3. Course/Class Stats
        long totalCourses = courseRepository.countTotalCourses();
        long totalOpenClasses = classRepository.countTotalOpenClasses(now);
        long upcomingClasses = classRepository.countUpcomingClasses(now);
        long fullClasses = classRepository.countFullClasses();

        return AdminDashboardResponse.builder()
                .totalStudents(totalStudents)
                .newStudents(newStudents)
                .activeStudents(activeStudents)
                .inactiveStudents(inactiveStudents)
                .totalTeachers(totalTeachers)
                .teachingTeachers(teachingTeachers)
                .classesPerTeacher(classesPerTeacher)
                .totalCourses(totalCourses)
                .totalOpenClasses(totalOpenClasses)
                .upcomingClasses(upcomingClasses)
                .fullClasses(fullClasses)
                .build();
    }

    @Override
    public List<TimeSeriesData> getRegistrationStats(String period) {
        LocalDateTime since;
        List<Object[]> rawData;
        
        switch (period.toLowerCase()) {
            case "7d":
                since = LocalDateTime.now().minusDays(7);
                rawData = userRepository.countRegistrationsByDay(since);
                break;
            case "30d":
                since = LocalDateTime.now().minusDays(30);
                rawData = userRepository.countRegistrationsByDay(since);
                break;
            case "90d":
                since = LocalDateTime.now().minusDays(90);
                rawData = userRepository.countRegistrationsByMonth(since);
                break;
            case "1y":
            default:
                since = LocalDateTime.now().minusYears(1);
                rawData = userRepository.countRegistrationsByMonth(since);
                break;
        }

        return rawData.stream()
                .map(obj -> new TimeSeriesData(obj[0].toString(), ((Number) obj[1]).longValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSeriesData> getCourseEnrollmentStats() {
        List<Object[]> rawData = courseRepository.countEnrollmentPerCourse();
        return rawData.stream()
                .map(obj -> new TimeSeriesData((String) obj[0], ((Number) obj[1]).longValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSeriesData> getStudentStatusStats() {
        List<Object[]> rawData = userRepository.countStudentStatusDistribution();
        return rawData.stream()
                .map(obj -> new TimeSeriesData((String) obj[0], ((Number) obj[1]).longValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassSizeData> getClassSizeStats() {
        List<Object[]> rawData = classRepository.getClassSizeStats();
        return rawData.stream()
                .map(obj -> new ClassSizeData((String) obj[0], ((Number) obj[1]).longValue(), ((Number) obj[2]).intValue()))
                .collect(Collectors.toList());
    }
}
