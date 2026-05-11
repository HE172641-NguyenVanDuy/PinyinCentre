package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.UserEntity;
import com.pinyincentre.pinyin.service.user.UserResponseProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
    Optional<UserEntity> findByUsername(String username);
    //@Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Users WHERE email = :email", nativeQuery = true)
    boolean existsByEmail(String email);

   // @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Users WHERE username = :userName", nativeQuery = true)
    boolean existsByUsername( String userName);
    Optional<UserEntity> findByEmail(String email);

    UserEntity getFirstByUsername(String username);
    UserEntity getFirstByEmail(String email);
    Optional<UserEntity> findFirstByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

    @Modifying
    @Query(value = "UPDATE users u SET u.status = :status WHERE u.id = :id", nativeQuery = true)
    int changeStatusUserById(@Param("id") String id,@Param("status") int status);

    @Query(value = """
    SELECT 
        U.id AS id,
        U.username AS username,
        U.email AS email,
        U.phone_number AS phoneNumber,
        U.dob AS dob,
        U.cic AS cic,
        U.full_name AS fullName,
        U.updated_date AS updateDate,
        U.created_date AS createDate,
        U.expired_date AS expireDate,
        U.status AS status,
        U.address AS address,
        U.gender AS gender
    FROM (
        SELECT id 
        FROM users 
        WHERE status = :status AND (is_delete = 0 OR is_delete IS NULL)
        ORDER BY created_date DESC, username DESC 
        LIMIT :limit OFFSET :offset
    ) AS TMP
    INNER JOIN users AS U ON U.id = TMP.id
    """, nativeQuery = true)
    List<UserResponseProjection> listUserPagination(@Param("status") int status,
                                                    @Param("limit") int limit,
                                                    @Param("offset") int offset);


    @Query(value = """
        SELECT 
        U.id AS id,
        U.username AS username,
        U.email AS email,
        U.phone_number AS phoneNumber,
        U.dob AS dob,
        U.cic AS cic,
        U.full_name AS fullName,
        U.updated_date AS updateDate,
        U.created_date AS createDate,
        U.expired_date AS expireDate,
        U.status AS status,
        U.address AS address,
        U.gender AS gender
        FROM users U JOIN user_roles R ON U.id = R.user_id
                 WHERE R.role_name = :roleEntity AND (is_delete = 0 OR is_delete IS NULL) AND Status = :status
          ORDER BY created_date DESC, username DESC 
        LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<UserResponseProjection> getListUserByRole(@Param("roleEntity") String roleEntity, 
                                                   @Param("status") int status,
                                                   @Param("limit") int limit,
                                                   @Param("offset") int offset);


    @Query(value = """
        SELECT u.* FROM users u 
        JOIN user_roles r ON u.id = r.user_id 
        WHERE r.role_name = :roleName 
        AND u.status = :status 
        AND (u.is_delete = 0 OR u.is_delete IS NULL)
    """, nativeQuery = true)
    List<UserEntity> findActiveTeachersByRole(@Param("roleName") String roleName, @Param("status") int status);

    @Query(value = """
    SELECT u.full_name FROM USERS u WHERE u.id = :id
    """,nativeQuery = true)
    String findFullNameById(@Param("id") String id);

    @Query(value = """
        SELECT 
            U.id AS id, U.username AS username, U.email AS email, U.phone_number AS phoneNumber,
            U.dob AS dob, U.cic AS cic, U.full_name AS fullName, U.updated_date AS updateDate,
            U.created_date AS createDate, U.expired_date AS expireDate, U.status AS status, U.address AS address,
            U.gender AS gender
        FROM users U
        INNER JOIN user_class UC ON U.id = UC.user_id
        WHERE UC.class_id = :classId
    """, nativeQuery = true)
    List<UserResponseProjection> getStudentsInClass(@Param("classId") String classId);

    @Query(value = """
        SELECT 
            U.id AS id, U.username AS username, U.email AS email, U.phone_number AS phoneNumber,
            U.dob AS dob, U.cic AS cic, U.full_name AS fullName, U.updated_date AS updateDate,
            U.created_date AS createDate, U.expired_date AS expireDate, U.status AS status, U.address AS address,
            U.gender AS gender
        FROM users U
        INNER JOIN user_roles R ON U.id = R.user_id
        WHERE R.role_name = 'STUDENT' AND U.status = 1 AND (U.is_delete = 0 OR U.is_delete IS NULL)
        AND NOT EXISTS (
            SELECT 1 FROM user_class UC WHERE UC.user_id = U.id AND UC.class_id = :classId
        )
    """, nativeQuery = true)
    List<UserResponseProjection> getStudentsNotInClass(@Param("classId") String classId);
    @Query(value = "SELECT COUNT(u.id) FROM users u JOIN user_roles r ON u.id = r.user_id WHERE r.role_name = 'STUDENT' AND (u.is_delete = 0 OR u.is_delete IS NULL)", nativeQuery = true)
    long countTotalStudents();

    @Query(value = "SELECT COUNT(u.id) FROM users u JOIN user_roles r ON u.id = r.user_id WHERE r.role_name = 'STUDENT' AND (u.is_delete = 0 OR u.is_delete IS NULL) AND u.created_date >= :since", nativeQuery = true)
    long countNewStudents(@Param("since") java.time.LocalDateTime since);

    @Query(value = "SELECT COUNT(u.id) FROM users u JOIN user_roles r ON u.id = r.user_id WHERE r.role_name = 'STUDENT' AND (u.is_delete = 0 OR u.is_delete IS NULL) AND u.status = :status", nativeQuery = true)
    long countStudentsByStatus(@Param("status") int status);

    @Query(value = "SELECT COUNT(u.id) FROM users u JOIN user_roles r ON u.id = r.user_id WHERE r.role_name = 'TEACHER' AND (u.is_delete = 0 OR u.is_delete IS NULL)", nativeQuery = true)
    long countTotalTeachers();

    @Query(value = "SELECT COUNT(u.id) FROM users u JOIN user_roles r ON u.id = r.user_id WHERE r.role_name = 'TEACHER' AND (u.is_delete = 0 OR u.is_delete IS NULL) AND u.status = 1", nativeQuery = true)
    long countActiveTeachers();

    @Query(value = "SELECT u.full_name as teacherName, COUNT(c.id) as classCount FROM users u JOIN user_roles r ON u.id = r.user_id LEFT JOIN classes c ON u.id = c.teacher_id WHERE r.role_name = 'TEACHER' AND (u.is_delete = 0 OR u.is_delete IS NULL) GROUP BY u.id, u.full_name", nativeQuery = true)
    List<Object[]> countClassesPerTeacher();
    @Query(value = "SELECT DATE(u.created_date) as date, COUNT(u.id) as count FROM users u JOIN user_roles r ON u.id = r.user_id WHERE r.role_name = 'STUDENT' AND u.created_date >= :since GROUP BY DATE(u.created_date) ORDER BY DATE(u.created_date) ASC", nativeQuery = true)
    List<Object[]> countRegistrationsByDay(@Param("since") java.time.LocalDateTime since);

    @Query(value = "SELECT DATE_FORMAT(u.created_date, '%Y-%m') as month, COUNT(u.id) as count FROM users u JOIN user_roles r ON u.id = r.user_id WHERE r.role_name = 'STUDENT' AND u.created_date >= :since GROUP BY DATE_FORMAT(u.created_date, '%Y-%m') ORDER BY DATE_FORMAT(u.created_date, '%Y-%m') ASC", nativeQuery = true)
    List<Object[]> countRegistrationsByMonth(@Param("since") java.time.LocalDateTime since);
    @Query(value = "SELECT CASE WHEN u.status = 1 THEN 'Active' WHEN u.status = 2 THEN 'Ban' ELSE 'Unknown' END as status, COUNT(u.id) as count FROM users u JOIN user_roles r ON u.id = r.user_id WHERE r.role_name = 'STUDENT' AND (u.is_delete = 0 OR u.is_delete IS NULL) GROUP BY u.status", nativeQuery = true)
    List<Object[]> countStudentStatusDistribution();
}
