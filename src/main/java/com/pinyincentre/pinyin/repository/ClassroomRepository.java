package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, String> {
    List<Classroom> findByCourseId(String courseId);
}
