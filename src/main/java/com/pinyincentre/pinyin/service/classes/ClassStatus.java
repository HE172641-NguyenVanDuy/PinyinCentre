package com.pinyincentre.pinyin.service.classes;

public enum ClassStatus {

    DElETE(1,true),
    ACTIVE(0,false)
    ;

    private final int code;
    private final boolean status;

    private ClassStatus(int code, boolean statusName) {
        this.code = code;
        this.status = statusName;
    }
    public int getCode() {
        return code;
    }
    public Boolean getStatus() {
        return status;
    }
}
