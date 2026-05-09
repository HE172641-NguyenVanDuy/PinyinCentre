package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Schedule s WHERE s.classId IN " +
            "(SELECT c.id FROM Classroom c WHERE c.teacherId = :teacherId) " +
            "AND s.classDate = :classDate " +
            "AND ((s.startTime < :endTime AND s.endTime > :startTime)) " +
            "AND s.isDelete = false")
    boolean existsByTeacherIdAndDateTime(String teacherId, LocalDate classDate,
                                         LocalTime startTime, LocalTime endTime);

    @Query("SELECT s FROM Schedule s WHERE s.classId = :classId AND (s.isDelete = false OR s.isDelete IS NULL)")
    List<Schedule> findActiveSchedulesByClassId(String classId);
 
    List<Schedule> findByClassId(String classId);

    @Query("SELECT s FROM Schedule s " +
            "LEFT JOIN FETCH s.classroom c " +
            "LEFT JOIN FETCH c.teacher t " +
            "WHERE s.classDate BETWEEN :startDate AND :endDate " +
            "AND s.isDelete = false " +
            "ORDER BY s.classDate ASC, s.startTime ASC")
    List<Schedule> findSchedulesBetweenDates(LocalDate startDate, LocalDate endDate);

    @Query(value = """
    SELECT s.id, s.class_date as class_date, s.start_time as start_time, 
           s.end_time as end_time, c.class_name as classroom_name, 
           co.course_name as course_name, s.link
    FROM schedules s
    JOIN classes c ON s.class_id = c.id
    JOIN courses co ON c.course_id = co.id
    JOIN user_class uc ON uc.class_id = c.id
    WHERE uc.user_id = :studentId 
      AND s.class_date BETWEEN :startDate AND :endDate
      AND (s.is_delete = false OR s.is_delete IS NULL)
    ORDER BY s.class_date ASC, s.start_time ASC
""", nativeQuery = true)
    List<Map<String, Object>> findSchedulesByStudentIdAndDateRange(
            @Param("studentId") String studentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}