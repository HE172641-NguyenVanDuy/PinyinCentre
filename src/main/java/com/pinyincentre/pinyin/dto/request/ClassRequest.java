package com.pinyincentre.pinyin.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassRequest {

    @Size(min=3, max=150, message = "Tên lớp không được ít hơn 3 hoặc quá 150 ký tự.")
    @NotBlank(message = "Không được để trống tên lớp.")
    String className;

    @NotBlank(message = "Mã lớp không được trống.")
    String courseId;

    @NotBlank(message = "Mã giáo viên không được trống.")
    String teacherId;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @Min(value = 1, message = "Số lượng học sinh tối đa phải lớn hơn 0")
    private int maxStudents;
}
