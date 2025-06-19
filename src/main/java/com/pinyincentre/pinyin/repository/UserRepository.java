package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.entity.User;
import com.pinyincentre.pinyin.service.user.UserResponseProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByUsername(String username);
    //@Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Users WHERE email = :email", nativeQuery = true)
    boolean existsByEmail(String email);

   // @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Users WHERE username = :userName", nativeQuery = true)
    boolean existsByUsername( String userName);

    @Modifying
    @Query(value = "UPDATE USERS u SET u.status = :status WHERE u.id = :id", nativeQuery = true)
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
        U.address AS address
    FROM (
        SELECT id 
        FROM USERS 
        WHERE status = :status AND (is_delete = 0 OR is_delete IS NULL)
        ORDER BY created_date, username DESC 
        LIMIT :limit OFFSET :offset
    ) AS TMP
    INNER JOIN USERS AS U ON U.id = TMP.id
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
        U.address AS address    
        FROM USERS U JOIN user_roles R ON U.id = R.user_id
                 WHERE R.role_name = :role AND (is_delete = 0 OR is_delete IS NULL) AND Status = 1
         ORDER BY created_date, username DESC 
        LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<UserResponseProjection> getListUserByRole(@Param("role") String role, @Param("limit") int limit,
                                                   @Param("offset") int offset);


    List<User> findByRolesName(String roleName);

    @Query(value = """
    SELECT u.full_name FROM USERS u WHERE u.id = :id
    """,nativeQuery = true)
    String findFullNameById(@Param("id") String id);
}
