package com.pinyincentre.pinyin.service.course;

import com.pinyincentre.pinyin.dto.request.CourseRequest;
import com.pinyincentre.pinyin.dto.response.CourseResponse;
import com.pinyincentre.pinyin.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CourseMapper {


    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    Course toCourse(CourseRequest request);
    CourseResponse toCourseResponse(Course course);
    default Timestamp map(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
    }

    default LocalDateTime map(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
