package com.pinyincentre.pinyin.repository;

import com.pinyincentre.pinyin.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
    List<Submission> findByAssignmentId(String assignmentId);
    Optional<Submission> findByAssignmentIdAndStudentId(String assignmentId, String studentId);
}
