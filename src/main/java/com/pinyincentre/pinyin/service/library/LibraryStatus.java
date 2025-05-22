package com.pinyincentre.pinyin.service.library;

public enum LibraryStatus {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");
    ;
    private final String statusName;

    LibraryStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }


}
