package com.pinyincentre.pinyin.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserUpdateRequest {

    //private String id;

//    @Size(min = 6, max = 30, message = "Username phải từ 6-30 ký tự")
//    @Pattern(regexp = "^[a-zA-Z0-9_.]+$", message = "Username chỉ được chứa chữ, số, dấu gạch dưới hoặc dấu chấm")
//    String username;
//
//    @Email(message = "Email không hợp lệ")
//    String email;

    @Pattern(regexp = "^(0\\d{9,10})$", message = "Số điện thoại phải bắt đầu bằng 0 và có từ 10-11 chữ số")
    private String phoneNumber;

    @Size(min = 2, max = 50, message = "Fullname phải từ 2-50 ký tự")
    private String fullName;

    @Past(message = "Ngày sinh không thể là ngày trong tương lai")
    private LocalDate dob;

    private String cic;

}
