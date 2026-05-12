package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByOrderCode(Long orderCode);

    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status = 'PAID'", nativeQuery = true)
    Long getTotalRevenue();

    @Query(value = "SELECT c.course_name, SUM(p.amount) as revenue " +
                   "FROM payments p JOIN courses c ON p.course_id = c.id " +
                   "WHERE p.status = 'PAID' GROUP BY c.course_name", nativeQuery = true)
    List<Object[]> getRevenueByCourse();

    @Query(value = "SELECT COUNT(DISTINCT user_id) FROM payments WHERE status = 'PAID'", nativeQuery = true)
    Long getTotalPaidStudents();
}
