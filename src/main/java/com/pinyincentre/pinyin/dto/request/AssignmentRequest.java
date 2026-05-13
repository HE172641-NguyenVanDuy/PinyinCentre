package com.pinyincentre.pinyin.dto.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    
    private String description;
    
    @NotNull(message = "Hạn nộp không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime deadline;
    
    @NotNull(message = "Điểm không được để trống")
    private Double totalScore;
    
    @NotBlank(message = "Lớp học không được để trống")
    private String classId;
    
    private MultipartFile file;
}
