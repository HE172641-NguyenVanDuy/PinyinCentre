package com.pinyincentre.pinyin.service.course;

public enum CourseStatus {

    ACTIVE(false,"Active"),;

    private final boolean code;
    private final String statusName;

    CourseStatus(boolean code, String statusName) {
        this.code = code;
        this.statusName = statusName;
    }

    public boolean getCode() {  return code; }
    public String getStatusName() { return statusName; }
}
