package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedDateDesc(String userId);
    long countByUserIdAndIsReadFalse(String userId);
}
