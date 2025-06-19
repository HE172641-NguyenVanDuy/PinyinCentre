package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserClassRepository extends JpaRepository<UserClass, Long> {
    long countByClassId(String classId);
    List<UserClass> findByClassId(String classId);
    @Modifying
    @Transactional
    @Query("DELETE FROM UserClass uc WHERE uc.userId = :userId AND uc.classId = :classId")
    void deleteByUserIdAndClassId(String userId, String classId);
}