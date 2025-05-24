package com.pinyincentre.pinyin.service.utils;

import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class DateMapper {
    @Named("toLocalDateTime")
    public static LocalDateTime toLocalDateTime(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }
}
