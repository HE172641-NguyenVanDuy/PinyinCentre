package com.pinyincentre.pinyin.service.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateUtils {

    /**
     * Chuyển từ LocalDateTime về LocalDate (loại bỏ phần giờ phút)
     */
    public static LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }

    /**
     * Chuyển từ LocalDate sang LocalDateTime với thời gian là 00:00:00
     */
    public static LocalDateTime toStartOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    /**
     * Chuyển từ LocalDate sang LocalDateTime với thời gian là 23:59:59.999999999
     */
    public static LocalDateTime toEndOfDay(LocalDate date) {
        return date != null ? date.atTime(LocalTime.MAX) : null;
    }

    /**
     * Chuyển từ LocalDate sang LocalDateTime với một thời gian tùy ý
     */
    public static LocalDateTime toLocalDateTime(LocalDate date, LocalTime time) {
        return (date != null && time != null) ? date.atTime(time) : null;
    }
}