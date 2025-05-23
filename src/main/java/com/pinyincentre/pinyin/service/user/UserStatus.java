package com.pinyincentre.pinyin.service.user;

public enum UserStatus {

    BAN(2, "Ban"),
    ACTIVE(1, "Active");

    private final int code;
    private final String statusName;

    private UserStatus(int code, String statusName) {
        this.code = code;
        this.statusName = statusName;
    }
    public int getCode() {
        return code;
    }
    public String getStatusName() {
        return statusName;
    }

    public static UserStatus fromCode(int code) {
        for (UserStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }

}
