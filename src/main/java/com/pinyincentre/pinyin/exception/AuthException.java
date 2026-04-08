package com.pinyincentre.pinyin.exception;

public class AuthException extends RuntimeException{
    private Long code;
    private static final long serialVersionUID = -609099771903107222L;

    public AuthException(String message, String linkSocial) {
    }

    public Long getCode() {
        return this.code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public AuthException(String arg0) {
        super(arg0);
    }

    public AuthException(String arg0, Long code) {
        super(arg0);
        this.code = code;
    }
}

