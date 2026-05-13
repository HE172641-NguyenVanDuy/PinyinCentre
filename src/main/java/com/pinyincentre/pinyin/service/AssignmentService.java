package com.pinyincentre.pinyin.service;

import com.pinyincentre.pinyin.dto.request.AssignmentRequest;
import com.pinyincentre.pinyin.dto.request.SubmissionRequest;
import com.pinyincentre.pinyin.dto.response.AssignmentResponse;
import com.pinyincentre.pinyin.dto.response.SubmissionResponse;

import java.io.IOException;
import java.util.List;

public interface AssignmentService {
    AssignmentResponse createAssignment(AssignmentRequest request, String teacherId) throws IOException;
    List<AssignmentResponse> getAssignmentsByClass(String classId, String userId);
    AssignmentResponse getAssignmentById(String id, String userId);
    
    SubmissionResponse submitAssignment(SubmissionRequest request, String studentId) throws IOException;
    List<SubmissionResponse> getSubmissionsByAssignment(String assignmentId);
    SubmissionResponse getStudentSubmission(String assignmentId, String studentId);
    
    SubmissionResponse gradeSubmission(String submissionId, Double score, String comment, String teacherId);

    AssignmentResponse updateAssignment(String id, AssignmentRequest request, String teacherId) throws IOException;
    void deleteAssignment(String id, String teacherId);
}
