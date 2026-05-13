package com.pinyincentre.pinyin.service;

import com.pinyincentre.pinyin.dto.request.AssignmentRequest;
import com.pinyincentre.pinyin.dto.request.SubmissionRequest;
import com.pinyincentre.pinyin.dto.response.AssignmentResponse;
import com.pinyincentre.pinyin.dto.response.SubmissionResponse;
import com.pinyincentre.pinyin.entity.Assignment;
import com.pinyincentre.pinyin.entity.Submission;
import com.pinyincentre.pinyin.repository.AssignmentRepository;
import com.pinyincentre.pinyin.repository.SubmissionRepository;
import com.pinyincentre.pinyin.repository.UserRepository;
import com.pinyincentre.pinyin.service.classes.ClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final ClassService classService;

    @Override
    public AssignmentResponse createAssignment(AssignmentRequest request, String teacherId) throws IOException {
        String finalTeacherId = teacherId;
        
        // Nếu không lấy được teacherId từ Token (ví dụ lỗi claim), lấy từ thông tin lớp học
        if (finalTeacherId == null) {
            finalTeacherId = Optional.ofNullable(classService.getClassById(request.getClassId()))
                    .map(c -> c.getTeacherId())
                    .orElse(null);
        }
        
        if (finalTeacherId == null) {
            throw new RuntimeException("Không tìm thấy thông tin giáo viên cho lớp học này");
        }

        String fileUrl = null;
        String fileId = null;

        if (request.getFile() != null && !request.getFile().isEmpty()) {
            Map uploadResult = cloudinaryService.upload(request.getFile());
            fileUrl = (String) uploadResult.get("url");
            fileId = (String) uploadResult.get("public_id");
        }

        Assignment assignment = Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .totalScore(request.getTotalScore())
                .classId(request.getClassId())
                .teacherId(finalTeacherId)
                .fileUrl(fileUrl)
                .fileId(fileId)
                .build();

        assignment = assignmentRepository.save(assignment);
        return mapToAssignmentResponse(assignment, null);
    }

    @Override
    public List<AssignmentResponse> getAssignmentsByClass(String classId, String userId) {
        return assignmentRepository.findByClassId(classId).stream()
                .map(a -> {
                    String status = "Chưa nộp";
                    Submission submission = submissionRepository.findByAssignmentIdAndStudentId(a.getId(), userId).orElse(null);
                    if (submission != null) {
                        status = submission.getStatus() == Submission.SubmissionStatus.GRADED ? "Đã chấm" : "Đã nộp";
                    } else if (a.getDeadline() != null && LocalDateTime.now().isAfter(a.getDeadline())) {
                        status = "Quá hạn";
                    }
                    return mapToAssignmentResponse(a, status);
                })
                .collect(Collectors.toList());
    }

    @Override
    public AssignmentResponse getAssignmentById(String id, String userId) {
        Assignment a = assignmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Assignment not found"));
        String status = "Chưa nộp";
        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(a.getId(), userId).orElse(null);
        if (submission != null) {
            status = submission.getStatus() == Submission.SubmissionStatus.GRADED ? "Đã chấm" : "Đã nộp";
        } else if (a.getDeadline() != null && LocalDateTime.now().isAfter(a.getDeadline())) {
            status = "Quá hạn";
        }
        return mapToAssignmentResponse(a, status);
    }

    @Override
    public SubmissionResponse submitAssignment(SubmissionRequest request, String studentId) throws IOException {
        Map uploadResult = cloudinaryService.upload(request.getFile());
        String fileUrl = (String) uploadResult.get("url");
        String fileId = (String) uploadResult.get("public_id");

        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(request.getAssignmentId(), studentId)
                .orElse(new Submission());

        submission.setAssignmentId(request.getAssignmentId());
        submission.setStudentId(studentId);
        submission.setFileUrl(fileUrl);
        submission.setFileId(fileId);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(Submission.SubmissionStatus.SUBMITTED);

        submission = submissionRepository.save(submission);
        return mapToSubmissionResponse(submission);
    }

    @Override
    public List<SubmissionResponse> getSubmissionsByAssignment(String assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId).stream()
                .map(this::mapToSubmissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubmissionResponse getStudentSubmission(String assignmentId, String studentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .map(this::mapToSubmissionResponse)
                .orElse(null);
    }

    @Override
    public SubmissionResponse gradeSubmission(String submissionId, Double score, String comment, String teacherId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setScore(score);
        submission.setComment(comment);
        submission.setGradedAt(LocalDateTime.now());
        submission.setStatus(Submission.SubmissionStatus.GRADED);

        submission = submissionRepository.save(submission);
        return mapToSubmissionResponse(submission);
    }

    @Override
    public AssignmentResponse updateAssignment(String id, AssignmentRequest request, String teacherId) throws IOException {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập"));

        if (!assignment.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Bạn không có quyền sửa bài tập này");
        }

        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDeadline(request.getDeadline());
        assignment.setTotalScore(request.getTotalScore());

        if (request.getFile() != null && !request.getFile().isEmpty()) {
            // Delete old file if exists
            if (assignment.getFileId() != null) {
                try {
                    cloudinaryService.delete(assignment.getFileId());
                } catch (Exception e) {
                    // Log error but continue
                    System.err.println("Lỗi khi xóa file cũ trên Cloudinary: " + e.getMessage());
                }
            }
            
            Map uploadResult = cloudinaryService.upload(request.getFile());
            assignment.setFileUrl((String) uploadResult.get("url"));
            assignment.setFileId((String) uploadResult.get("public_id"));
        }

        assignment = assignmentRepository.save(assignment);
        return mapToAssignmentResponse(assignment, null);
    }

    @Override
    public void deleteAssignment(String id, String teacherId) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập"));

        if (!assignment.getTeacherId().equals(teacherId)) {
            log.error("Ownership check failed: assignment teacherId = {}, requester teacherId = {}", 
                    assignment.getTeacherId(), teacherId);
            throw new RuntimeException("Bạn không có quyền xóa bài tập này");
        }

        // Delete submissions first to avoid FK constraint issues
        List<Submission> submissions = submissionRepository.findByAssignmentId(id);
        for (Submission s : submissions) {
            if (s.getFileId() != null) {
                try {
                    cloudinaryService.delete(s.getFileId());
                } catch (Exception e) {
                    System.err.println("Lỗi khi xóa file bài nộp trên Cloudinary: " + e.getMessage());
                }
            }
        }
        submissionRepository.deleteAll(submissions);

        // Delete assignment file
        if (assignment.getFileId() != null) {
            try {
                cloudinaryService.delete(assignment.getFileId());
            } catch (Exception e) {
                System.err.println("Lỗi khi xóa file bài tập trên Cloudinary: " + e.getMessage());
            }
        }

        assignmentRepository.delete(assignment);
    }

    private AssignmentResponse mapToAssignmentResponse(Assignment a, String status) {
        return AssignmentResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .fileUrl(a.getFileUrl())
                .fileId(a.getFileId())
                .deadline(a.getDeadline())
                .totalScore(a.getTotalScore())
                .classId(a.getClassId())
                .teacherId(a.getTeacherId())
                .createdDate(a.getCreatedDate())
                .status(status)
                .build();
    }

    private SubmissionResponse mapToSubmissionResponse(Submission s) {
        String studentName = userRepository.findById(s.getStudentId())
                .map(u -> u.getFullName())
                .orElse("Unknown");

        return SubmissionResponse.builder()
                .id(s.getId())
                .assignmentId(s.getAssignmentId())
                .studentId(s.getStudentId())
                .studentName(studentName)
                .fileUrl(s.getFileUrl())
                .fileId(s.getFileId())
                .submittedAt(s.getSubmittedAt())
                .score(s.getScore())
                .comment(s.getComment())
                .feedbackFileUrl(s.getFeedbackFileUrl())
                .gradedAt(s.getGradedAt())
                .status(s.getStatus().name())
                .build();
    }
}
