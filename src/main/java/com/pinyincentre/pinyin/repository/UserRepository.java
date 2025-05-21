package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.dto.response.UserResponse;
import com.pinyincentre.pinyin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    @Query(value = "SELECT COUNT(u) > 0 FROM Users u WHERE u.email = :email", nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);

    @Query(value = "SELECT COUNT(u) > 0 FROM Users u WHERE u.user_name = :userName", nativeQuery = true)
    boolean existsByUsername(@Param("userName") String userName);

    @Query(value = "UPDATE USERS u SET u.status = :status WHERE u.id = :id", nativeQuery = true)
    int changeStatusUserById(@Param("id") String id,@Param("status") int status);

    @Query(value = """
    				SELECT U.id, U.username, U.email, U.phone_number, U.dob, u.cic, U.full_name, U.update_date, U.create_date, U.expired_date, status
    				FROM (
    					SELECT id FROM USERS WHERE status = :status AND ( is_delete = 0 OR is_delete IS NULL )
    					ORDER BY created_date, username DESC LIMIT :limit OFFSET :offset
    				) AS TMP
    				INNER JOIN USERS AS U ON U.id = TMP.id
    """, nativeQuery = true)
    List<UserResponse> listUserPagination(@Param("status")int status,  @Param("limit") int limit,
                                          @Param("offset") int offset);


}
