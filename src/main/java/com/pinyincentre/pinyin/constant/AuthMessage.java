package com.pinyincentre.pinyin.constant;

public enum AuthMessage {
    INVALID_GRANT("Mã xác thực Google đã sử dụng hoặc hết hạn. Vui lòng thử lại."),
    INVALID_CODE("Phiên đăng nhập Google của bạn không hợp lệ, vui lòng thử lại."),
    STATE_EXPIRED("Phiên đăng nhập Google của bạn đã hết hạn, vui lòng thử lại."),
    LOGIN_FAILED_WITH_GOOGLE("Đăng nhập lỗi với Google! Vui lòng thử lại."),
    INVALID_CREDENTIALS("Sai tên đăng nhập hoặc mật khẩu"),
    USER_NOT_FOUND("Không tìm thấy tài khoản/email"),
    INVALID_PASSWORD("Sai mật khẩu"),
    USERNAME_EXISTS("Tên đăng nhập đã tồn tại"),
    EMAIL_EXISTS("Email đã tồn tại"),
    TOKEN_EXPIRED("Token đã hết hạn"),
    TOKEN_INVALID("Token không hợp lệ"),
    TOKEN_NOT_EXIST("Token không tồn tại"),
    UNAUTHORIZED("Không có quyền truy cập"),
    UNKNOWN_ERROR("Có lỗi xảy ra, vui lòng thử lại sau"),
    REGISTER_SUCCESSFULLY("Đăng kí thành công!"),
    GET_NEW_TOKEN_SUCCESSFULLY("Lấy token mới thành công"),
    GET_NEW_TOKEN_FAILED("Lấy token thất bại"),
    LOGOUT_SUCCESSFULLY("Đăng xuất thành công"),
    CHANGE_PASSWORD_SUCCESSFULLY("Đổi mật khẩu thành công"),
    INVALID_PASSWORD_CHANGE_PASSWORD("Mật khẩu không đúng, kiểm tra lại"),
    EQUAL_PASSWORD_CHANGE("Mật khẩu mới không được trùng với mật khẩu cũ"),
    FAILED_TOKEN("Token không hợp lệ hoặc đã hết hạn"),
    VALID_TOKEN("Token hợp lệ");

    private final String message;

    AuthMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}