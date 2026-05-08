package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByScheduleId(Long scheduleId);
    List<Attendance> findByUserId(String userId);
    Optional<Attendance> findByUserIdAndScheduleId(String userId, Long scheduleId);
}
