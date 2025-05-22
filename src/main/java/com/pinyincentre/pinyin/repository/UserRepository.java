package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    //@Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Users WHERE email = :email", nativeQuery = true)
    boolean existsByEmail(String email);

   // @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Users WHERE username = :userName", nativeQuery = true)
    boolean existsByUsername( String userName);

    @Modifying
    @Query(value = "UPDATE USERS u SET u.status = :status WHERE u.id = :id", nativeQuery = true)
    int changeStatusUserById(@Param("id") String id,@Param("status") int status);

    @Query(value = """
    				SELECT U.id, U.username, U.email, U.phone_number, U.dob, u.cic, U.full_name, U.updated_date, U.created_date, U.expired_date, status
    				FROM (
    					SELECT id FROM USERS WHERE status = :status AND ( is_delete = 0 OR is_delete IS NULL )
    					ORDER BY created_date, username DESC LIMIT :limit OFFSET :offset
    				) AS TMP
    				INNER JOIN USERS AS U ON U.id = TMP.id
    """, nativeQuery = true)
    List<UserResponse> listUserPagination(@Param("status")int status,  @Param("limit") int limit,
                                          @Param("offset") int offset);


}
