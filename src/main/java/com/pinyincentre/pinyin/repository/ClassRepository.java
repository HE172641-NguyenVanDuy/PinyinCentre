package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.dto.response.ClassResponse;
import com.pinyincentre.pinyin.dto.response.LibraryResponse;
import com.pinyincentre.pinyin.entity.Classroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface ClassRepository extends JpaRepository<Classroom, String> {

    @Query(value = "SELECT c.name AS className, co.courseName AS courseName, \n" +
            "       u.fullName AS teacherName, c.startDate AS startDate, \n" +
            "       c.endDate AS endDate, c.maxStudents AS maxStudents " +
            " FROM Classroom c JOIN Course co ON c.courseId = co.id " +
            " JOIN UserEntity u ON c.teacherId = u.id " +
            " WHERE c.isDelete = :isDelete ORDER BY c.createdDate DESC ")
    Page<Object[]> findAllClassesByStatusPagination(@Param("isDelete") boolean status, Pageable pageable);

    @Query(value = """
    SELECT c.class_name, co.course_name,u.full_name, c.started_date, c.end_date,c.max_students FROM classes c join courses co\s
    on c.course_id = co.id
    JOIN users u\s
    ON u.id = c.teacher_id
    WHERE c.id = :id
    """, nativeQuery = true)
    ClassResponse findClassById(@Param("id") String id);

    @Query(value = """
    SELECT c.id, c.class_name, c.course_id, c.teacher_id, 
           c.started_date, c.end_date, c.max_students,
           c.created_date, c.updated_date, c.is_delete
    FROM classes c 
    WHERE c.id = :id
""", nativeQuery = true)
    Classroom findByClassId(@Param("id") String id);

    @Query(value = """
    SELECT c.class_name, co.course_name,u.full_name, c.started_date, c.end_date,c.max_students FROM classes c join courses co\s
    on c.course_id = co.id
    JOIN users u\s
    ON u.id = c.teacher_id
    WHERE co.id = :id
    """,nativeQuery = true)
    List<ClassResponse> findClassByCourseId(@Param("id") String id);


    @Query(value = """
    SELECT c.id, c.class_name as name, co.course_name as course_name, 
           c.started_date as start_date, c.end_date as end_date,
           (SELECT COUNT(*) FROM user_class uc2 WHERE uc2.class_id = c.id) as student_count,
           (SELECT COUNT(*) FROM schedules s WHERE s.class_id = c.id AND (s.is_delete = false OR s.is_delete IS NULL)) as schedule_count
    FROM classes c
    JOIN courses co ON c.course_id = co.id
    JOIN user_class uc ON uc.class_id = c.id
    WHERE uc.user_id = :studentId AND (c.is_delete = false OR c.is_delete IS NULL)
    """, nativeQuery = true)
    List<Map<String, Object>> findClassesByStudentId(@Param("studentId") String studentId);
}
