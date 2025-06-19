package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    List<Schedule> findScheduleByClassId(String classId);

    @Query("SELECT s FROM Schedule s " +
            "LEFT JOIN FETCH s.classroom c " +
            "LEFT JOIN FETCH c.teacher t " +
            "WHERE s.classDate BETWEEN :startDate AND :endDate " +
            "AND s.isDelete = false " +
            "ORDER BY s.classDate ASC, s.startTime ASC")
    List<Schedule> findSchedulesBetweenDates(LocalDate startDate, LocalDate endDate);

}