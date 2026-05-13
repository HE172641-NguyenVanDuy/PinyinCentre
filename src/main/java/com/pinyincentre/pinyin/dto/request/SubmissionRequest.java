package com.pinyincentre.pinyin.dto.request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionRequest {
    @NotBlank(message = "ID bài tập không được để trống")
    private String assignmentId;
    
    @NotNull(message = "File bài làm không được để trống")
    private MultipartFile file;
}
