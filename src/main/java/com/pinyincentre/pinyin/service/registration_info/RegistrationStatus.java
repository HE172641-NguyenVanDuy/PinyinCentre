package com.pinyincentre.pinyin.service.registration_info;

public enum RegistrationStatus {

    REGISTERED(1,"Registered"),
    NON_REGISTERED(0,"non-registered");
    ;

    private final int code;
    private final String statusName;

    RegistrationStatus(int code, String statusName) {
        this.code = code;
        this.statusName = statusName;
    }

    public int getCode() { return code; }
    public String getStatusName() { return statusName; }
}
