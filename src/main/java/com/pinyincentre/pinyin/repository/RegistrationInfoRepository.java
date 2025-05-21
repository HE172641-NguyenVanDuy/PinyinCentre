package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.dto.response.RegistrationInfoResponse;
import com.pinyincentre.pinyin.entity.RegistrationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationInfoRepository extends JpaRepository<RegistrationInfo, String> {
    @Query(value = """
        SELECT  r.id, r.fullname, r.phone_number, r.email, c.course_name, r.created_date, r.is_registered
        FROM (
        	SELECT id FROM registration_info WHERE is_registered = 0 AND ( is_delete = 0 OR is_delete IS NULL )
        	ORDER BY created_date DESC LIMIT :limit OFFSET :offset
        ) AS TEMP
        INNER JOIN registration_info AS r ON r.id = TEMP.id
        JOIN courses c ON r.course_id = c.id
        """, nativeQuery = true)
    List<RegistrationInfoResponse> getListNotRegistratedInfoWithPagination(
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Modifying
    @Query(value = """
    UPDATE registration_info SET is_registered = :isRegistered WHERE id = :id 
    """, nativeQuery = true)
    int updateIsRegistered(@Param("isRegistered")int isRegistered,@Param("id") String id);

    @Query(value = """
    SELECT  r.id, r.fullname, r.phone_number, r.email, c.course_name, r.created_date, r.is_registered
    FROM registration_info r JOIN courses c 
        ON c.id = r.course_id 
    WHERE id = :uuid
    """,nativeQuery = true)
    RegistrationInfoResponse findByUUID(@Param("uuid") String uuid);

}
