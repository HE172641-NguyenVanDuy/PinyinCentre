package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    List<Assignment> findByClassId(String classId);
    List<Assignment> findByTeacherId(String teacherId);
}
