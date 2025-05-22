package com.pinyincentre.pinyin.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class CourseRequest implements Serializable {


    @NotNull(message = "Course name cannot be null")
    @Size(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    private String courseName;

    @NotBlank(message = "Slot number must be filled.")
    private int slotNumber;

//    @NotNull(message = "Start time cannot be null")
//    @FutureOrPresent(message = "Start time must be in the present or future")
//    private LocalDateTime startDate;
//
//    @NotNull(message = "End time cannot be null")
//    @Future(message = "End time must be in the future")
//    private LocalDateTime endDate;
}
