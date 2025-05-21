package com.pinyincentre.pinyin.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RegistrationInfoRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ và tên phải có từ 2 đến 100 ký tự")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0\\d{9,10})$", message = "Số điện thoại phải bắt đầu bằng 0 và có từ 10-11 chữ số")
    private String phoneNumber;

    @NotNull(message = "Mã khóa học không được để trống")
    private String courseId;
}
