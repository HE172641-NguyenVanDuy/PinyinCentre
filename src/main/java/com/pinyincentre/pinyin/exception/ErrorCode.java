package com.pinyincentre.pinyin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {

    SAVE_CLASS_ERROR(5002, "Lớp không được lưu thành công !", HttpStatus.BAD_REQUEST),
    CLASS_NOT_FOUND(5001, "Không tìm thấy lớp !", HttpStatus.BAD_REQUEST),
    BAN_USER_FAIL(4007, "Giới hạn tài khoản không thành công!", HttpStatus.OK),
    BAN_USER_SUCCESS(4006, "Giới hạn tài khoản thành công!", HttpStatus.OK),
    USER_ID_EMPTY(4005,"Username người dùng bị trống!",HttpStatus.BAD_REQUEST),
    EXIST_USERNAME(4003,"Username này đã tồn tại tài khoản!",HttpStatus.BAD_REQUEST),
    EXIST_EMAIL(4004,"Email này đã tồn tại tài khoản!",HttpStatus.BAD_REQUEST),
    CREATE_USER_FAIL(4002,"Tạo tài khoản người dùng không thành công!",HttpStatus.BAD_REQUEST),
    CREATE_USER(4001,"Tạo tài khoản người dùng thành công!",HttpStatus.OK),
    CREATE_COURSE_FAIL(3002,"Tạo khóa học mới không thành công!",HttpStatus.BAD_REQUEST),
    CREATE_COURSE_SUCCESS(3001,"Tạo khóa học mới thành công!",HttpStatus.CREATED),
    FAIL_CHANGE_IS_REGISTERED(2004,"Phê duyệt thông tin đăng ký không thành công !", HttpStatus.BAD_REQUEST),
    CHANGE_IS_REGISTERED(2003,"Thông tin đăng ký đã được phê duyệt", HttpStatus.OK),
    REGISTRATION_NOTNULL_FIELD(2002,"Các trường truyền vào không được để trống!", HttpStatus.BAD_REQUEST),
    REGISTRATION_FAIL(2001,"Đăng kí tư vấn không thành công !", HttpStatus.OK),
    REGISTRATION_SUCCESS(2000,"Đăng kí tư vấn thành công !", HttpStatus.CREATED),
    NOT_FOUND(1005, "Not found !", HttpStatus.NOT_FOUND),
    SUCCESS(1000,"SUCCESS", HttpStatus.OK);

    private final int code;
    private final String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
