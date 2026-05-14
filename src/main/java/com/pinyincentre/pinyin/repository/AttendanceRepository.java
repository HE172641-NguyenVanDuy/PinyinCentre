package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByScheduleId(Long scheduleId);
    List<Attendance> findByUserId(String userId);
    Optional<Attendance> findByUserIdAndScheduleId(String userId, Long scheduleId);

    @Query(value = """
        SELECT u.id, u.full_name as name, u.username as username,
               COUNT(CASE WHEN a.status = true THEN 1 END) as present,
               COUNT(CASE WHEN a.status = false THEN 1 END) as absent,
               COUNT(s.id) as total
        FROM users u
        JOIN user_class uc ON u.id = uc.user_id
        LEFT JOIN schedules s ON uc.class_id = s.class_id
        LEFT JOIN attendance a ON u.id = a.user_id AND s.id = a.schedule_id
        WHERE uc.class_id = :classId AND (s.is_delete = false OR s.is_delete IS NULL)
        GROUP BY u.id, u.full_name, u.username
    """, nativeQuery = true)
    List<Map<String, Object>> getClassSummary(@Param("classId") String classId);

    @Query(value = """
        SELECT a.id as id, s.class_date as classDate, cl.class_name as className, a.status as status
        FROM attendance a
        JOIN schedules s ON a.schedule_id = s.id
        JOIN classes cl ON s.class_id = cl.id
        WHERE a.user_id = :userId
        ORDER BY s.class_date DESC
    """, nativeQuery = true)
    List<Map<String, Object>> findDetailedByUserId(@Param("userId") String userId);
}
