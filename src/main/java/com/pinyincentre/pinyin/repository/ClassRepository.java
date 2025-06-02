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
import java.util.UUID;

@Repository
public interface ClassRepository extends JpaRepository<Classroom, UUID> {

    @Query(value = "SELECT c.className AS className, co.courseName AS courseName, \n" +
            "       u.fullName AS teacherName, c.startDate AS startDate, \n" +
            "       c.endDate AS endDate, c.maxStudents AS maxStudents " +
            " FROM Classroom c JOIN Course co ON c.courseId = co.id " +
            " JOIN User u ON c.teacherId = u.id " +
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


}
