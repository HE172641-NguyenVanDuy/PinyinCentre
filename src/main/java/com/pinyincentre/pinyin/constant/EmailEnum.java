package com.pinyincentre.pinyin.constant;

public enum EmailEnum {
    SUBJECT_CHANGE_PASSWORD("[PinyinCentre] Yêu cầu đặt lại mật khẩu của bạn"),
    SUBJECT_ACTIVE_ACCOUNT("[PinyinCentre] Kích hoạt tài khoản"),
    WITHDRAW_MONEY("Xác nhận yêu cầu rút tiền"),
    TEMPLATE_CHANGE_PASSWORD("templates/ForgotPassword.html"),
    TEMPALTE_WITHDRAW_MONEY("templates/withdraw-confirm.html"),
    TEMPLATE_ACTIVE_ACCOUNT("templates/ActiveAccount.html"),
    SEND_EMAIL_SUCCESSFULLY_REGISTER("templates/PhotographerStudio.html"),
    SEND_EMAIL_SUCCESSFULLY("Gửi email thành công");

    private final String message;
    EmailEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

