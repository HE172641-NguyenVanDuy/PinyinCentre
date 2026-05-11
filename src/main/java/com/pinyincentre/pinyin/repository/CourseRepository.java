package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.dto.response.CourseResponse;
import com.pinyincentre.pinyin.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    @Query(value = """
    SELECT c.id, c.course_name, c.created_date, c.slot_quantity
    FROM courses c
    WHERE c.is_delete = 0
    ORDER BY c.created_date DESC
    LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<CourseResponse> getAllCourseActiveWithPagination(@Param("limit") int limit,
                                                          @Param("offset") int offset);

    @Query(value = """
    SELECT c.id, c.course_name, c.created_date, c.slot_quantity
    FROM courses c
    WHERE c.id = :id
    """, nativeQuery = true)
    CourseResponse getCourseById(@Param("id") String id);

    @Query(value = """
    SElECT c.course_name FROM Courses c WHERE c.id = :id
    """,nativeQuery = true)
    String findCourseNameById(@Param("id") String id);
    @Query(value = "SELECT COUNT(c.id) FROM courses c WHERE (c.is_delete = 0 OR c.is_delete IS NULL)", nativeQuery = true)
    long countTotalCourses();
    @Query(value = "SELECT c.course_name, (SELECT COUNT(uc.id) FROM user_class uc JOIN classes cl ON uc.class_id = cl.id WHERE cl.course_id = c.id) as student_count FROM courses c WHERE (c.is_delete = 0 OR c.is_delete IS NULL)", nativeQuery = true)
    List<Object[]> countEnrollmentPerCourse();
}
